package cart.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import cart.application.MemberService;
import cart.domain.Member;
import cart.dto.request.OrderPostRequest;
import cart.dto.response.ProductInOrderResponse;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
public class OrderIntegrationTest extends IntegrationTest {
    @Autowired
    private MemberService memberService;

    @Test
    void 주문_상세_조회_테스트() {
        final Member member = memberService.findMemberById(1L);

        final ExtractableResponse<Response> response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .header(HttpHeaders.ORIGIN, "http://www.example.com") // check CORS
                .when()
                .get("/orders/{id}", 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getInt("priceBeforeDiscount")).isEqualTo(50_000);
        assertThat(response.jsonPath().getInt("priceAfterDiscount")).isEqualTo(45_000);
        final List<ProductInOrderResponse> productsResponse = new ArrayList<>(
                response.jsonPath().getList("products", ProductInOrderResponse.class)
        );
        assertThat(productsResponse).hasSize(2);
        assertThat(productsResponse).extracting("name").contains("치킨", "샐러드");
    }

    @Test
    void 주문_목록_조회_테스트() {
        final Member member = memberService.findMemberById(1L);

        final ExtractableResponse<Response> response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .header(HttpHeaders.ORIGIN, "http://www.example.com") // check CORS
                .when()
                .get("/orders")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("[0].mainProductName")).isEqualTo("치킨");
        assertThat(response.jsonPath().getInt("[0].extraProductCount")).isEqualTo(1);
        assertThat(response.jsonPath().getInt("[0].paymentAmount")).isEqualTo(45_000);
    }

    @Test
    void 주문_등록_테스트() {
        final Member member = memberService.findMemberById(1L);
        final OrderPostRequest request = new OrderPostRequest(List.of(1L, 2L), 95_000);

        final ExtractableResponse<Response> response = given().log().all()
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .header(HttpHeaders.ORIGIN, "http://www.example.com") // check CORS
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/orders")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        final long locationId = Long.parseLong(response.header("Location").split("/")[2]);

        final ExtractableResponse<Response> findOrderResponse = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic(member.getEmail(), member.getPassword())
                .header(HttpHeaders.ORIGIN, "http://www.example.com") // check CORS
                .when()
                .get("/orders/{id}", locationId)
                .then().log().all()
                .extract();

        assertThat(findOrderResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findOrderResponse.jsonPath().getInt("priceAfterDiscount")).isEqualTo(95_000);
    }
}
