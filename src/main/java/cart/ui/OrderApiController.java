package cart.ui;

import cart.application.OrderService;
import cart.domain.Member;
import cart.dto.OrderListResponse;
import cart.dto.OrderRequest;
import cart.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;


@RestController
@RequestMapping("/orders")
public class OrderApiController {
    public static final String MAXIMUM_BIGINT = "9223372036854775807";
    
    private final OrderService orderService;

    public OrderApiController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Void> addOrder(final Member member,
                                         @RequestBody @Valid final OrderRequest orderRequest) {
        final Long orderId = orderService.add(member, orderRequest);
        return ResponseEntity.created(URI.create("/orders/" + orderId)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> showOrderById(final Member member,
                                                       @PathVariable final Long id) {
        return ResponseEntity.ok(orderService.findById(member, id));
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> showOrders(final Member member,
                                                        @RequestParam(value = "last-id", defaultValue = MAXIMUM_BIGINT) final Long idx) {
        return ResponseEntity.ok(orderService.findPageByIndex(member, idx));
    }

}
