package cart.service;

import cart.dao.CartItemDao;
import cart.domain.cartItem.CartItem;
import cart.domain.common.Money;
import cart.domain.member.Member;
import cart.domain.order.Order;
import cart.domain.order.OrderItem;
import cart.domain.point.Point;
import cart.domain.point.PointPolicy;
import cart.domain.product.Product;
import cart.dto.OrderItemDto;
import cart.dto.OrderRequest;
import cart.dto.OrderResponse;
import cart.dto.OrderedProduct;
import cart.dto.PageInfo;
import cart.dto.PageRequest;
import cart.dto.PagingOrderResponse;
import cart.dto.PaymentDto;
import cart.repository.MemberRepository;
import cart.repository.OrderRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static cart.common.fixture.DomainFixture.EMAIL;
import static cart.common.fixture.DomainFixture.MEMBER_HUCHU;
import static cart.common.fixture.DomainFixture.PASSWORD;
import static cart.common.fixture.DomainFixture.PRODUCT_CHICKEN;
import static cart.common.fixture.DomainFixture.PRODUCT_IMAGE;
import static cart.common.fixture.DomainFixture.PRODUCT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;


    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CartItemDao cartItemDao;
    @Mock
    private PointPolicy pointPolicy;

    @Test
    void 주문을_추가한다() {
        //given
        final Product product = new Product(1L, PRODUCT_NAME, 20000, PRODUCT_IMAGE);
        final Member member = new Member(1L, EMAIL, PASSWORD, 1000);
        final CartItem cartItem = new CartItem(1L, 1, product, member);

        final OrderRequest orderRequest = new OrderRequest(List.of(new OrderItemDto(cartItem.getId())), new PaymentDto(20000, 19000, 1000));

        when(cartItemDao.findById(cartItem.getId()))
                .thenReturn(cartItem);

        when(pointPolicy.save(Money.valueOf(19000)))
                .thenReturn(Point.valueOf(1900));

        when(orderRepository.addOrder(Order.from(member, 19000, 1000, List.of(new OrderItem(product, 1)))))
                .thenReturn(1L);

        //when
        final Long id = orderService.addOrder(member, orderRequest);

        //then
        assertThat(id).isEqualTo(1L);
        verify(memberRepository, times(1))
                .updateMemberPoint(new Member(1L, EMAIL, PASSWORD, 1900));
        verify(cartItemDao, times(1))
                .deleteByIdIn(List.of(cartItem.getId()));
    }

    @Test
    void 회원의_모든_주문을_얻는다() {
        //given
        final PageRequest pageRequest = new PageRequest(1, 10);

        when(orderRepository.getAllOrders(MEMBER_HUCHU, 0, 10))
                .thenReturn(List.of(Order.from(1L, MEMBER_HUCHU, 19000, 1000, List.of(new OrderItem(PRODUCT_CHICKEN, 1)))));

        when(orderRepository.countAllOrders())
                .thenReturn(1);

        //when
        final PagingOrderResponse response = orderService.getAllOrders(MEMBER_HUCHU, pageRequest);

        //then
        assertSoftly(softly -> {
            softly.assertThat(response.getOrders()).usingRecursiveComparison()
                    .isEqualTo(List.of(new OrderResponse(
                                    1L,
                                    List.of(new OrderedProduct(PRODUCT_NAME, 20000, 1, PRODUCT_IMAGE)),
                                    new PaymentDto(20000, 19000, 1000))
                            )
                    );
            softly.assertThat(response.getPageInfo()).usingRecursiveComparison()
                    .isEqualTo(new PageInfo(1, 10, 1, 1));
        });

    }

    @Test
    void id로_회원의_특정_주문을_얻는다() {
        //given
        final Long orderId = 1L;
        when(orderRepository.getOrderById(MEMBER_HUCHU, orderId))
                .thenReturn(Order.from(orderId, MEMBER_HUCHU, 19000, 1000, List.of(new OrderItem(PRODUCT_CHICKEN, 1))));

        //when
        final OrderResponse response = orderService.getOrderById(MEMBER_HUCHU, orderId);

        //then
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(new OrderResponse(
                                1L,
                                List.of(new OrderedProduct(PRODUCT_NAME, 20000, 1, PRODUCT_IMAGE)),
                                new PaymentDto(20000, 19000, 1000)
                        )
                );
    }
}
