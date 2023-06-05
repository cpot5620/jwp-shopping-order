package cart.fixture;

import static cart.fixture.DomainFixture.CHICKEN;
import static cart.fixture.DomainFixture.PIZZA;

import cart.dto.request.CartItemQuantityUpdateRequest;
import cart.dto.request.CartItemRequest;
import cart.dto.request.ProductRequest;

public class DtoFixture {

    public static final ProductRequest CHICKEN_PRODUCT_REQUEST = new ProductRequest(CHICKEN.getName(), CHICKEN.getPrice(),
            CHICKEN.getImageUrl());
    public static final CartItemRequest PIZZA_CART_ITEM_REQUEST = new CartItemRequest(PIZZA.getId());
    public static final CartItemQuantityUpdateRequest THREE_QUANTITY_REQUEST = new CartItemQuantityUpdateRequest(3);
}
