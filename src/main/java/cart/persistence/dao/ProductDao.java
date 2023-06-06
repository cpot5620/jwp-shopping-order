package cart.persistence.dao;

import cart.persistence.entity.ProductEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductDao {

    private static final RowMapper<ProductEntity> PRODUCT_MAPPER = (rs, rowNum) -> new ProductEntity(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("price"),
            rs.getString("image_url")
    );

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public ProductDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("product")
                .usingGeneratedKeyColumns("id");
    }

    public List<ProductEntity> getAllProducts() {
        final String sql = "SELECT id, name, price, image_url FROM product";
        return jdbcTemplate.query(sql, PRODUCT_MAPPER);
    }

    public ProductEntity findById(final Long productId) {
        final String sql = "SELECT id, name, price, image_url FROM product WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, PRODUCT_MAPPER, productId);
    }

    public List<ProductEntity> findByIds(final List<Long> productIds) {
        if (productIds.isEmpty()) {
            return new ArrayList<>();
        }

        final String sql = "SELECT id, name, price, image_url FROM product WHERE id IN (:id)";
        final SqlParameterSource source = new MapSqlParameterSource("id", productIds);
        return namedJdbcTemplate.query(sql, source, PRODUCT_MAPPER);
    }

    public Long createProduct(final ProductEntity productEntity) {
        final BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(productEntity);
        return jdbcInsert.executeAndReturnKey(source).longValue();
    }

    public void updateProduct(final ProductEntity productEntity) {
        final String sql = "UPDATE product SET name = ?, price = ?, image_url = ? WHERE id = ?";
        jdbcTemplate.update(
                sql,
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getImageUrl(),
                productEntity.getId()
        );
    }

    public void deleteProduct(final Long productId) {
        final String sql = "DELETE FROM product WHERE id = ?";
        jdbcTemplate.update(sql, productId);
    }
}
