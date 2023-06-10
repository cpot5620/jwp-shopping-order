package cart.domain;

import cart.domain.vo.Amount;
import java.util.List;

public class Coupon {

    private final Long id;
    private final String name;
    private final Amount discountAmount;
    private final Amount minAmount;

    public Coupon(final String name, final Amount discountAmount, final Amount minAmount) {
        this(null, name, discountAmount, minAmount);
    }

    public Coupon(final Long id, final String name, final Amount discountAmount, final Amount minAmount) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.minAmount = minAmount;
    }

    public static Coupon empty() {
        return new Coupon(null, "", Amount.of(0), Amount.of(0));
    }

    public int calculateDiscountedAmount(final int total) {
        if (discountAmount.getValue() >= total) {
            return 0;
        }
        return total - discountAmount.getValue();
    }

    public boolean isActive(final List<Long> memberCouponIds, final int totalProductAmount) {
        return memberCouponIds.contains(id) && minAmount.getValue() <= totalProductAmount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Amount getDiscountAmount() {
        return discountAmount;
    }

    public Amount getMinAmount() {
        return minAmount;
    }
}
