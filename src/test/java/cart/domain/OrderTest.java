package cart.domain;

import cart.Fixture;
import cart.domain.pointmanager.DefaultPointPolicy;
import cart.exception.OrderEmptyException;
import cart.exception.PointBiggerThenLimitException;
import cart.exception.PointNotEnoughException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OrderTest {

    @DisplayName("주문을 생성한다")
    @Test
    void generate() {
        final Order actual = Order.of(Fixture.memberA, 0, List.of(Fixture.cartItem1), new DefaultPointPolicy());

        assertAll(
                () -> assertThat(actual.getMemberId()).isEqualTo(Fixture.memberA.getId()),
                () -> assertThat(actual.getOrderItems().size()).isEqualTo(1),
                () -> assertThat(actual.getTotalPrice()).isEqualTo(3000),
                () -> assertThat(actual.getPayPrice()).isEqualTo(3000),
                () -> assertThat(actual.getEarnedPoints()).isEqualTo(300),
                () -> assertThat(actual.getUsedPoints()).isEqualTo(0)
        );
    }

    @DisplayName("사용 포인트가 이용자가 보유한 포인트보다 크면 예외가 발생한다")
    @Test
    void pointNotEnoughFail() {
        assertThatThrownBy(() -> Order.of(Fixture.memberA, 1, List.of(Fixture.cartItem1), new DefaultPointPolicy()))
                .isInstanceOf(PointNotEnoughException.class);
    }

    @DisplayName("사용 포인트가 이용자가 사용 가능 최대 포인트보다크면 예외가 발생한다")
    @Test
    void pointBiggerThenLimitFail() {
        final Member member = new Member(2L, "a@a.com", "1234", 1000);

        assertDoesNotThrow(() -> Order.of(member, 300, List.of(Fixture.cartItem1), new DefaultPointPolicy()));
        assertThatThrownBy(() -> Order.of(member, 301, List.of(Fixture.cartItem1), new DefaultPointPolicy()))
                .isInstanceOf(PointBiggerThenLimitException.class);
    }


    @DisplayName("주문한 상품의 개수가 0이면 예외가 발생한다")
    @Test
    void cartItemSizeFail() {
        assertThatThrownBy(() -> Order.of(Fixture.memberA, 0, List.of(), new DefaultPointPolicy()))
                .isInstanceOf(OrderEmptyException.class);
    }
}
