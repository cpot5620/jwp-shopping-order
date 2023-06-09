package cart.application;

import cart.application.event.DefaultMemberPointUpdateEvent;
import cart.application.event.DefaultPaymentRequestEvent;
import cart.domain.cart.Cart;
import cart.domain.member.Member;
import cart.domain.order.Order;
import cart.domain.order.OrderItem;
import cart.domain.order.Orders;
import cart.domain.product.Price;
import cart.dto.request.OrderRequest;
import cart.dto.response.OrderResponse;
import cart.repository.CartRepository;
import cart.repository.OrderRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderService(
            final OrderRepository orderRepository,
            final CartRepository cartRepository,
            final ApplicationEventPublisher applicationEventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Long addOrder(final Member member, final OrderRequest request) {
        final Cart cart = cartRepository.findByIds(request.getCartItemIds());

        cart.checkOwner(member);

        requestPayment(member, request, cart);

        final Order order = cartToOrder(member, cart);
        final Long orderId = orderRepository.save(order);

        cartRepository.deleteCart(cart);

        requestPointUpdate(member, request, orderId);

        return orderId;
    }

    private void requestPayment(final Member member, final OrderRequest request, final Cart cart) {
        final Price totalPrice = cart.getTotalPrice();
        applicationEventPublisher.publishEvent(new DefaultPaymentRequestEvent(member, totalPrice, request));
    }

    private Order cartToOrder(final Member member, final Cart cart) {
        return new Order(member, cart.getCartItems()
                .stream()
                .map(it -> new OrderItem(it.getQuantity(), it.getProduct()))
                .collect(Collectors.toUnmodifiableList())
        );
    }

    private void requestPointUpdate(final Member member, final OrderRequest request, final Long orderId) {
        applicationEventPublisher.publishEvent(new DefaultMemberPointUpdateEvent(member, request, orderId));
    }

    @Transactional(readOnly = true)
    public OrderResponse findByOrderId(final Member member, final Long orderId) {
        final Order order = orderRepository.findByOrderId(orderId);

        order.checkOwner(member);

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findMemberOrders(final Member member) {
        final Orders orders = orderRepository.findByMember(member);

        orders.checkOwner(member);

        return orders.getOrders().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toUnmodifiableList());
    }
}
