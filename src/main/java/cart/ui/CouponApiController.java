package cart.ui;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import cart.application.CouponService;
import cart.domain.Member;
import cart.dto.request.MemberCouponAddRequest;
import cart.dto.response.CouponsResponse;
import cart.dto.response.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
@RequestMapping("/coupons")
public class CouponApiController {
    private final CouponService couponService;

    public CouponApiController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "발급 가능한 쿠폰 조회", description = "발급 가능한 쿠폰 리스트를 조회한다.")
    @ApiResponse(
        responseCode = "200",
        description = "쿠폰 조회 성공"
    )
    @GetMapping
    public ResponseEntity<CouponsResponse> getAllCoupons(Member member) {
        CouponsResponse couponsResponse = couponService.findAllCoupons();
        return ResponseEntity.ok().body(couponsResponse);
    }

    @Operation(summary = "쿠폰 발급", description = "회원이 쿠폰을 발급한다")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "쿠폰 발급 성공"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "쿠폰 발급 실패",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
        )
    })
    @PostMapping("/{couponId}")
    public ResponseEntity<Void> addMemberCoupon(Member member, @PathVariable Long couponId,
        @Valid @RequestBody MemberCouponAddRequest memberCouponAddRequest) {
        Long memberCouponId = couponService.addMemberCoupon(member, couponId, memberCouponAddRequest);
        return ResponseEntity.created(URI.create("/coupons/member/" + memberCouponId)).build();
    }
}
