package cart.dao;

import cart.domain.Member;
import cart.domain.Order;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    public OrderDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("orders")
                .usingGeneratedKeyColumns("id");
    }

    public List<Order> findAll() {
        final String findAllQuery =
                "SELECT orders.id, orders.member_id, orders.created_at, orders.total_price, orders.final_price, members.id, members.email, members.password "
                        + "FROM orders "
                        + "INNER JOIN members ON orders.member_id = members.id";
        return jdbcTemplate.query(findAllQuery, orderRowMapper());
    }

    public Long saveOrder(final Order order) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("member_id", order.getMember().getId())
                .addValue("created_at", LocalDateTime.now())
                .addValue("total_price", order.getTotalPrice())
                .addValue("final_price", order.getFinalPrice());

        return insertAction.executeAndReturnKey(parameters).longValue();
    }

    public List<Order> findOrderByMember(final Long memberId) {
        final String findOrderByMemberQuery =
                "SELECT orders.id, orders.member_id, orders.created_at, orders.total_price, orders.final_price, members.id, members.email, members.password "
                        + "FROM orders "
                        + "INNER JOIN members ON orders.member_id = members.id "
                        + "WHERE orders.member_id = ?";

        return jdbcTemplate.query(findOrderByMemberQuery, orderRowMapper(), memberId);
    }

    public Optional<Order> findById(final Long id) {
        final String findByIdQuery =
                "SELECT orders.id, orders.member_id, orders.created_at, orders.total_price, orders.final_price, members.id, members.email, members.password "
                        + "FROM orders "
                        + "INNER JOIN members ON orders.member_id = members.id "
                        + "WHERE orders.id = ?";
        final List<Order> orders = jdbcTemplate.query(findByIdQuery, orderRowMapper(), id);

        if (orders.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orders.get(0));
    }

    private RowMapper<Order> orderRowMapper() {
        return (rs, rowNum) -> {
            final Member member = memberMapper(rs);

            final long orderId = rs.getLong("id");
            final LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            final int totalPrice = rs.getInt("total_price");
            final int finalPrice = rs.getInt("final_price");
            return new Order(orderId, createdAt, member, totalPrice, finalPrice);
        };
    }

    private Member memberMapper(final ResultSet rs) throws SQLException {
        final Long memberId = rs.getLong("members.id");
        final String email = rs.getString("members.email");
        final String password = rs.getString("members.password");
        return new Member(memberId, email, password);
    }
}
