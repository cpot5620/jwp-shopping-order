package cart.dao;

import cart.domain.Product;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;

    public ProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Product product) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tb_product (name, price, image_url) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, product.getName());
            ps.setInt(2, product.getPrice());
            ps.setString(3, product.getImageUrl());

            return ps;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public List<Product> findAll() {
        String sql = "SELECT * FROM tb_product";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long productId = rs.getLong("id");
            String name = rs.getString("name");
            int price = rs.getInt("price");
            String imageUrl = rs.getString("image_url");
            return new Product(productId, name, price, imageUrl);
        });
    }

    public Optional<Product> findById(Long productId) {
        String sql = "SELECT * FROM tb_product WHERE id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String name = rs.getString("name");
            int price = rs.getInt("price");
            String imageUrl = rs.getString("image_url");
            return new Product(productId, name, price, imageUrl);
        }, productId)
                .stream()
                .findAny();
    }

    public boolean isExistBy(Long productId) {
        String sql = "SELECT EXISTS (SELECT 1 FROM tb_product WHERE id = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, productId));
    }

    public void update(Long productId, Product product) {
        String sql = "UPDATE tb_product SET name = ?, price = ?, image_url = ? WHERE id = ?";

        jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getImageUrl(), productId);
    }

    public void deleteById(Long productId) {
        String sql = "DELETE FROM tb_product WHERE id = ?";

        jdbcTemplate.update(sql, productId);
    }
}
