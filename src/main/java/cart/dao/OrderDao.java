package cart.dao;

import cart.entity.OrderEntity;
import cart.ui.paging.Page;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {

    private final SimpleJdbcInsert jdbcTemplate;

    public OrderDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("shopping_order")
                .usingGeneratedKeyColumns("id")
                .usingColumns("member_id", "used_point", "saved_point");
    }

    public Long save(final OrderEntity orderEntity) {
        return jdbcTemplate.executeAndReturnKey(new BeanPropertySqlParameterSource(orderEntity)).longValue();
    }

    public Optional<OrderEntity> findById(final Long id) {
        final String sql = "SELECT * FROM shopping_order WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.getJdbcTemplate()
                    .queryForObject(sql, orderEntityMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<OrderEntity> findByMemberId(final Long memberId) {
        final String sql = "SELECT * FROM shopping_order WHERE member_id = ?";
        try {
            return jdbcTemplate.getJdbcTemplate().query(sql, orderEntityMapper(), memberId);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<OrderEntity> findByMemberIdWithPaging(final Long memberId, final Page page) {
        final String sql = "SELECT * FROM shopping_order WHERE member_id = ? "
                + "ORDER BY ordered_at "
                + "LIMIT ?, ?";
        try {
            return jdbcTemplate.getJdbcTemplate().query(sql, orderEntityMapper(), memberId,
                    page.getOffset(),
                    page.getLimit());
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    private static RowMapper<OrderEntity> orderEntityMapper() {
        return (rs, rowNum) -> new OrderEntity(
                rs.getLong("id"),
                rs.getLong("member_id"),
                rs.getTimestamp("ordered_at").toLocalDateTime(),
                rs.getInt("used_point"),
                rs.getInt("saved_point")
        );
    }
}
