package cart.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static cart.domain.fixture.DiscountPolicyFixture.defaultDiscountPolicy;
import static cart.domain.fixture.OrderFixture.orderUnderDiscountThreshold;
import static cart.domain.fixture.OrderFixture.orderWithoutId;
import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {


    @Test
    @DisplayName("Order를 인자로 받아 PaymentRecord를 생성한다.")
    void createPaymentRecord() {
        //given
        final DiscountPolicy discountPolicy = defaultDiscountPolicy;
        final DeliveryPolicy deliveryPolicy = new DefaultDeliveryPolicy("기본 배송 정책", Money.from(3_500));
        final Payment payment = new Payment(List.of(discountPolicy), List.of(deliveryPolicy));
        final PaymentRecord expected = new PaymentRecord(orderWithoutId, Money.from(55_000),
                Map.of(discountPolicy, Money.from(5_500)), Map.of(deliveryPolicy, Money.from(3_500)));
        //when
        final PaymentRecord actual = payment.createPaymentRecord(orderWithoutId);
        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("적용대상이 아닌 주문은 할인하지 않는다. ")
    void createPaymentRecord_notAppliedDiscount() {
        //given
        final DiscountPolicy discountPolicy = defaultDiscountPolicy;
        final DeliveryPolicy deliveryPolicy = new DefaultDeliveryPolicy("기본 배송 정책", Money.from(3_500));
        final Payment payment = new Payment(List.of(discountPolicy), List.of(deliveryPolicy));
        final PaymentRecord expected = new PaymentRecord(orderUnderDiscountThreshold, Money.from(40_000),
                Map.of(), Map.of(deliveryPolicy, Money.from(3_500)));
        //when
        final PaymentRecord actual = payment.createPaymentRecord(orderUnderDiscountThreshold);
        //then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

}