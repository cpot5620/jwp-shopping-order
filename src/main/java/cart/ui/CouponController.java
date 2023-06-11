package cart.ui;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cart.application.CouponService;
import cart.domain.Member;
import cart.domain.MemberCoupon;
import cart.dto.MemberCouponIssueRequest;
import cart.dto.MemberCouponResponse;

@RestController
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<MemberCouponResponse>> showMemberCoupons(Member member) {
        List<MemberCoupon> memberCoupons = couponService.getMemberCouponsOf(member);

        return ResponseEntity.ok(MemberCouponResponse.of(memberCoupons));
    }

    @PostMapping("/member/coupons")
    public ResponseEntity<Void> issueCouponTo(Member member, @RequestBody MemberCouponIssueRequest issueRequest) {
        couponService.issueCouponTo(member, issueRequest.getCouponId());

        return ResponseEntity.noContent().build();
    }
}
