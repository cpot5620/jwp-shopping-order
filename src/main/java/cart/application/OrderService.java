package cart.application;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import cart.domain.CartItem;
import cart.domain.MemberCoupon;
import cart.domain.Order;
import cart.domain.OrderItem;
import cart.domain.Product;
import cart.dto.request.OrderCouponRequest;
import cart.dto.request.OrderItemRequest;
import cart.dto.request.OrderRequest;
import cart.dto.response.OrderResponse;
import cart.repository.CartItemRepository;
import cart.repository.CouponRepository;
import cart.repository.OrderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CouponRepository couponRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(OrderRepository orderRepository, CouponRepository couponRepository,
                        CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.couponRepository = couponRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Long order(OrderRequest request, Long memberId) {
        List<MemberCoupon> memberCoupons = getMemberCoupons(request);
        Map<Long, MemberCoupon> memberCouponsById = memberCoupons.stream()
                .collect(toMap(MemberCoupon::getId, Function.identity()));
        List<OrderItem> orderItems = getOrderItems(request, memberCouponsById);
        couponRepository.useCoupons(memberCoupons);
        clearCartItems(request, memberId);

        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
        return orderRepository.save(new Order(memberId, orderItems, totalPrice, request.getDeliveryFee()));
    }

    private List<MemberCoupon> getMemberCoupons(OrderRequest request) {
        List<Long> couponIds = request.getOrderItems().stream()
                .flatMap(orderItemRequest -> orderItemRequest.getCoupons().stream())
                .map(OrderCouponRequest::getCouponId)
                .collect(toList());
        return couponRepository.findAllByMemberCouponIds(couponIds);
    }

    private void clearCartItems(OrderRequest request, Long memberId) {
        List<CartItem> cartItems = request.getOrderItems().stream()
                .map(it -> new CartItem(it.getId(), it.getQuantity(), it.getProduct().toDomain(), memberId))
                .collect(toList());
        cartItemRepository.clear(cartItems);
    }

    private List<OrderItem> getOrderItems(OrderRequest request, Map<Long, MemberCoupon> memberCouponsById) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest orderItemRequest : request.getOrderItems()) {
            Product product = orderItemRequest.getProduct().toDomain();
            List<MemberCoupon> memberCoupons = new ArrayList<>();
            for (OrderCouponRequest coupon : orderItemRequest.getCoupons()) {
                memberCoupons.add(memberCouponsById.get(coupon.getCouponId()));
            }

            int totalPrice = applyCoupon(orderItemRequest, product, memberCoupons);

            OrderItem orderItem = new OrderItem(product, orderItemRequest.getQuantity(), memberCoupons, totalPrice);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private int applyCoupon(OrderItemRequest orderItemRequest, Product product, List<MemberCoupon> memberCoupons) {
        int price = product.getPrice() * orderItemRequest.getQuantity();
        for (MemberCoupon memberCoupon : memberCoupons) {
            price = memberCoupon.apply(price);
        }
        return price;
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long orderId) {
        Order order = orderRepository.findById(orderId);
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAllByMemberId(Long memberId) {
        List<Order> orders = orderRepository.findAllByMemberId(memberId);
        return orders.stream()
                .map(OrderResponse::from)
                .collect(toList());
    }
}
