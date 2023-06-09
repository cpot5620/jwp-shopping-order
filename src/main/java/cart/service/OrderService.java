package cart.service;

import cart.dao.CartItemDao;
import cart.domain.Member;
import cart.domain.order.Order;
import cart.domain.order.OrderItem;
import cart.domain.point.Point;
import cart.domain.point.PointPolicyStrategy;
import cart.domain.shipping.ShippingDiscountPolicy;
import cart.domain.shipping.ShippingFee;
import cart.dto.order.OrderCreateResponse;
import cart.dto.order.OrderRequest;
import cart.dto.order.OrderResponse;
import cart.dto.order.OrdersResponse;
import cart.repository.OrderRepository;
import cart.repository.PointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemDao cartItemDao;
    private final PointRepository pointRepository;
    private final PointPolicyStrategy pointPolicyStrategy;

    public OrderService(OrderRepository orderRepository, CartItemDao cartItemDao, PointRepository pointRepository, PointPolicyStrategy pointPolicyStrategy) {
        this.orderRepository = orderRepository;
        this.cartItemDao = cartItemDao;
        this.pointRepository = pointRepository;
        this.pointPolicyStrategy = pointPolicyStrategy;
    }

    @Transactional
    public OrderCreateResponse createOrder(final Member member, final OrderRequest orderRequest) {
        final List<OrderItem> orderItemList = orderRequest.getOrder().stream()
                .map(orderItemDto -> new OrderItem(cartItemDao.findById(orderItemDto.getCartItemId()).getProduct(), orderItemDto.getQuantity()))
                .collect(toList());
        final ShippingFee shippingFee = orderRepository.findShippingFee();
        final ShippingDiscountPolicy shippingDiscountPolicy = orderRepository.findShippingDiscountPolicy();

        final Order newOrder = Order.of(member, shippingFee, orderItemList, shippingDiscountPolicy.getThreshold(), new Point(orderRequest.getUsedPoint()));
        checkOrderException(orderRequest, newOrder);

        final Long orderId = orderRepository.saveOrder(member, newOrder);
        final Point memberPoint = pointRepository.findPointByMemberId(member.getId());
        Long minus = memberPoint.minus(orderRequest.getUsedPoint());
        pointRepository.updatePoint(member.getId(), minus);
        System.out.println("minus"+minus);
        final Long earnedPoint = pointPolicyStrategy.caclulatePointWithPolicy(newOrder.getTotalPrice() - orderRequest.getUsedPoint());
        System.out.println("earnedPoint"+earnedPoint);
        pointRepository.updatePoint(member.getId(), earnedPoint);

        return new OrderCreateResponse(orderId, earnedPoint);
    }

    private static void checkOrderException(OrderRequest orderRequest, Order newOrder) {
        newOrder.checkSameTotalPrice(orderRequest.getTotalProductsPrice());
        newOrder.checkMinusOrderPrice(orderRequest.getUsedPoint());
    }

    public List<OrdersResponse> findAllOrdersByMember(final Member member) {
        final List<Order> orders = orderRepository.findAllOrdersByMember(member);
        return orders.stream()
                .map(OrdersResponse::from)
                .collect(toList());
    }

    public OrderResponse findOrderDetailsByOrderId(final Member member, final Long orderId) {
        final Order order = orderRepository.findOrderById(member, orderId);
        return OrderResponse.from(order);
    }

}
