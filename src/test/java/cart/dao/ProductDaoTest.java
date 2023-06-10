package cart.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cart.entity.ProductEntity;
import cart.repository.dao.ProductDao;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@JdbcTest
class ProductDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ProductDao productDao;

    @BeforeEach
    void setUp() {
        this.productDao = new ProductDao(jdbcTemplate);
    }

    @Test
    void createProduct() {
        // given
        final ProductEntity product = new ProductEntity("product", 1_000, "www.image.png");

        // when
        final Long saveId = productDao.createProduct(product);

        // then
        assertThat(saveId).isNotNull();
    }

    @Test
    void getProductById() {
        // given
        final ProductEntity product = new ProductEntity("product", 1_000, "www.image.png");
        final Long saveId = productDao.createProduct(product);

        // when
        final ProductEntity findProduct = productDao.getProductById(saveId);

        // then
        assertThat(findProduct.getName()).isEqualTo(product.getName());
        assertThat(findProduct.isDeleted()).isFalse();
    }

    @Test
    void deleteProduct() {
        // given
        final ProductEntity product = new ProductEntity("product", 1_000, "www.image.png");
        final Long saveId = productDao.createProduct(product);
        productDao.deleteProduct(saveId);

        // when, then
        assertThatThrownBy(() -> productDao.getProductById(saveId))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void getProductByIds() {
        // given
        final List<Long> ids = List.of(1L, 3L);

        // when
        final List<ProductEntity> findProducts = productDao.getProductByIds(ids);

        // then
        assertThat(findProducts).map(ProductEntity::getId)
                .isEqualTo(ids);
    }
}
