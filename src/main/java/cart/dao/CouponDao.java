package cart.dao;

import cart.dao.entity.CouponEntity;
import cart.dao.entity.CouponTypeCouponResultMap;
import cart.dao.entity.CouponTypeEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CouponDao {

    private final NamedParameterJdbcOperations jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<CouponTypeCouponResultMap> couponTypeCouponResultMapRowMapper = (rs, num) ->
            new CouponTypeCouponResultMap(
                    rs.getLong("couponTypeId"),
                    rs.getLong("couponId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("discountAmount"),
                    rs.getBoolean("usageStatus")
            );

    public CouponDao(final NamedParameterJdbcOperations jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("coupon")
                .usingGeneratedKeyColumns("id");
    }

    public Long issue(final CouponEntity couponEntity) {
        final SqlParameterSource params = new BeanPropertySqlParameterSource(couponEntity);
        return jdbcInsert.executeAndReturnKey(params).longValue();
    }

    public List<CouponTypeCouponResultMap> findByMemberId(final Long memberId) {
        final String sql = "SELECT c.id couponId, " +
                "ct.id couponTypeId, " +
                "ct.name name, " +
                "ct.description description, " +
                "ct.discount_amount discountAmount, " +
                "c.usage_status usageStatus " +
                "FROM coupon c " +
                "JOIN coupon_type ct ON c.coupon_type_id = ct.id " +
                "WHERE c.member_id = :memberId";

        final Map<String, Long> params = Collections.singletonMap("memberId", memberId);

        return jdbcTemplate.query(sql, params, couponTypeCouponResultMapRowMapper);
    }

    public void changeStatus(final Long couponId, final Boolean toChange) {
        final String sql = "UPDATE coupon SET usage_status = :toChange " +
                "WHERE id = :couponId";

        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("toChange", toChange)
                .addValue("couponId", couponId);

        jdbcTemplate.update(sql, params);
    }

    public Optional<CouponTypeCouponResultMap> findById(final Long couponId) {
        final String sql = "SELECT c.id couponId, " +
                "ct.id couponTypeId, " +
                "ct.name name, " +
                "ct.description description, " +
                "ct.discount_amount discountAmount, " +
                "c.usage_status usageStatus " +
                "FROM coupon_type ct " +
                "JOIN coupon c ON c.coupon_type_id = ct.id " +
                "WHERE c.id = :couponId";

        final Map<String, Long> params = Collections.singletonMap("couponId", couponId);

        try {
            final CouponTypeCouponResultMap couponTypeEntity = jdbcTemplate.queryForObject(sql, params, couponTypeCouponResultMapRowMapper);
            return Optional.ofNullable(couponTypeEntity);
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<CouponTypeEntity> findAll() {
        final String sql = "SELECT ct.id couponTypeId, " +
                "ct.name name, " +
                "ct.description description, " +
                "ct.discount_amount discountAmount " +
                "FROM coupon_type ct";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new CouponTypeEntity(
                rs.getLong("couponTypeId"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("discountAmount")
        ));
    }

    public void deleteCoupon(final Long id) {
        final String sql = "DELETE FROM coupon WHERE id = :id";

        final Map<String, Long> params = Collections.singletonMap("id", id);
        jdbcTemplate.update(sql, params);
    }
}
