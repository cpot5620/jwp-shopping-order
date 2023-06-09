package cart.product.application;

import cart.cartitem.dao.CartItemDao;
import cart.cartitem.domain.CartItem;
import cart.product.application.dto.ProductCartItemDto;
import cart.product.dao.ProductDao;
import cart.product.domain.Product;
import cart.product.exception.NotFoundProductException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static cart.fixtures.CartItemFixtures.Member_Dooly_CartItem1;
import static cart.fixtures.MemberFixtures.Member_Dooly;
import static cart.fixtures.ProductFixtures.CHICKEN;
import static cart.fixtures.ProductFixtures.PANCAKE;
import static cart.fixtures.ProductFixtures.PIZZA;
import static cart.fixtures.ProductFixtures.SALAD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductDao productDao;

    @Mock
    CartItemDao cartItemDao;

    @Test
    void 모든_상품을_가져온다() {
        // given
        when(productDao.getAllProducts()).thenReturn(List.of(CHICKEN.ENTITY));

        // when
        final List<Product> products = productService.getAllProducts();

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(products).hasSize(1);
            softAssertions.assertThat(products.get(0)).usingRecursiveComparison().isEqualTo(CHICKEN.ENTITY);
        });
    }

    @Nested
    class getProductById_테스트 {

        @Test
        void 상품_id를_통해_상품을_가져오다() {
            // given
            when(productDao.countById(1L)).thenReturn(1L);
            when(productDao.getProductById(1L)).thenReturn(CHICKEN.ENTITY);

            // when
            final Product product = productService.getProductById(1L);

            // then
            assertThat(product).usingRecursiveComparison().isEqualTo(CHICKEN.ENTITY);
        }

        @Test
        void 존재하지_않는_상품_id를_조회하면_예외를_반환하다() {
            // given
            when(productDao.countById(1L)).thenReturn(0L);

            // when, then
            assertThatThrownBy(() -> productService.getProductById(1L))
                    .isInstanceOf(NotFoundProductException.class);
        }
    }

    @Nested
    class getProductsInPaging_테스트 {

        @Test
        void 첫페이지_상품_페이지를_가져오다() {
            // given
            when(productDao.getLastProductId()).thenReturn(4L);
            when(productDao.getProductByInterval(5L, 2))
                    .thenReturn(List.of(PANCAKE.ENTITY, PIZZA.ENTITY));

            // when
            final List<Product> products = productService.getProductsInPaging(0L, 2);

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(products).hasSize(2);
                softAssertions.assertThat(products.get(0)).usingRecursiveComparison().isEqualTo(PANCAKE.ENTITY);
                softAssertions.assertThat(products.get(1)).usingRecursiveComparison().isEqualTo(PIZZA.ENTITY);
            });
        }

        @Test
        void 첫페이지가_아닌_상품_페이지를_가져오다() {
            // given
            when(productDao.getProductByInterval(4L, 2))
                    .thenReturn(List.of(PIZZA.ENTITY, SALAD.ENTITY));

            // when
            final List<Product> products = productService.getProductsInPaging(4L, 2);

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(products).hasSize(2);
                softAssertions.assertThat(products.get(0)).usingRecursiveComparison().isEqualTo(PIZZA.ENTITY);
                softAssertions.assertThat(products.get(1)).usingRecursiveComparison().isEqualTo(SALAD.ENTITY);
            });
        }
    }

    @Nested
    class hasLastProduct_테스트 {

        @Test
        void 마지막_상품을_가지고_있으면_True를_반환하다() {
            // given
            when(productDao.getProductByInterval(2L, 2))
                    .thenReturn(List.of(CHICKEN.ENTITY));

            // when
            final boolean hasLastProduct = productService.hasLastProduct(2L, 2);

            // then
            assertThat(hasLastProduct).isTrue();
        }

        @Test
        void 마지막_상품을_가지고_있지_않으면_False를_반환하다() {
            // given
            when(productDao.getProductByInterval(4L, 2))
                    .thenReturn(List.of(PIZZA.ENTITY, SALAD.ENTITY));

            // when
            final boolean hasLastProduct = productService.hasLastProduct(4L, 2);

            // then
            assertThat(hasLastProduct).isFalse();
        }
    }

    @Test
    void 상품을_생성하다() {
        // given
        final Product product = Product.of("test", 10000, "www.test.com");
        when(productDao.createProduct(product)).thenReturn(5L);

        // when
        final Long productId = productService.createProduct(product);

        // then
        assertThat(productId).isEqualTo(5L);
    }

    @Nested
    class updateProduct_테스트 {

        @Test
        void 상품_정보를_수정하다() {
            // given
            final Product product = Product.of("test", 10000, "www.test.com");
            when(productDao.countById(1L)).thenReturn(1L);
            doNothing().when(productDao).updateProduct(1L, product);

            // when, then
            assertDoesNotThrow(() -> productService.updateProduct(1L, product));
        }

        @Test
        void 존재하지_않는_상품을_수정하면_예외를_반환하다() {
            // given
            when(productDao.countById(1L)).thenReturn(0L);

            // when, then
            assertThatThrownBy(() -> productService.updateProduct(1L, CHICKEN.ENTITY))
                    .isInstanceOf(NotFoundProductException.class);
        }
    }

    @Nested
    class deleteProduct_테스트 {

        @Test
        void 상품을_삭제하다() {
            // given
            doNothing().when(productDao).deleteProduct(1L);
            when(productDao.countById(1L)).thenReturn(1L);

            // when, then
            assertDoesNotThrow(() -> productService.deleteProduct(1L));
        }

        @Test
        void 존재하지_않는_상품을_삭제하면_예외를_반환하다() {
            // given
            when(productDao.countById(1L)).thenReturn(0L);

            // when, then
            assertThatThrownBy(() -> productService.deleteProduct(1L))
                    .isInstanceOf(NotFoundProductException.class);
        }
    }

    @Nested
    class getProductCartItemsByProduct_테스트 {

        @Test
        void 장바구니에_존재하는_상품을_가져오다() {
            // given
            final Optional<CartItem> cartItem = Optional.of(Member_Dooly_CartItem1.ENTITY);
            when(cartItemDao.findByMemberIdAndProductId(1L, 1L))
                    .thenReturn(cartItem);

            // when
            final List<ProductCartItemDto> productCartItemDtos
                    = productService.getProductCartItemsByProduct(Member_Dooly.ENTITY, List.of(CHICKEN.ENTITY));

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(productCartItemDtos).hasSize(1);
                softAssertions.assertThat(productCartItemDtos.get(0).getProduct()).usingRecursiveComparison().isEqualTo(CHICKEN.ENTITY);
                softAssertions.assertThat(productCartItemDtos.get(0).getCartItem()).usingRecursiveComparison().isEqualTo(Member_Dooly_CartItem1.ENTITY);
            });
        }

        @Test
        void 장바구니에_존재하지_않는_상품을_가져오다() {
            // given
            final Optional<CartItem> cartItem = Optional.empty();
            when(cartItemDao.findByMemberIdAndProductId(1L, 1L))
                    .thenReturn(cartItem);

            // when
            final List<ProductCartItemDto> productCartItemDtos
                    = productService.getProductCartItemsByProduct(Member_Dooly.ENTITY, List.of(CHICKEN.ENTITY));

            // then
            SoftAssertions.assertSoftly(softAssertions -> {
                softAssertions.assertThat(productCartItemDtos).hasSize(1);
                softAssertions.assertThat(productCartItemDtos.get(0).getProduct()).usingRecursiveComparison().isEqualTo(CHICKEN.ENTITY);
                softAssertions.assertThat(productCartItemDtos.get(0).getCartItem()).isNull();
            });
        }
    }
}
