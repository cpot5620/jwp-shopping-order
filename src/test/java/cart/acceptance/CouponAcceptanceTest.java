package cart.acceptance;

import cart.controller.dto.CouponReissueRequest;
import cart.dao.MemberDao;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.*;

@SuppressWarnings("NonAsciiCharacters")
@Import(MemberDao.class)
public class CouponAcceptanceTest extends AcceptanceTest {

    /**
     * given 쿠폰 ID와 사용자 정보를 가지고
     * when 발급 요청을 하면
     * then 성공한다
     */
    @Test
    void 쿠폰을_사용자에게_발급해준다() {
        final ExtractableResponse<Response> 결과 = 쿠폰을_발급한다(1L);

        // then
        assertAll(
                () -> assertThat(결과.statusCode()).isEqualTo(CREATED.value()),
                () -> assertThat(결과.header(HttpHeaders.LOCATION)).contains("/coupons/")
        );
    }

    private ExtractableResponse<Response> 쿠폰을_발급한다(long couponId) {
        return givenBasic()
                .when()
                .post("/coupons/{couponId}", couponId)
                .then().log().all()
                .extract();
    }

    /**
     * given 쿠폰 아이디와 회원 정보를 주어지면
     * when 사용한 쿠폰을 재발급 요청하면
     * then 쿠폰이 재발급 된다.
     */
    @Test
    void 사용한_쿠폰을_재발급_한다() {
        // given
        final CouponReissueRequest request = new CouponReissueRequest(1L, "a@a.com", "1234");

        // when
        final ExtractableResponse<Response> response = 쿠폰을_재발급_한다(request, 1L);

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    private ExtractableResponse<Response> 쿠폰을_재발급_한다(final CouponReissueRequest request, final long couponId) {
        return givenBasic()
                .body(request)
                .patch("/coupons/{couponId}", couponId)
                .then().log().all()
                .extract();
    }

    /**
     * when 회원이 쿠폰 조회를 요청하면
     * then 해당 회원이 가지고 있는 쿠폰 전부를 반환한다.
     */
    @Test
    void 회원이_소유한_있는_쿠폰을_반환한다() {
        // when
        final ExtractableResponse<Response> 회원_쿠폰 = 회원이_소유한_쿠폰을_찾는다();

        // then
        final JsonPath result = 회원_쿠폰.jsonPath();
        assertAll(
                () -> assertThat(회원_쿠폰.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(result.getList("id", Long.class)).hasSize(3),
                () -> assertThat(result.getList("discountAmount", Integer.class)).containsExactly(1000, 3000, 5000)
        );
    }

    private ExtractableResponse<Response> 회원이_소유한_쿠폰을_찾는다() {
        return givenBasic()
                .get("/coupons/member")
                .then().log().all()
                .extract();
    }

    /**
     * when 모든 쿠폰 조회 요청이 들어오면
     * then 모든 쿠폰을 반환한다
     */
    @Test
    void 모든_쿠폰을_반환한다() {
        // when
        final ExtractableResponse<Response> 전체_쿠폰 = 전체_쿠폰을_조회한다();

        // then
        assertAll(
                () -> assertThat(전체_쿠폰.statusCode()).isEqualTo(OK.value()),
                () -> assertThat(전체_쿠폰.jsonPath().getList("discountAmount", Integer.class))
                        .containsExactly(1000, 3000, 5000, 10000)
        );
    }

    private ExtractableResponse<Response> 전체_쿠폰을_조회한다() {
        return givenBasic()
                .when()
                .get("/coupons")
                .then().log().all()
                .extract();
    }

    /**
     * given 쿠폰 아이디가 주어졌을 때
     * when 쿠폰 삭제 요청을 하면
     * then 쿠폰이 삭제된다.
     */
    @Test
    void 쿠폰을_삭제한다() {
        // given
        final long couponId = 1L;

        // when
        final ExtractableResponse<Response> 쿠폰_삭제_결과 = 쿠폰을_삭제한다(couponId);

        // then
        assertThat(쿠폰_삭제_결과.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 쿠폰을_삭제한다(final long couponId) {
        return givenBasic()
                .when()
                .delete("/coupons/{couponId}", couponId)
                .then().log().all()
                .extract();
    }

}
