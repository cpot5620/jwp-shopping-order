package cart.dao;

import cart.domain.point.Point;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRewardPointDao {

    private static final RowMapper<Point> rowMapper = (rs, rowNum) ->
            new Point(
                    rs.getLong("id"),
                    rs.getInt("point"),
                    rs.getTimestamp("created_at")
                      .toLocalDateTime(),
                    rs.getTimestamp("expired_at")
                      .toLocalDateTime()
            );

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    public MemberRewardPointDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("member_reward_point")
                .usingGeneratedKeyColumns("id");
    }

    public Long save(Long memberId, Point point, Long orderId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", memberId)
                .addValue("point", point.calculatePointByExpired())
                .addValue("created_at", point.getCreatedAt())
                .addValue("expired_at", point.getExpiredAt())
                .addValue("reward_order_id", orderId);
        return insertAction.executeAndReturnKey(params).longValue();
    }

    public List<Point> getAllByMemberId(Long memberId) {
        String sql = "SELECT id, point, created_at, expired_at FROM member_reward_point WHERE member_id = :member_id";
        SqlParameterSource source = new MapSqlParameterSource("member_id", memberId);
        return namedParameterJdbcTemplate.query(sql, source, rowMapper);
    }

    public Optional<Point> getPointByOrderId(Long orderId) {
        String sql = "SELECT id, point, created_at, expired_at FROM member_reward_point WHERE reward_order_id = :reward_order_id";
        try {
            SqlParameterSource source = new MapSqlParameterSource("reward_order_id", orderId);
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, source, rowMapper));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public void updatePoints(List<Point> points) {
        String sql = "UPDATE member_reward_point SET point = ? WHERE id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Point point = points.get(i);
                        ps.setLong(1, point.getPointAmount());
                        ps.setLong(2, point.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return points.size();
                    }
                }
        );
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM member_reward_point WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
