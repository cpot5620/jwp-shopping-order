package cart.application;

import cart.domain.Coupon;
import cart.domain.Member;
import cart.domain.MemberCoupon;
import cart.domain.Order;
import cart.domain.OrderItem;
import cart.domain.Product;
import cart.domain.repository.CartItemRepository;
import cart.domain.repository.MemberCouponRepository;
import cart.domain.repository.OrderRepository;
import cart.domain.repository.ProductRepository;
import cart.domain.vo.Amount;
import cart.dto.request.OrderRequest;
import cart.dto.response.OrderDetailResponse;
import cart.dto.response.OrderProductResponse;
import cart.dto.response.OrderResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(final OrderRepository orderRepository, final ProductRepository productRepository,
                        final MemberCouponRepository memberCouponRepository,
                        final CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.memberCouponRepository = memberCouponRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public OrderDetailResponse order(final OrderRequest orderRequest, final Member member) {
        final List<OrderItem> orderItems = findOrderItems(orderRequest);
        final MemberCoupon coupon = findCouponIfExists(member.getId(), orderRequest.getCouponId());
        final Order order = orderRepository.create(
                new Order(orderItems, coupon, Amount.of(orderRequest.getTotalProductAmount()),
                        Amount.of(orderRequest.getDeliveryAmount()), orderRequest.getAddress()), member.getId());
        final List<Long> productIds = orderItems.stream()
                .map(it -> it.getProduct().getId())
                .collect(Collectors.toList());
        cartItemRepository.deleteAllByMemberIdAndProductId(member.getId(), productIds);
        final int discountedProductAmount = order.discountProductAmount().getValue();
        useCoupon(member, coupon, orderRequest.getCouponId());
        return new OrderDetailResponse(order.getId(), orderRequest.getTotalProductAmount(),
                order.getDeliveryAmount().getValue(), discountedProductAmount, order.getAddress(),
                makeOrderProductResponses(orderItems));
    }

    private void useCoupon(final Member member, final MemberCoupon coupon, final Long requestCouponId) {
        if (requestCouponId == null) {
            return;
        }
        coupon.use();
        memberCouponRepository.update(coupon, member.getId());
    }

    private MemberCoupon findCouponIfExists(final Long memberId, final Long requestCouponId) {
        if (requestCouponId != null) {
            return memberCouponRepository.findByCouponIdAndMemberId(requestCouponId, memberId);
        }
        return new MemberCoupon(memberId, Coupon.empty());
    }

    private List<OrderItem> findOrderItems(final OrderRequest orderRequest) {
        return orderRequest.getProducts().stream()
                .map(it -> {
                    final Product product = productRepository.findById(it.getId());
                    return new OrderItem(product, it.getQuantity());
                }).collect(Collectors.toList());
    }

    private List<OrderProductResponse> makeOrderProductResponses(final List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(it -> new OrderProductResponse(it.getProduct().getId(), it.getProduct().getName(),
                        it.getProduct().getAmount().getValue(), it.getProduct().getImageUrl(), it.getQuantity()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse showOrderDetail(final Member member, final Long id) {
        final Order order = orderRepository.findById(id, member.getId());

        final List<OrderProductResponse> orderProductResponses = getOrderProductResponses(order);
        return new OrderDetailResponse(order.getId(), order.getTotalProductAmount().getValue(),
                order.getDeliveryAmount().getValue(), order.discountProductAmount().getValue(), order.getAddress(),
                orderProductResponses);
    }

    private List<OrderProductResponse> getOrderProductResponses(final Order order) {
        return order.getOrderItems().stream()
                .map(it -> new OrderProductResponse(it.getProduct().getId(), it.getProduct().getName(),
                        it.getProduct().getAmount().getValue(), it.getProduct().getImageUrl(), it.getQuantity()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> showAllOrders(final Member member) {
        final List<Order> orders = orderRepository.findAll(member.getId());
        return orders.stream()
                .map(it -> new OrderResponse(it.getId(), getOrderProductResponses(it)))
                .collect(Collectors.toList());
    }
}
