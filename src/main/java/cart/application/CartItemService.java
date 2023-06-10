package cart.application;

import cart.domain.CartItem;
import cart.domain.Member;
import cart.domain.repository.CartItemRepository;
import cart.domain.repository.ProductRepository;
import cart.dto.request.CartItemQuantityUpdateRequest;
import cart.dto.request.CartItemRequest;
import cart.dto.request.RemoveCartItemsRequest;
import cart.dto.response.CartItemResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartItemService(final CartItemRepository cartItemRepository, final ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> findByMember(final Member member) {
        final List<CartItem> cartItems = cartItemRepository.findByMemberId(member.getId());
        return cartItems.stream().map(CartItemResponse::of).collect(Collectors.toList());
    }

    public Long save(final Member member, final CartItemRequest cartItemRequest) {
        final Optional<CartItem> cartItem = cartItemRepository.findByMemberIdAndProductId(member.getId(),
            cartItemRequest.getProductId());
        if (cartItem.isPresent()) {
            final CartItem updatedCartItem = cartItem.get().addQuantity(cartItemRequest.getQuantity());
            cartItemRepository.updateQuantity(updatedCartItem);
            return cartItem.get().getId();
        }
        return cartItemRepository.create(
                new CartItem(
                        cartItemRequest.getQuantity(),
                        productRepository.findById(cartItemRequest.getProductId()),
                        member)
        );
    }

    public void updateQuantity(final Member member, final Long id, final CartItemQuantityUpdateRequest request) {
        final CartItem cartItem = cartItemRepository.findById(id);
        cartItem.checkOwner(member);

        if (request.getQuantity() == 0) {
            cartItemRepository.deleteById(id);
            return;
        }

        cartItem.changeQuantity(request.getQuantity());
        cartItemRepository.updateQuantity(cartItem);
    }

    public void remove(final Member member, final Long id) {
        final CartItem cartItem = cartItemRepository.findById(id);
        cartItem.checkOwner(member);

        cartItemRepository.deleteById(id);
    }

    public void remove(final Member member, final RemoveCartItemsRequest request) {
        final List<CartItem> cartItems = cartItemRepository.findAllByIds(request.getCartItemIds());
        for (final CartItem cartItem : cartItems) {
            cartItem.checkOwner(member);
        }
        cartItemRepository.deleteAll(cartItems);
    }
}
