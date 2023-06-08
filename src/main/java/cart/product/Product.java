package cart.product;

import cart.cartitem.CartItem;
import cart.discountpolicy.DiscountPolicy;
import cart.discountpolicy.application.builder.DiscountUnitPolicy;
import cart.discountpolicy.discountcondition.DiscountCondition;
import cart.discountpolicy.discountcondition.DiscountTarget;

import java.util.Objects;

public class Product {
    private final Long id;
    private final String name;
    private final int price;
    private final String imageUrl;
    private int discountPrice;

    public Product(String name, int price, String imageUrl) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.discountPrice = 0;
        this.imageUrl = imageUrl;
    }

    public Product(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discountPrice = 0;
        this.imageUrl = imageUrl;
    }

    public void addDiscountPrice(int discountPrice) {
        this.discountPrice += discountPrice;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void discount(DiscountPolicy discountPolicy) {
        if (discountPolicy.isTarget(DiscountTarget.SPECIFIC)) {
            if (discountPolicy.isApplied(id)) {
                addDiscountPrice(discountPolicy.calculateDiscountPrice(price));
            }
            return;
        }

        if (discountPolicy.isTarget(DiscountTarget.ALL)) {
            addDiscountPrice(discountPolicy.calculateDiscountPrice(price));
            return;
        }

        throw new IllegalArgumentException("할인정책을 진행할 수 없는 조건입니다.");
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return price == product.price && Objects.equals(id, product.id) && Objects.equals(name, product.name) && Objects.equals(imageUrl, product.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, imageUrl);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
