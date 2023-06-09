package cart.domain;

import static fixture.OrdersProductFixture.주문_상품_치킨_2개;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderProductTest {

    @Test
    @DisplayName("OrderProduct 의 수량과 가격을 이용해 값을 계산한다.")
    void getTotalPrice() {
        int totalPrice = 주문_상품_치킨_2개.getTotalPrice();

        assertThat(totalPrice).isEqualTo(20000);
    }

}
