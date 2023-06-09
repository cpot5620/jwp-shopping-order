package cart.application;

import static cart.fixture.MemberFixture.MEMBER;
import static cart.fixture.ProductFixture.CHICKEN;
import static cart.fixture.ProductFixture.PIZZA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import cart.domain.CartItem;
import cart.dto.request.CartItemQuantityUpdateRequest;
import cart.dto.request.CartItemRequest;
import cart.dto.response.CartItemResponse;
import cart.exception.ItemOwnerNotMatchException;
import cart.exception.ProductNotFound;
import cart.repository.CartItemRepository;
import cart.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;


@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CartItemService 은(는)")
class CartItemServiceTest {

    private static final CartItemRequest CART_ITEM_REQUEST = new CartItemRequest(CHICKEN.getId());

    private CartItemRepository cartItemRepository = mock(CartItemRepository.class);
    private ProductRepository productRepository = mock(ProductRepository.class);
    private CartItemService cartItemService = new CartItemService(cartItemRepository, productRepository);

    @Test
    void 아이템을_추가한다() {
        // given
        given(productRepository.findById(1L))
                .willReturn(Optional.of(CHICKEN));
        given(cartItemRepository.save(any()))
                .willReturn(1L);

        // when
        Long actual = cartItemService.add(MEMBER, CART_ITEM_REQUEST);

        // then
        assertThat(actual).isEqualTo(1L);
    }

    @Test
    void 아이템_추가시_해당하는_상품이_없으면_예외() {
        // given
        given(productRepository.findById(1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartItemService.add(MEMBER, CART_ITEM_REQUEST))
                .isInstanceOf(ProductNotFound.class);
    }

    @Test
    void 멤버를_통해서_모든_장바구니_상품을_반환한다() {
        // given
        List<CartItem> expected = List.of(
                new CartItem(1L, 5, CHICKEN, 1L),
                new CartItem(2L, 10, PIZZA, 1L)
        );
        given(cartItemRepository.findAllByMemberId(MEMBER.getId()))
                .willReturn(expected);
        // when
        List<CartItemResponse> actual = cartItemService.findByMember(MEMBER);

        // then
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 상품_수량을_변경한다_수량을_0개로하면_삭제() {
        // given
        CartItemQuantityUpdateRequest request = new CartItemQuantityUpdateRequest(0);
        long cartItemId = 1L;
        CartItem cartItem = new CartItem(cartItemId, 1, PIZZA, MEMBER.getId());
        given(cartItemRepository.findById(cartItemId))
                .willReturn(Optional.of(cartItem));

        // when
        cartItemService.updateQuantity(MEMBER, cartItemId, request);

        // then
        verify(cartItemRepository, times(1)).deleteById(cartItemId);
        verify(cartItemRepository, times(0)).updateQuantity(any());
    }

    @Test
    void 상품_수량을_변경한다_수량을_1개_이상이면_업데이트() {
        // given
        CartItemQuantityUpdateRequest request = new CartItemQuantityUpdateRequest(10);
        long cartItemId = 1L;
        CartItem cartItem = new CartItem(cartItemId, 1, PIZZA, MEMBER.getId());
        given(cartItemRepository.findById(cartItemId))
                .willReturn(Optional.of(cartItem));

        // when
        cartItemService.updateQuantity(MEMBER, cartItemId, request);

        // then
        verify(cartItemRepository, times(0)).deleteById(any());
        verify(cartItemRepository, times(1)).updateQuantity(any());
    }

    @Test
    void 수량_변경시_해당하는_멤버의_상품이_아니면_예외() {
        // given
        CartItemQuantityUpdateRequest request = new CartItemQuantityUpdateRequest(10);
        CartItem cartItem = new CartItem(1L, 1, PIZZA, MEMBER.getId() + 1);
        given(cartItemRepository.findById(1L))
                .willReturn(Optional.of(cartItem));

        // when & then
        assertThatThrownBy(() -> cartItemService.updateQuantity(MEMBER, cartItem.getId(), request))
                .isInstanceOf(ItemOwnerNotMatchException.class);
    }

    @Test
    void 장바구니_상품을_삭제한다() {
        // given
        CartItem cartItem = new CartItem(1L, 1, PIZZA, MEMBER.getId());
        given(cartItemRepository.findById(1L))
                .willReturn(Optional.of(cartItem));
        // when & then
        assertThatNoException().isThrownBy(() -> cartItemService.remove(MEMBER, cartItem.getId()));
    }

    @Test
    void 상품_삭제시_해당하는_멤버의_상품이_아니면_예외() {
        // given
        CartItem cartItem = new CartItem(1L, 1, PIZZA, MEMBER.getId() + 1);
        given(cartItemRepository.findById(1L))
                .willReturn(Optional.of(cartItem));

        // when & then
        assertThatThrownBy(() -> cartItemService.remove(MEMBER, cartItem.getId()))
                .isInstanceOf(ItemOwnerNotMatchException.class);
    }
}
