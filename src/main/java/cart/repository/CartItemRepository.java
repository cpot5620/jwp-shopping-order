package cart.repository;

import static cart.exception.ErrorMessage.NOT_FOUND_CART_ITEM;

import cart.dao.CartItemDao;
import cart.dao.entity.CartItemEntity;
import cart.dao.entity.CartItemWithMemberAndProductEntity;
import cart.dao.entity.MemberEntity;
import cart.dao.entity.ProductEntity;
import cart.domain.CartItem;
import cart.domain.CartItems;
import cart.domain.Member;
import cart.domain.Product;
import cart.exception.CartItemException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CartItemRepository {
    private final CartItemDao cartItemDao;

    public CartItemRepository(final CartItemDao cartItemDao) {
        this.cartItemDao = cartItemDao;
    }

    public Long save(CartItem cartItem) {
        CartItemEntity cartItemEntity = toEntity(cartItem);

        return cartItemDao.save(cartItemEntity);
    }

    public CartItems findByMemberId(Member member) {
        List<CartItemWithMemberAndProductEntity> cartItemEntities = cartItemDao.findByMemberId(member.getId());

        List<CartItem> cartItems = cartItemEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return new CartItems(cartItems);
    }

    public CartItem findByMemberAndProduct(Member member, Product product) {
        CartItemWithMemberAndProductEntity cartItemEntity = cartItemDao.findByMemberIdAndProductId(
                        member.getId(), product.getId())
                .orElseThrow(() -> new CartItemException(NOT_FOUND_CART_ITEM));

        return toDomain(cartItemEntity);
    }

    public CartItem findById(Long id) {
        CartItemWithMemberAndProductEntity cartItemWithMemberAndProductEntity = cartItemDao.findById(id)
                .orElseThrow(() -> new CartItemException(NOT_FOUND_CART_ITEM));

        return toDomain(cartItemWithMemberAndProductEntity);
    }

    public CartItems findByMemberAndCartItemIds(Member member, List<Long> cartItemIds) {
        CartItems cartItems = findByMemberId(member);

        return cartItems.getContainedCartItems(cartItemIds);
    }

    public boolean existCartItem(Member member, Product product) {
        return cartItemDao.existByMemberIdAndProductId(member.getId(), product.getId());
    }

    public void updateQuantity(CartItem cartItem) {
        CartItemEntity cartItemEntity = toEntity(cartItem);

        int updatedRow = cartItemDao.updateQuantity(cartItemEntity);
        if (updatedRow == 0) {
            throw new CartItemException(NOT_FOUND_CART_ITEM);
        }
    }

    public void deleteById(Long id) {
        int deletedRow = cartItemDao.deleteById(id);

        if (deletedRow == 0) {
            throw new CartItemException(NOT_FOUND_CART_ITEM);
        }
    }

    public void deleteByMemberId(Long memberId) {
        int deletedRow = cartItemDao.deleteByMemberId(memberId);

        if (deletedRow == 0) {
            throw new CartItemException(NOT_FOUND_CART_ITEM);
        }
    }

    private CartItemEntity toEntity(CartItem cartItem) {
        Member member = cartItem.getMember();
        Product product = cartItem.getProduct();

        return new CartItemEntity(cartItem.getId(),
                member.getId(),
                product.getId(),
                cartItem.getQuantity(),
                null, null
        );
    }

    private CartItem toDomain(CartItemWithMemberAndProductEntity cartItemEntity) {
        MemberEntity memberEntity = cartItemEntity.getMemberEntity();
        Member member = new Member(
                memberEntity.getId(),
                memberEntity.getEmail(),
                memberEntity.getPassword(),
                memberEntity.getPoint()
        );

        ProductEntity productEntity = cartItemEntity.getProductEntity();

        Product product = new Product(productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getImageUrl()
        );

        CartItemEntity cartItem = cartItemEntity.getCartItemEntity();
        return new CartItem(cartItem.getId(), cartItem.getQuantity(), product, member);
    }

    public void deleteOrderedCartItem(List<Long> cartItemIds) {
        cartItemDao.deleteByIds(cartItemIds);
    }
}
