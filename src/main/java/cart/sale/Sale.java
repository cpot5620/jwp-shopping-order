package cart.sale;

import cart.discountpolicy.DiscountPolicy;

public class Sale {
    private final Long id;
    private final String name;
    private final DiscountPolicy discountPolicy;

    public Sale(Long id, String name, DiscountPolicy discountPolicy) {
        this.id = id;
        this.name = name;
        this.discountPolicy = discountPolicy;
    }

    public DiscountPolicy getDiscountPolicy() {
        return discountPolicy;
    }
}
