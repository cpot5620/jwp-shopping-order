package cart.dao;

import static cart.fixture.TestFixture.ORDER_ITEMS_ONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import cart.dao.dto.OrderItemDto;
import cart.domain.MemberCoupon;
import cart.domain.OrderItem;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@JdbcTest
class OrderItemDaoTest {

    private static final Long ORDER_ID = 1L;
    private static final Long ORDER_ID_2 = 1L;
    @Autowired
    JdbcTemplate jdbcTemplate;
    private OrderItemDao orderItemDao;

    @BeforeEach
    void setUp() {
        this.orderItemDao = new OrderItemDao(jdbcTemplate);
    }

    @Test
    void 주문_항목을_모두_저장한다() {
        assertThatNoException()
                .isThrownBy(() -> orderItemDao.insertAll(ORDER_ID, ORDER_ITEMS_ONE));
    }

    @Test
    void 쿠폰도_함께_저장된다() {
        orderItemDao.insertAll(ORDER_ID_2, ORDER_ITEMS_ONE);

        assertThat(orderItemDao.selectAllOf(ORDER_ID_2))
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .isEqualTo(toDtos(ORDER_ITEMS_ONE));
    }

    @Test
    void 주문_항목을_모두_조회한다() {
        orderItemDao.insertAll(ORDER_ID, ORDER_ITEMS_ONE);
        List<OrderItemDto> orderItems = orderItemDao.selectAllOf(ORDER_ID);

        assertThat(orderItems)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrderElementsOf(toDtos(ORDER_ITEMS_ONE));
    }

    private List<OrderItemDto> toDtos(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toDtos)
                .collect(Collectors.toList());
    }

    private OrderItemDto toDtos(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getOrderedProduct().getId(),
                orderItem.getOrderedProduct().getName(),
                orderItem.getOrderedProduct().getPrice().getValue(),
                orderItem.getOrderedProduct().getImageUrl(),
                orderItem.getQuantity(),
                orderItem.getUsedCoupons().stream()
                        .map(MemberCoupon::getId)
                        .collect(Collectors.toList())
        );
    }
}
