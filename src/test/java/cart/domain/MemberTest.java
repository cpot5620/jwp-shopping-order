package cart.domain;

import cart.exception.OrderException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MemberTest {

    @Test
    void 모든_필드가_같으면_같은_Member이다() {
        // given
        Member member1 = new Member(1L, "email", "pw", new Point(100));
        Member member2 = new Member(1L, "email", "pw", new Point(100));

        // expect
        assertThat(member1).isEqualTo(member2);
    }

    @ParameterizedTest
    @CsvSource({"1234, true", "12345, false"})
    void 비밀번호가_같은지_확인한다(final String password, final boolean expect) {
        // given
        Member member = new Member(1L, "email", "1234", new Point(0));

        // when
        boolean result = member.checkPassword(password);

        // then
        assertThat(result).isEqualTo(expect);
    }

    @Test
    void 총_상품_가격에_따른_포인트를_추가한다() {
        // given
        Point point = new Point(0);
        Member member = new Member(1L, "email", "1234", point);
        int totalProductPrice = 1000;

        // when
        Member newMember = member.savePoint(totalProductPrice);

        // then
        assertThat(newMember.getPointValue()).isEqualTo(50);
    }

    @Test
    void 사용_포인트를_뺀다() {
        // given
        Point point = new Point(1000);
        Member member = new Member(1L, "email", "password", point);
        Point usePoint = new Point(1000);

        // when
        Member newMember = member.usePoint(usePoint);

        // then
        assertThat(newMember.getPointValue()).isEqualTo(0);
    }

    @Test
    void 사용한_포인트가_보유_포인트보다_많으면_예외가_발생한다() {
        // given
        Point point = new Point(1000);
        Member member = new Member(1L, "email", "password", point);
        Point usePoint = new Point(10000);

        // expect
        assertThatThrownBy(() -> member.usePoint(usePoint))
                .isInstanceOf(OrderException.IllegalUsePoint.class);
    }
}
