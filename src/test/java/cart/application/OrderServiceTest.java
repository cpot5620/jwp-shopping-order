package cart.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import cart.domain.Member;
import cart.dto.request.CartItemRequest;
import cart.dto.request.OrderCreateRequest;
import cart.dto.response.OrderResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

class OrderServiceTest extends ServiceTest {

    @Test
    void 주문시_포인트_사용을_확인한다() {
        final var request = new OrderCreateRequest(200, List.of(
                new CartItemRequest(1L, 1L, 2),
                new CartItemRequest(2L, 2L, 4)
        ));
        final Long id = orderService.createOrder(request, Member.of(1L, "a@a.com", "1234", 1000));
        final OrderResponse response = orderService.findById(id, Member.of(1L, "a@a.com", "1234", 1000));
        final Member member = memberDao.findMemberById(1L);

        assertAll(
                () -> assertThat(member.getPoints()).isEqualTo(10780),
                () -> assertThat(response.getUsedPoints()).isEqualTo(200)
        );
    }

}
