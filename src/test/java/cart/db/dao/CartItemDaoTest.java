package cart.db.dao;

import cart.db.entity.CartItemEntity;
import cart.db.entity.MemberEntity;
import cart.db.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@JdbcTest
class CartItemDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private CartItemDao cartItemDao;
    private ProductDao productDao;
    private MemberDao memberDao;

    @BeforeEach
    void setUp() {
        cartItemDao = new CartItemDao(jdbcTemplate);
        productDao = new ProductDao(jdbcTemplate);
        memberDao = new MemberDao(jdbcTemplate);
    }

    @Test
    void 장바구니에_상품을_저장한다() {
        // given
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();

        // when
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        Long cartItemId = cartItemDao.save(cartItemEntity);

        // then
        assertThat(cartItemId).isPositive();
    }

    @Test
    void 사용자_id로_장바구니_상품을_조회한다() {
        // given
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        cartItemDao.save(cartItemEntity);

        // when
        List<CartItemEntity> cartItems = cartItemDao.findByMemberId(member.getId());

        // then
        assertThat(cartItems).hasSize(1);
    }

    @Test
    void 장바구니_상품을_조회할_때_삭제된_상품이_안나온다() {
        // given
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        cartItemDao.save(cartItemEntity);
        productDao.deleteById(product.getId());

        // when
        List<CartItemEntity> cartItems = cartItemDao.findByMemberId(member.getId());

        // then
        assertThat(cartItems).hasSize(0);
    }

    @Test
    void 장바구니_상품을_id로_조회한다() {
        // given
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        Long cartItemId = cartItemDao.save(cartItemEntity);

        // when
        Optional<CartItemEntity> savedCartItem = cartItemDao.findById(cartItemId);

        // then
        assertThat(savedCartItem).isPresent();
    }

    @Test
    void 장바구니_상품을_삭제한다() {
        // given
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        Long cartItemId = cartItemDao.save(cartItemEntity);

        // when
        cartItemDao.deleteById(cartItemId);

        // then
        assertThat(cartItemDao.findById(cartItemId)).isEmpty();
    }

    @Test
    void 장바구니_상품_수량을_변경한다() {
        // given
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        Long cartItemId = cartItemDao.save(cartItemEntity);

        // when
        cartItemDao.updateQuantity(new CartItemEntity(cartItemId, product, member, 100));

        // then
        Optional<CartItemEntity> savedCartItem = cartItemDao.findById(cartItemId);
        assertThat(savedCartItem).isPresent();
        assertThat(savedCartItem.get().getQuantity()).isEqualTo(100);
    }

    @Test
    void 상품을_모두_삭제한다() {
        //given
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        Long cartItemId = cartItemDao.save(cartItemEntity);
        Long cartItemId1 = cartItemDao.save(cartItemEntity);
        Long cartItemId2 = cartItemDao.save(cartItemEntity);

        // when
        cartItemDao.deleteAllByIds(List.of(cartItemId1, cartItemId2, cartItemId));

        assertThat(cartItemDao.findById(cartItemId)).isEmpty();
        assertThat(cartItemDao.findById(cartItemId1)).isEmpty();
        assertThat(cartItemDao.findById(cartItemId2)).isEmpty();
    }

    @Test
    void 상품을_모두_조회한다() {
        ProductEntity product = getProductEntity();
        MemberEntity member = getMemberEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(product, member);
        Long cartItemId = cartItemDao.save(cartItemEntity);
        Long cartItemId1 = cartItemDao.save(cartItemEntity);
        Long cartItemId2 = cartItemDao.save(cartItemEntity);

        List<CartItemEntity> cartItemEntities = cartItemDao.findAllByIds(List.of(cartItemId1, cartItemId2, cartItemId));

        assertThat(cartItemEntities).usingRecursiveComparison()
                .ignoringActualNullFields()
                .ignoringExpectedNullFields()
                .isEqualTo(List.of(cartItemEntity, cartItemEntity, cartItemEntity));
    }

    private MemberEntity getMemberEntity() {
        Long memberId = memberDao.save(new MemberEntity("email1@email.com", "password"));
        return memberDao.findById(memberId).get();
    }

    private ProductEntity getProductEntity() {
        Long productId = productDao.save(
                new ProductEntity("밀리", BigDecimal.valueOf(1_000_000_000), "http://millie.com"));
        return productDao.findById(productId).get();
    }
}