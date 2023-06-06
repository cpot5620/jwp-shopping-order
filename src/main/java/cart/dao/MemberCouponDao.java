package cart.dao;

import cart.entity.MemberCouponEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberCouponDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<MemberCouponEntity> rowMapper = (rs, rowNum) -> {
        final Long id = rs.getLong("id");
        final Long memberId = rs.getLong("member_id");
        final Long couponId = rs.getLong("coupon_id");
        final boolean used = rs.getBoolean("used");
        return new MemberCouponEntity(id, memberId, couponId, used);
    };

    public MemberCouponDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("member_coupon")
                .usingColumns("coupon_id", "member_id", "used")
                .usingGeneratedKeyColumns("id");
    }

    public MemberCouponEntity insert(final MemberCouponEntity memberCouponEntity) {
        final BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(memberCouponEntity);
        final long id = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        return new MemberCouponEntity(
                id,
                memberCouponEntity.getMemberId(),
                memberCouponEntity.getCouponId(),
                memberCouponEntity.isUsed()
        );
    }

    public void update(final MemberCouponEntity memberCouponEntity) {
        String sql = "UPDATE member_coupon SET used = ? WHERE id = ?";
        jdbcTemplate.update(sql, memberCouponEntity.isUsed(), memberCouponEntity.getId());
    }

    public List<MemberCouponEntity> findByMemberIdWithUsed(final Long memberId, final boolean used) {
        String sql = "SELECT * FROM member_coupon WHERE member_id = ? AND used = ?";
        return jdbcTemplate.query(sql, rowMapper, memberId, used);
    }

    public Optional<MemberCouponEntity> findById(final Long id) {
        String sql = "SELECT * FROM member_coupon WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
