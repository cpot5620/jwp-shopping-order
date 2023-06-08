package cart.controller.cart.dto;

import java.util.List;
import java.util.stream.Collectors;

public class CouponDiscountRequest {
    private List<CouponRequest> coupons;

    public CouponDiscountRequest() {
    }

    public List<CouponRequest> getCoupons() {
        return coupons;
    }

    public List<Long> getCouponIds() {
        return coupons.stream()
                .map(CouponRequest::getId)
                .collect(Collectors.toList());
    }
}
