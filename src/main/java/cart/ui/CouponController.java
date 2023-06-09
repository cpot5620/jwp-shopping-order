package cart.ui;


import cart.application.CouponService;
import cart.domain.Member;
import cart.dto.response.MemberCouponResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ResponseEntity<List<MemberCouponResponse>> findAllByMember(Member member) {
        List<MemberCouponResponse> response = couponService.findAllByMemberId(member.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
