package cart.domain;

import cart.domain.coupon.Coupon;
import cart.domain.value.Price;
import cart.exception.CartItemException;

import java.util.Objects;

public class CartItem {
    private Long id;
    private int quantity;
    private final Product product;
    private final Member member;

    public CartItem(Member member, Product product) {
        this.quantity = 1;
        this.member = member;
        this.product = product;
    }

    public CartItem(Long id, int quantity, Product product, Member member) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.member = member;
    }

    public CartItem(Long id, int quantity, Product product) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.member = null;
    }

    public Price applyCoupon(Coupon coupon) {
        return product.applyCoupon(coupon).multiply(quantity);
    }

    public Price getPrice() {
        return product.getPrice().multiply(quantity);
    }

    public Price getDiscountedPrice() {
        return product.getDiscountedPrice().multiply(quantity);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void checkOwner(Member member) {
        if (!Objects.equals(this.member.getId(), member.getId())) {
            throw new CartItemException.IllegalMember(this, member);
        }
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }
}
