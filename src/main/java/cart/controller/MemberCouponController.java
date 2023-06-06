package cart.controller;

import cart.auth.Auth;
import cart.auth.Credential;
import cart.domain.member.MemberCoupon;
import cart.dto.MemberCouponResponse;
import cart.service.MemberCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@SecurityRequirement(name = "basicAuth")
@Tag(name = "쿠폰", description = "쿠폰을 조회한다")
// TODO: member-coupons로 변경
@RequestMapping("/coupons")
@RestController
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    public MemberCouponController(final MemberCouponService memberCouponService) {
        this.memberCouponService = memberCouponService;
    }

    @Operation(summary = "사용자 쿠폰 조회", description = "사용자의 모든 쿠폰을 조회한다.")
    @GetMapping
    public ResponseEntity<List<MemberCouponResponse>> findAll(@Auth final Credential credential) {
        final List<MemberCoupon> memberCoupons = memberCouponService.findAllByMemberId(credential.getMemberId());
        final List<MemberCouponResponse> result = memberCoupons.stream()
                .map(MemberCouponResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
