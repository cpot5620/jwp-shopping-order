package cart.domain.product.application.dto;

import cart.domain.cartitem.application.dto.CartItemQuantityResponse;
import cart.domain.cartitem.domain.CartItem;
import cart.domain.product.domain.Product;

public class ProductCartItemResponse {

    private final ProductResponse product;
    private final CartItemQuantityResponse cartItem;

    public ProductCartItemResponse(ProductResponse product, CartItemQuantityResponse cartItem) {
        this.product = product;
        this.cartItem = cartItem;
    }

    public static ProductCartItemResponse createContainsCartItem(Product product, CartItem cartItem) {
        return new ProductCartItemResponse(
                ProductResponse.from(product),
                CartItemQuantityResponse.from(cartItem)
        );
    }

    public static ProductCartItemResponse createOnlyProduct(Product product) {
        return new ProductCartItemResponse(
                ProductResponse.from(product),
                null
        );
    }

    public ProductResponse getProduct() {
        return product;
    }

    public CartItemQuantityResponse getCartItem() {
        return cartItem;
    }
}