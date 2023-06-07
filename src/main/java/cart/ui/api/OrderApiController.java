package cart.ui.api;

import cart.application.OrderService;
import cart.domain.Member;
import cart.dto.OrderRequest;
import cart.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("/orders")
@Controller
public final class OrderApiController {
    private final OrderService orderService;

    public OrderApiController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> order(final Member member,
                                      @RequestBody final OrderRequest request) {
        final Long orderId = orderService.save(member, request);

        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll(final Member member) {
        final List<OrderResponse> orders = orderService.findAllByMember(member);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findByOrderId(final Member member, @PathVariable Long id) {
        final OrderResponse order = orderService.findById(id);

        return ResponseEntity.ok(order);
    }
}
