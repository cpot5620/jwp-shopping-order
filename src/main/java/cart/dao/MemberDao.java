package cart.dao;

import cart.domain.Member;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public MemberDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Member> getMemberById(final Long id) {
        final String sql = "SELECT * FROM member WHERE id = ?";
        try {
            final Member member = jdbcTemplate.queryForObject(sql, new MemberRowMapper(), id);
            return Optional.of(member);
        } catch (final IncorrectResultSizeDataAccessException exception) {
            return Optional.empty();
        }
    }

    public Optional<Member> getMemberByEmail(final String email) {
        final String sql = "SELECT * FROM member WHERE email = ?";
        try {
            final Member member = jdbcTemplate.queryForObject(sql, new MemberRowMapper(), email);
            return Optional.of(member);
        } catch (final IncorrectResultSizeDataAccessException exception) {
            return Optional.empty();
        }
    }

    public void addMember(final Member member) {
        final String sql = "INSERT INTO member (email, password, points) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getPoints());
    }

    public void updateMember(final Member member) {
        final String sql = "UPDATE member SET email = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getId());
    }

    public void updateMemberPoint(final Member member) {
        final String sql = "UPDATE member SET points = ? WHERE id = ?";
        jdbcTemplate.update(sql, member.getPoints(), member.getId());
    }

    public void deleteMember(final Long id) {
        final String sql = "DELETE FROM member WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Member> getAllMembers() {
        final String sql = "SELECT * from member";
        return jdbcTemplate.query(sql, new MemberRowMapper());
    }

    private static class MemberRowMapper implements RowMapper<Member> {
        @Override
        public Member mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return new Member(rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getInt("points")
            );
        }
    }
}

