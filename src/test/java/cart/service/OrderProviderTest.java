package cart.service;

import cart.controller.dto.OrderResponse;
import cart.domain.Product;
import cart.domain.coupon.Coupon;
import cart.domain.member.Member;
import cart.domain.order.*;
import cart.service.order.OrderMapper;
import cart.service.order.OrderProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class OrderProviderTest {

    private OrderProvider orderProvider;
    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void init() {
        orderProvider = new OrderProvider(orderRepository, new OrderMapper());
    }

    @Test
    void 상품을_조회한다() {
        // given
        final Member member = new Member(1L, "a@a.com", "1234");
        final Product chicken = new Product(1L, "치킨", 10000, "imgUrl");
        final Product dessert = new Product(1L, "desert", 5000, "imgUrl");
        final OrderItem chickenOrderItem = new OrderItem(chicken, Quantity.from(1));
        final OrderItem desertOrderItem = new OrderItem(dessert, Quantity.from(1));
        final Coupon coupon = new Coupon(1L, 1L, "1000원 할인 쿠폰", "1000원이 할인 됩니다.", 1000, false);

        final Order order = new Order(new OrderItems(List.of(chickenOrderItem, desertOrderItem)), member, coupon, Price.from(chicken.getPrice() + dessert.getPrice()));
        given(orderRepository.findOrderByMemberId(anyLong())).willReturn(List.of(order));

        // when
        final List<OrderResponse> orderResponses = orderProvider.findOrderByMember(member.getId());

        // then
        final OrderResponse result = orderResponses.get(0);
        assertAll(
                () -> assertThat(result.getOrderItems()).hasSize(2),
                () -> assertThat(result.getDate()).isNotNull(),
                () -> assertThat(result.getPrice()).isEqualTo(15000)
        );
    }
}
