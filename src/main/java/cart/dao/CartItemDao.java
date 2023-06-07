package cart.dao;

import cart.domain.cartItem.CartItem;
import cart.domain.member.Member;
import cart.domain.product.Product;
import cart.exception.CartItemException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartItemDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapper<CartItem> cartItemRowMapper = (rs, rowNum) -> {
        Long memberId = rs.getLong("member_id");
        String email = rs.getString("email");
        String password = rs.getString("password");
        int point = rs.getInt("point");
        Long productId = rs.getLong("product.id");
        String name = rs.getString("name");
        int price = rs.getInt("price");
        String imageUrl = rs.getString("image_url");
        Long cartItemId = rs.getLong("cart_item.id");
        int quantity = rs.getInt("cart_item.quantity");
        Member member = new Member(memberId, email, password, point);
        Product product = new Product(productId, name, price, imageUrl);
        return new CartItem(cartItemId, quantity, product, member);
    };

    public CartItemDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("cart_item")
                .usingGeneratedKeyColumns("id");
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public List<CartItem> findByMemberId(Long memberId) {
        final String sql = "SELECT cart_item.id, cart_item.member_id, member.email, member.password, member.point, product.id, product.name, product.price, product.image_url, cart_item.quantity " +
                "FROM cart_item " +
                "INNER JOIN member ON cart_item.member_id = member.id " +
                "INNER JOIN product ON cart_item.product_id = product.id " +
                "WHERE cart_item.member_id = ?";
        return jdbcTemplate.query(sql, cartItemRowMapper, memberId);
    }

    public Long save(CartItem cartItem) {
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("member_id", cartItem.getMember().getId())
                .addValue("product_id", cartItem.getProduct().getId())
                .addValue("quantity", cartItem.getQuantity());
        return simpleJdbcInsert.executeAndReturnKey(params).longValue();
    }

    public CartItem findById(Long id) {
        try {
            final String sql = "SELECT cart_item.id, cart_item.member_id, member.email, member.password, member.point, product.id, product.name, product.price, product.image_url, cart_item.quantity " +
                    "FROM cart_item " +
                    "INNER JOIN member ON cart_item.member_id = member.id " +
                    "INNER JOIN product ON cart_item.product_id = product.id " +
                    "WHERE cart_item.id = ?";
            return jdbcTemplate.queryForObject(sql, cartItemRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new CartItemException.IllegalId(id);
        }
    }

    public int delete(Long memberId, Long productId) {
        String sql = "DELETE FROM cart_item WHERE member_id = ? AND product_id = ?";
        return jdbcTemplate.update(sql, memberId, productId);
    }

    public int deleteById(Long id) {
        final String sql = "DELETE FROM cart_item WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int deleteByIdIn(final List<Long> ids) {
        final String sql = "DELETE FROM cart_item WHERE id IN (:ids)";
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ids", ids);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    public int updateQuantity(CartItem cartItem) {
        final String sql = "UPDATE cart_item SET quantity = ? WHERE id = ?";
        return jdbcTemplate.update(sql, cartItem.getQuantity(), cartItem.getId());
    }
}

