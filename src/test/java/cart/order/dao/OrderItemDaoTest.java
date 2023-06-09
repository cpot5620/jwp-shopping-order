package cart.order.dao;

import cart.config.DaoTest;
import cart.order.application.dto.OrderItemDto;
import cart.order.domain.OrderHistory;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static cart.fixtures.MemberFixtures.Member_Dooly;
import static cart.fixtures.ProductFixtures.CHICKEN;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class OrderItemDaoTest extends DaoTest {

    @Test
    void 주문_ID를_통해_어떤_상품들을_주문했는지_조회한다() {
        // given, when
        final List<OrderItemDto> orderItemDtos = orderItemDao.findByOrderHistoryId(1L);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(orderItemDtos).hasSize(1);
            softAssertions.assertThat(orderItemDtos.get(0).getId()).isEqualTo(1L);
            softAssertions.assertThat(orderItemDtos.get(0).getProductId()).isEqualTo(1);
            softAssertions.assertThat(orderItemDtos.get(0).getName()).isEqualTo(CHICKEN.NAME);
            softAssertions.assertThat(orderItemDtos.get(0).getPrice()).isEqualTo(CHICKEN.PRICE);
            softAssertions.assertThat(orderItemDtos.get(0).getImageUrl()).isEqualTo(CHICKEN.IMAGE_URL);
            softAssertions.assertThat(orderItemDtos.get(0).getQuantity()).isEqualTo(2);
        });
    }

    @Test
    void 장바구니한_상품을_주문하다() {
        // given
        final OrderHistory orderHistory = new OrderHistory(1L, Member_Dooly.ENTITY, 50000L, LocalDateTime.now());
        final OrderItemDto orderItemDto
                = new OrderItemDto(10L, orderHistory, CHICKEN.ID, CHICKEN.NAME, CHICKEN.PRICE, CHICKEN.IMAGE_URL, 10);

        // when
        final Long orderItemId = orderItemDao.save(orderItemDto);

        // then
        assertThat(orderItemId).isEqualTo(10L);
    }
}
