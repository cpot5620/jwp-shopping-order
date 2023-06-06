package cart.domain;

import cart.exception.CartItemException;
import cart.exception.CouponException;
import cart.exception.OrderException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static cart.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class OrderTest {

    /*
        치킨 = 10000원
        피자 = 20000원
     */
    @Test
    void 주문_전체_금액을_계산한다() {
        Order order = Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 밀리_쿠폰_10퍼센트);

        Money totalPrice = order.calculateTotalPrice();

        assertThat(totalPrice).isEqualTo(new Money(111000));
    }

    @Test
    void 쿠폰을_적용하지_않는_주문의_전체_금액을_계산한다() {
        Order order = Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 가짜_쿠폰);

        Money totalPrice = order.calculateTotalPrice();

        assertThat(totalPrice).isEqualTo(new Money(123000));
    }

    @Test
    void 다른_사용자의_쿠폰으로_주문을_할_때_예외가_발생한다() {
        assertThatThrownBy(() -> Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 박스터_쿠폰_10퍼센트))
                .isInstanceOf(CouponException.class);
    }

    @Test
    void 만료기간이_지난_쿠폰으로_주문을_할_때_예외가_발생한다() {
        assertThatThrownBy(() -> Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 밀리_만료기간_지난_쿠폰_10퍼센트))
                .isInstanceOf(CouponException.class);
    }

    @Test
    void 다른_사용자의_장바구니_상품으로_주문을_할_때_예외가_발생한다() {
        assertThatThrownBy(() -> Order.of(박스터, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 박스터_쿠폰_10퍼센트))
                .isInstanceOf(CartItemException.class);
    }

    @Test
    void 다른_사용자의_주문이면_예외가_발생한다() {
        Order order = Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 가짜_쿠폰);

        assertThatThrownBy(() -> order.validateOwner(박스터))
                .isInstanceOf(OrderException.class);
    }

    @Test
    void 할인_금액을_반환한다() {
        Order order = Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 밀리_쿠폰_1000원);

        Money totalPrice = order.calculateDiscountPrice();

        assertThat(totalPrice).isEqualTo(new Money(1000));
    }

    @Test
    void 최종_결제_금액이랑_다르면_예외가_발생한다() {
        Order order = Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 밀리_쿠폰_10퍼센트);

        assertThatThrownBy(() -> order.validateOrderPrice(BigDecimal.valueOf(123000)))
                .isInstanceOf(OrderException.class);
    }

    @Test
    void 쿠폰을_사용하지_않는_주문이라면_true를_반환한다() {
        Order order = Order.of(밀리, List.of(장바구니_밀리_치킨_10개, 장바구니_밀리_피자_1개), 3000, 가짜_쿠폰);

        boolean result = order.isUnusedCoupon();

        assertThat(result).isTrue();
    }
}