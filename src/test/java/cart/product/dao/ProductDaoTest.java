package cart.product.dao;

import cart.config.DaoTest;
import cart.product.domain.Product;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static cart.fixtures.ProductFixtures.CHICKEN;
import static cart.fixtures.ProductFixtures.PANCAKE;
import static cart.fixtures.ProductFixtures.PIZZA;
import static cart.fixtures.ProductFixtures.SALAD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("NonAsciiCharacters")
class ProductDaoTest extends DaoTest {

    @Test
    void 모든_상품을_조회하다() {
        final List<Product> products = productDao.getAllProducts();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(products).hasSize(4);
            softAssertions.assertThat(products.get(0)).usingRecursiveComparison().isEqualTo(CHICKEN.ENTITY);
            softAssertions.assertThat(products.get(1)).usingRecursiveComparison().isEqualTo(SALAD.ENTITY);
            softAssertions.assertThat(products.get(2)).usingRecursiveComparison().isEqualTo(PIZZA.ENTITY);
            softAssertions.assertThat(products.get(3)).usingRecursiveComparison().isEqualTo(PANCAKE.ENTITY);
        });
    }

    @Test
    void 상품_아이디를_통해_상품을_찾는다() {
        final Product product = productDao.getProductById(1L);

        assertThat(product).usingRecursiveComparison().isEqualTo(CHICKEN.ENTITY);
    }

    @Test
    void 특정_구간의_상품들을_찾는다() {
        final List<Product> products = productDao.getProductByInterval(3L, 5);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(products).hasSize(2);
            softAssertions.assertThat(products.get(0)).usingRecursiveComparison().isEqualTo(SALAD.ENTITY);
            softAssertions.assertThat(products.get(1)).usingRecursiveComparison().isEqualTo(CHICKEN.ENTITY);
        });
    }

    @Test
    void 가장_마지막으로_등록된_상품의_id를_찾는다() {
        final Long lastId = productDao.getLastProductId();

        assertThat(lastId).isEqualTo(4L);
    }

    @Test
    void 상품을_등록하다() {
        final Product product = Product.of("test", 50000, "www.test.com");
        final Long id = productDao.createProduct(product);

        assertThat(id).isEqualTo(5L);
    }

    @Test
    void 상품을_수정하다() {
        final Product product = Product.of("test", 50000, "www.test.com");

        assertDoesNotThrow(() -> productDao.updateProduct(1L, product));
    }

    @Test
    void 상품을_삭제하다() {
        assertDoesNotThrow(() -> productDao.deleteProduct(1L));
    }

    @Test
    void 상품_id를_통해_상품이_몇_개_존재하는지_확인하다() {
        final Long count = productDao.countById(1L);

        assertThat(count).isOne();
    }
}
