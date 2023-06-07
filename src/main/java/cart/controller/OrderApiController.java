package cart.controller;

import cart.auth.Auth;
import cart.domain.member.Member;
import cart.dto.OrderRequest;
import cart.dto.OrderResponse;
import cart.dto.PageRequest;
import cart.dto.PagingOrderResponse;
import cart.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> addOrder(@Auth final Member member, @Valid @RequestBody final OrderRequest orderRequest) {
        final Long orderId = orderService.addOrder(member, orderRequest);
        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    @GetMapping
    public ResponseEntity<PagingOrderResponse> getAllOrders(@Auth final Member member, final PageRequest pageRequest) {
        final PagingOrderResponse response = orderService.getAllOrders(member, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@Auth final Member member, @PathVariable final Long orderId) {
        final OrderResponse response = orderService.getOrderById(member, orderId);
        return ResponseEntity.ok(response);
    }
}
