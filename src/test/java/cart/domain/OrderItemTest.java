package cart.domain;

import static cart.fixture.TestFixture.CART_ITEM_샐러드_MEMBER_A;
import static cart.fixture.TestFixture.MEMBER_A_COUPON_FIXED_2000;
import static cart.fixture.TestFixture.MEMBER_A_COUPON_PERCENTAGE_50;
import static cart.fixture.TestFixture.ORDERED_샐러드;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderItemTest {
    @Test
    void 총액을_셀_수_있다() {
        assertThat(ORDERED_샐러드.getTotal()).isEqualTo(new Money(200_000));
    }

    @Test
    void 총액에는_쿠폰이_적용된다() {
        CartItem cartItem = CART_ITEM_샐러드_MEMBER_A();
        cartItem.apply(List.of(MEMBER_A_COUPON_PERCENTAGE_50(), MEMBER_A_COUPON_FIXED_2000()));

        OrderItem ordered = new OrderItem(cartItem);

        assertThat(ordered.getTotal()).isEqualTo(new Money(98000));
    }
}
