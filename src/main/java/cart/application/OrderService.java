package cart.application;

import cart.dao.CartItemDao;
import cart.dao.ProductDao;
import cart.domain.Member;
import cart.domain.Order;
import cart.domain.OrderItem;
import cart.domain.Point;
import cart.domain.Product;
import cart.dto.response.OrderDetailResponse;
import cart.dto.request.OrderItemCreateRequest;
import cart.dto.response.OrderItemResponse;
import cart.dto.request.OrderCreateRequest;
import cart.dto.response.OrderResponse;
import cart.exception.ProductNotFoundException;
import cart.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PointService pointService;
    private final ProductDao productDao;
    private final CartItemDao cartItemDao;

    public OrderService(OrderRepository orderRepository, PointService pointService, ProductDao productDao,
                        CartItemDao cartItemDao) {
        this.orderRepository = orderRepository;
        this.pointService = pointService;
        this.productDao = productDao;
        this.cartItemDao = cartItemDao;
    }

    @Transactional
    public Long createOrder(OrderCreateRequest orderCreateRequest, Member member) {
        List<OrderItem> orderItems = createOrderItems(orderCreateRequest);
        Point spendPoint = new Point(orderCreateRequest.getSpendPoint());
        Order order = new Order(null, member, orderItems, spendPoint.getAmount(), LocalDateTime.now());

        pointService.decreasePoint(member, spendPoint);
        Order saveOrder = orderRepository.save(order);
        pointService.increasePoint(member, saveOrder.calculateRewardPoint(10));
        deleteCartItems(member, orderItems);
        return saveOrder.getId();
    }

    private List<OrderItem> createOrderItems(OrderCreateRequest orderCreateRequest) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemCreateRequest orderItemCreateRequest : orderCreateRequest.getOrderItems()) {
            Long productId = orderItemCreateRequest.getProductId();
            int quantity = orderItemCreateRequest.getQuantity();
            Product product = findProduct(productId);
            OrderItem orderItem = new OrderItem(null, product, quantity, product.getPrice() * quantity);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private Product findProduct(Long productId) {
        return productDao.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("해당 상품을 찾을 수 없습니다."));
    }

    private void deleteCartItems(Member member, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            cartItemDao.deleteByMemberAndProduct(member.getId(), orderItem.getProduct().getId());
        }
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse findOrderById(Long orderId, Member member) {
        Order order = orderRepository.findById(orderId);
        order.checkOwner(member);

        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());

        return new OrderDetailResponse(
                order.getId(),
                order.calculateTotalPrice().getAmount(),
                order.getSpendPoint().getAmount(),
                order.calculateSpendPrice().getAmount(),
                order.getCreatedAt(),
                orderItemResponses
        );
    }

    private OrderItemResponse convertToItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getProduct().getImageUrl(),
                orderItem.getPrice().getAmount(),
                orderItem.getQuantity().getAmount()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAllOrders(Member member) {
        List<Order> orders = orderRepository.findAllByMember(member);
        return orders.stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getThumbnailUrl(),
                order.getFirstProductName(),
                order.getOrderItemCount(),
                order.calculateSpendPrice().getAmount(),
                order.getCreatedAt()
        );
    }
}
