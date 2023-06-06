package cart.acceptance;

import cart.dto.MemberCouponResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CouponAcceptanceTest extends AcceptanceTest {

    @Test
    void 쿠폰을_조회한다() {
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .auth().preemptive().basic("pizza1@pizza.com", "pizza")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/coupons")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<MemberCouponResponse> couponResponses = response.jsonPath().getList("", MemberCouponResponse.class);
        final MemberCouponResponse couponResponse = couponResponses.get(0);
        assertAll(
                () -> assertThat(couponResponses.size()).isEqualTo(2),
                () -> assertThat(couponResponse.getId()).isEqualTo(1),
                () -> assertThat(couponResponse.getName()).isEqualTo("30000원 이상 3000원 할인 쿠폰"),
                () -> assertThat(couponResponse.getType()).isEqualTo("price"),
                () -> assertThat(couponResponse.getValue().longValue()).isEqualTo(3000),
                () -> assertThat(couponResponse.getMinimumPrice().longValue()).isEqualTo(30000)
        );
    }
}
