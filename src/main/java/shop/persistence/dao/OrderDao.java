package shop.persistence.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import shop.exception.DatabaseException;
import shop.persistence.entity.OrderEntity;

import java.util.List;

@Component
public class OrderDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private static final RowMapper<OrderEntity> rowMapper =
            (rs, rowNum) -> new OrderEntity(
                    rs.getLong("id"),
                    rs.getLong("member_id"),
                    rs.getLong("total_price"),
                    rs.getLong("discounted_total_price"),
                    rs.getInt("delivery_price"),
                    rs.getTimestamp("order_at").toLocalDateTime()
            );

    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("`order`")
                .usingColumns("member_id", "total_price",
                        "discounted_total_price", "delivery_price", "order_at")
                .usingGeneratedKeyColumns("id");
    }

    public Long insert(OrderEntity orderEntity) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(orderEntity);

        return simpleJdbcInsert.executeAndReturnKey(param).longValue();
    }

    public OrderEntity findById(Long id) {
        String sql = "SELECT * FROM `order` WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DatabaseException.IllegalDataException(id + "를 갖는 주문 정보를 찾을 수 없습니다.");
        }
    }

    public List<OrderEntity> findAllByMemberId(Long memberId) {
        String sql = "SELECT * FROM `order` where member_id = ?";

        return jdbcTemplate.query(sql, rowMapper, memberId);
    }
}
