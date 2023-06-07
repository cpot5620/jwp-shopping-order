package cart.service;

import cart.dao.CartItemDao;
import cart.dao.ProductDao;
import cart.domain.cartItem.CartItem;
import cart.domain.member.Member;
import cart.dto.CartItemQuantityUpdateRequest;
import cart.dto.CartItemRequest;
import cart.dto.CartItemResponse;
import cart.exception.CartItemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartItemService {
    private final ProductDao productDao;
    private final CartItemDao cartItemDao;

    public CartItemService(ProductDao productDao, CartItemDao cartItemDao) {
        this.productDao = productDao;
        this.cartItemDao = cartItemDao;
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> findByMember(Member member) {
        List<CartItem> cartItems = cartItemDao.findByMemberId(member.getId());
        return cartItems.stream().map(CartItemResponse::of).collect(Collectors.toList());
    }

    public Long add(Member member, CartItemRequest cartItemRequest) {
        final Long productId = cartItemRequest.getProductId();
        final List<CartItem> cartItems = cartItemDao.findByMemberId(member.getId());

        cartItems.forEach(item -> item.checkProductDuplication(productId));

        return cartItemDao.save(CartItem.from(productDao.getProductById(productId), member));
    }

    public void updateQuantity(Member member, Long id, CartItemQuantityUpdateRequest request) {
        CartItem cartItem = cartItemDao.findById(id);
        cartItem.checkOwner(member);

        if (request.getQuantity() == 0) {
            cartItemDao.deleteById(id);
            return;
        }

        cartItem.changeQuantity(request.getQuantity());
        cartItemDao.updateQuantity(cartItem);
    }

    public void remove(Member member, Long id) {
        CartItem cartItem = cartItemDao.findById(id);
        cartItem.checkOwner(member);

        cartItemDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public CartItemResponse findByProductId(final Member member, final Long productId) {
        final List<CartItem> cartItems = cartItemDao.findByMemberId(member.getId());

        final CartItem cartItem = cartItems.stream()
                .filter(item -> item.hasSameProduct(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemException.IllegalProduct(productId));

        return CartItemResponse.of(cartItem);
    }
}
