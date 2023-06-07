package cart.dao;

import cart.domain.Member;
import cart.domain.Product;
import cart.domain.order.DeliveryFee;
import cart.domain.order.Order;
import cart.domain.order.OrderPrice;
import cart.domain.order.OrderProduct;
import cart.entity.OrderEntity;
import cart.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class OrderDaoTest {

    @Autowired
    DataSource dataSource;

    OrderDao orderDao;

    @BeforeEach
    void setting() {
        orderDao = new OrderDao(new JdbcTemplate(dataSource));
    }

    @DisplayName("주문을 저장한다.")
    @Test
    void save() throws SQLException {
        //given
        final Member member = new Member(1L, "email", "password");
        final List<OrderProduct> products = List.of(
                new OrderProduct(new Product(1L, "productA", 1000, "image1"), 3),
                new OrderProduct(new Product(2L, "productB", 10000, "image2"), 5)
        );
        final Order order = new Order(products, OrderPrice.of(products, new DeliveryFee(3000)));

        //when
        final long savedOrder = orderDao.save(member, order);
        final OrderEntity orderEntity = orderDao.findById(savedOrder);
        final List<OrderItemEntity> orderProductEntities = orderDao.findOrderProductByOrderId(savedOrder);

        //then
        assertAll(
                () -> assertThat(orderEntity.getMemberId()).isEqualTo(1L),
                () -> assertThat(orderEntity.getPrice()).isEqualTo(56000),
                () -> assertThat(orderProductEntities.get(0).getOrderId()).isEqualTo(savedOrder),
                () -> assertThat(orderProductEntities.get(0).getProductId()).isEqualTo(1L),
                () -> assertThat(orderProductEntities.get(0).getQuantity()).isEqualTo(3),
                () -> assertThat(orderProductEntities.get(1).getOrderId()).isEqualTo(savedOrder),
                () -> assertThat(orderProductEntities.get(1).getProductId()).isEqualTo(2L),
                () -> assertThat(orderProductEntities.get(1).getQuantity()).isEqualTo(5)
        );
    }

    @DisplayName("주문 아이디들을 통해 주문 상품들을 가져온다.")
    @Test
    void findOrderProductByOrderByIds() {
        //given
        final Member member = new Member(1L, "email", "password");
        final List<OrderProduct> products1 = List.of(
                new OrderProduct(new Product(1L, "productA", 1000, "image1"), 3),
                new OrderProduct(new Product(2L, "productB", 10000, "image2"), 5)
        );
        final Order firstOrder = new Order(products1, OrderPrice.of(products1, new DeliveryFee(3000)));

        final List<OrderProduct> products2 = List.of(
                new OrderProduct(new Product(1L, "productA", 1000, "image1"), 1),
                new OrderProduct(new Product(2L, "productB", 10000, "image2"), 2)
        );
        final Order secondOrder = new Order(products2, OrderPrice.of(products2, new DeliveryFee(3000)));
        final long firstOrderId = orderDao.save(member, firstOrder);
        final long secondOrderId = orderDao.save(member, secondOrder);

        //when
        final List<OrderItemEntity> orderProductByOrderByIds = orderDao.findOrderProductByIds(List.of(firstOrderId, secondOrderId));

        //then
        assertThat(orderProductByOrderByIds).hasSize(4);
    }
}
