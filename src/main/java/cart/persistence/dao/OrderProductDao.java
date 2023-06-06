package cart.persistence.dao;

import cart.persistence.entity.OrderProductEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderProductDao {

    private static final RowMapper<OrderProductEntity> ORDER_PRODUCT_ENTITY_ROW_MAPPER = (rs, rowNum) -> new OrderProductEntity(
            rs.getLong("id"),
            rs.getLong("order_id"),
            rs.getLong("product_id"),
            rs.getString("name"),
            rs.getString("image_url"),
            rs.getInt("purchased_price"),
            rs.getInt("quantity")
    );

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public OrderProductDao(final JdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("order_product")
                .usingGeneratedKeyColumns("id");
    }

    public void createProducts(final List<OrderProductEntity> orderProductEntities) {
        final SqlParameterSource[] sources = orderProductEntities.stream()
                .map(orderProductEntity -> new MapSqlParameterSource()
                        .addValue("order_id", orderProductEntity.getOrderId())
                        .addValue("product_id", orderProductEntity.getProductId())
                        .addValue("purchased_price", orderProductEntity.getPurchasedPrice())
                        .addValue("quantity", orderProductEntity.getQuantity())
                        .addValue("name", orderProductEntity.getName())
                        .addValue("image_url", orderProductEntity.getImageUrl())
                ).toArray(SqlParameterSource[]::new);
        jdbcInsert.executeBatch(sources);
    }

    public List<OrderProductEntity> findAllByOrderId(final Long orderId) {
        final String sql = "SELECT id, order_id, product_id, name, image_url, purchased_price, quantity FROM order_product " +
                "WHERE order_id IN (:order_id)";
        final SqlParameterSource source = new MapSqlParameterSource("order_id", orderId);
        return namedJdbcTemplate.query(sql, source, ORDER_PRODUCT_ENTITY_ROW_MAPPER);
    }

    public List<OrderProductEntity> findAllByOrderIds(final List<Long> orderIds) {
        final String sql = "SELECT id, order_id, product_id, name, image_url, purchased_price, quantity FROM order_product " +
                "WHERE order_id IN (:order_id)";
        final SqlParameterSource source = new MapSqlParameterSource("order_id", orderIds);
        return namedJdbcTemplate.query(sql, source, ORDER_PRODUCT_ENTITY_ROW_MAPPER);
    }
}
