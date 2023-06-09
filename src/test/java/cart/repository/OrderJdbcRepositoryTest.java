package cart.repository;

import cart.dao.ProductDao;
import cart.domain.Product;
import cart.domain.coupon.Coupon;
import cart.domain.member.Member;
import cart.domain.order.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@RepositoryTest
@Sql("classpath:truncate_order.sql")
class OrderJdbcRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductDao productDao;
    private Order noneCouponOrder;
    private Order couponOrder;
    private Member member;

    @BeforeEach
    void init() {
        member = new Member(1L, "a@a.com", "1234");
        noneCouponOrder = createOrderWithCoupon(null);
        couponOrder = createOrderWithCoupon(new Coupon(1L, 1L, "3000원 할인 쿠폰", "3000원이 할인 됩니다.", 3000, false));
    }

    private Order createOrderWithCoupon(final Coupon coupon) {
        final Product chicken = productDao.getProductById(1L);
        final Product dessert = productDao.getProductById(2L);
        final OrderItem chickenOrderItem = new OrderItem(chicken, Quantity.from(1));
        final OrderItem desertOrderItem = new OrderItem(dessert, Quantity.from(1));

        final OrderItems orderItems = new OrderItems(List.of(chickenOrderItem, desertOrderItem));
        final Price price = Price.from(chicken.getPrice() + dessert.getPrice());
        return new Order(orderItems, member, coupon, price);
    }

    @Test
    void 주문을_저장한다() {
        // when
        final Long savedCouponOrderId = orderRepository.save(couponOrder);
        final Long savedNoneCouponOrderId = orderRepository.save(noneCouponOrder);

        // then
        assertAll(
                () -> assertThat(assertThat(savedCouponOrderId).isNotNull()),
                () -> assertThat(assertThat(savedNoneCouponOrderId).isNotNull())
        );
    }

    @Test
    void 주문을_조회한다() {
        // given
        orderRepository.save(couponOrder);
        orderRepository.save(noneCouponOrder);

        // when
        final List<Order> orders = orderRepository.findOrderByMemberId(member.getId());

        // then
        assertAll(
                () -> assertThat(orders).hasSize(2),
                () -> assertThat(getOrder(orders, 0).getOrderItems()).hasSize(2),
                () -> assertThat(getOrder(orders, 0).getCoupon()).isNull(),
                () -> assertThat(getOrder(orders, 1).getOrderItems()).hasSize(2),
                () -> assertThat(getOrder(orders, 1).getCoupon()).isNull()
        );
    }

    private static Order getOrder(final List<Order> orders, final int index) {
        return orders.get(index);
    }
}
