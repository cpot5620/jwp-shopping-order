package shop.application.member.dto;

import shop.domain.coupon.MemberCoupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MemberCouponDto {
    private final Long id;
    private final String name;
    private final Integer discountRate;
    private final LocalDateTime expiredAt;
    private final Boolean isUsed;

    private MemberCouponDto(Long id, String name, Integer discountRate,
                            LocalDateTime expiredAt, Boolean isUsed) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.expiredAt = expiredAt;
        this.isUsed = isUsed;
    }

    public static MemberCouponDto of(MemberCoupon memberCoupon) {
        return new MemberCouponDto(
                memberCoupon.getCouponId(),
                memberCoupon.getCouponName(),
                memberCoupon.getDiscountRate(),
                memberCoupon.getMemberCouponExpiredAt(),
                memberCoupon.isUsed()
        );
    }

    public static List<MemberCouponDto> of(List<MemberCoupon> memberCoupons) {
        return memberCoupons.stream()
                .map(MemberCouponDto::of)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public Boolean isUsed() {
        return isUsed;
    }
}
