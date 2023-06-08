package cart.fixtures;

import static cart.fixtures.MemberFixtures.*;
import static cart.fixtures.ProductFixtures.*;

import cart.domain.cartitem.application.dto.CartItemResponse;
import cart.domain.cartitem.domain.CartItem;
import cart.domain.member.domain.Member;
import cart.domain.product.application.dto.ProductCartItemResponse;
import cart.domain.product.domain.Product;

public class CartItemFixtures {

    public static class Dooly_CartItem1 {
        public static final Long ID = 1L;
        public static final int QUANTITY = 2;
        public static final Product PRODUCT = CHICKEN.ENTITY();
        public static final Member MEMBER = Dooly.ENTITY();
        public static final int PRICE = PRODUCT.getPrice() * QUANTITY;

        public static CartItem ENTITY() {
            return new CartItem(ID, QUANTITY, PRODUCT, MEMBER);
        }
        public static final CartItemResponse RESPONSE = CartItemResponse.from(ENTITY());
        public static final ProductCartItemResponse PRODUCT_CART_ITEM_RESPONSE = ProductCartItemResponse.createContainsCartItem(PRODUCT, ENTITY());
    }

    public static class Dooly_CartItem2 {
        public static final Long ID = 2L;
        public static final int QUANTITY = 4;
        public static final Product PRODUCT = SALAD.ENTITY();
        public static final Member MEMBER = Dooly.ENTITY();
        public static final int PRICE = PRODUCT.getPrice() * QUANTITY;

        public static CartItem ENTITY() {
            return new CartItem(ID, QUANTITY, PRODUCT, MEMBER);
        }
        public static final CartItemResponse RESPONSE = CartItemResponse.from(ENTITY());
    }

    public static class Dooly_CartItem3 {
        public static final Long ID = 3L;
        public static final int QUANTITY = 5;
        public static final Product PRODUCT = PANCAKE.ENTITY();
        public static final Member MEMBER = Dooly.ENTITY();
        public static final int PRICE = PRODUCT.getPrice() * QUANTITY;

        public static CartItem ENTITY() {
            return new CartItem(ID, QUANTITY, PRODUCT, MEMBER);
        }
    }

    public static class Ber_CartItem1 {
        public static final Long ID = 4L;
        public static final int QUANTITY = 5;
        public static final Product PRODUCT = CHICKEN.ENTITY();
        public static final Member MEMBER = Ber.ENTITY();

        public static CartItem ENTITY() {
            return new CartItem(ID, QUANTITY, PRODUCT, MEMBER);
        }
    }

    public static class Ber_CartItem2 {
        public static final Long ID = 5L;
        public static final int QUANTITY = 5;
        public static final Product PRODUCT = PIZZA.ENTITY();
        public static final Member MEMBER = Ber.ENTITY();

        public static CartItem ENTITY() {
            return new CartItem(ID, QUANTITY, PRODUCT, MEMBER);
        }
    }

    public static class Bixx_CartItem1 {
        public static final Long ID = 6L;
        public static final int QUANTITY = 5;
        public static final Product PRODUCT = SALAD.ENTITY();
        public static final Member MEMBER = Bixx.ENTITY();

        public static CartItem ENTITY() {
            return new CartItem(ID, QUANTITY, PRODUCT, MEMBER);
        }
    }
}