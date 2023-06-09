package cart.product.integration;

import cart.config.IntegrationTest;
import cart.product.ui.request.ProductRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static cart.fixtures.MemberFixtures.Member_Dooly;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class ProductIntegrationTest extends IntegrationTest {

    @Test
    void 모든_상품_목록을_조회하다() {
        final ExtractableResponse<Response> result = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/products")
                .then()
                .extract();

        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 특정_상품_목록을_페이징해서_조회하다() {
        final ExtractableResponse<Response> response = given()
                .auth().preemptive().basic(Member_Dooly.EMAIL, Member_Dooly.PASSWORD)
                .param("lastId", 3)
                .param("pageItemCount", 2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/products/cart-items")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 특정_상품을_조회하다() {
        final ExtractableResponse<Response> result = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/products/" + 1L)
                .then()
                .extract();

        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 유저와_상품_id를_통해_장바구니를_가져오다() {
        final ExtractableResponse<Response> response = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().preemptive().basic(Member_Dooly.EMAIL, Member_Dooly.PASSWORD)
                .when()
                .get("/products/" + 1L + "/cart-items")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 상품을_추가하다() {
        final ProductRequest request = ProductRequest.of("치킨", 10_000, "http://example.com/chicken.jpg");

        final ExtractableResponse<Response> response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/products")
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    void 상품을_수정하다() {
        final ProductRequest product = ProductRequest.of("피자", 15_000, "http://example.com/pizza.jpg");

        final ExtractableResponse<Response> response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(product)
                .when()
                .put("/products/" + 1L)
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 상품을_삭제하다() {
        final ExtractableResponse<Response> response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/products/" + 1L)
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
