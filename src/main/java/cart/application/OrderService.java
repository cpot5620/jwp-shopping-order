package cart.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cart.dao.OrderDao;
import cart.dao.OrderItemDao;
import cart.dao.dto.OrderDto;
import cart.dao.dto.OrderItemDto;
import cart.domain.Cart;
import cart.domain.CartItem;
import cart.domain.Member;
import cart.domain.MemberCoupon;
import cart.domain.Order;
import cart.domain.OrderItem;
import cart.domain.Product;
import cart.dto.MemberCouponRequest;
import cart.dto.OrderItemRequest;
import cart.dto.OrderRequest;

@Service
public class OrderService {

    private final CartService cartService;
    private final CartItemService cartItemService;
    private final OrderItemDao orderItemDao;
    private final OrderDao orderDao;
    private final MemberService memberService;
    private final CouponService couponService;

    public OrderService(CartService cartService, CartItemService cartItemService, OrderItemDao orderItemDao,
            OrderDao orderDao, MemberService memberService, CouponService couponService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.orderItemDao = orderItemDao;
        this.orderDao = orderDao;
        this.memberService = memberService;
        this.couponService = couponService;
    }

    @Transactional
    public void order(Member member, OrderRequest request) {
        Cart cart = cartService.getCartOf(member);

        Order order = new Order(member);
        for (OrderItemRequest orderItemRequest : request.getOrderItems()) {
            CartItem itemToOrder = cartItemService.getItemBy(orderItemRequest.getId());
            List<MemberCoupon> memberCouponsToUse = getMemberCouponsBy(member, orderItemRequest);

            cart.applyCouponsOn(itemToOrder, memberCouponsToUse);
            Order couponAppliedOrder = cart.order(itemToOrder);
            order = order.join(couponAppliedOrder);
        }

        save(order);
        cartService.save(cart);
    }

    private List<MemberCoupon> getMemberCouponsBy(Member member, OrderItemRequest orderItemRequest) {
        return couponService.getMemberCouponsBy(
                member,
                orderItemRequest.getCoupons().stream()
                        .map(MemberCouponRequest::getCouponId)
                        .collect(Collectors.toList())
        );
    }

    public List<Order> getBy(Member owner) {
        return orderDao.selectAllBy(owner.getId()).stream()
                .map(OrderDto::getId)
                .map(id -> getBy(owner, id))
                .collect(Collectors.toList());
    }

    public Order getBy(Member owner, Long id) {
        OrderDto orderDto = orderDao.selectBy(id);
        return new Order(
                orderDto.getId(),
                memberService.getMemberBy(orderDto.getMemberId()),
                toOrderItems(owner, orderItemDao.selectAllOf(id)),
                orderDto.getCreatedAt()
        );
    }

    private void save(Order order) {
        couponService.updateMemberCoupons(order.getUsedMemberCoupons());
        Long orderId = orderDao.insert(OrderDto.of(order));
        orderItemDao.insertAll(orderId, order.getOrderItems());
    }

    private List<OrderItem> toOrderItems(Member owner, List<OrderItemDto> orderItemDtos) {
        return orderItemDtos.stream()
                .map(dto -> toOrderItem(owner, dto))
                .collect(Collectors.toList());
    }

    private OrderItem toOrderItem(Member owner, OrderItemDto orderItemDto) {
        return new OrderItem(
                orderItemDto.getId(),
                new Product(
                        orderItemDto.getOrderedProductId(),
                        orderItemDto.getOrderedProductName(),
                        orderItemDto.getOrderedProductPrice(),
                        orderItemDto.getOrderedProductImageUrl()
                ),
                orderItemDto.getQuantity(),
                couponService.getMemberCouponsBy(owner, orderItemDto.getMemberCouponIds())
        );
    }
}
