package shop.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import shop.application.member.dto.MemberJoinDto;
import shop.application.member.dto.MemberLoginDto;
import shop.domain.coupon.CouponType;
import shop.domain.member.Member;
import shop.domain.member.MemberName;
import shop.domain.member.Password;
import shop.domain.repository.MemberRepository;
import shop.util.Encryptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원가입을 할 수 있다.")
    @Test
    void joinTest() {
        MemberJoinDto request = new MemberJoinDto("testMember", "asdf1234");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/users/join")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("로그인을 할 수 있다.")
    @Test
    void loginTest() {
        //given
        String name = "testMember";
        String password = "test1234";
        MemberLoginDto request = new MemberLoginDto(name, password);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/users/join")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/users/login")
                .then()
                .log().all()
                .extract();

        //then
        String encryptedPassword = Encryptor.encrypt(password);
        String naturalToken = name + ":" + encryptedPassword;
        String encodedToken = Base64Utils.encodeToUrlSafeString(naturalToken.getBytes());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getString("token")).isEqualTo(encodedToken);
    }

    @DisplayName("회원가입시 쿠폰이 제공된다.")
    @Test
    void issueCouponIfJoin() {
        //given
        MemberLoginDto request = new MemberLoginDto("testName", "test1234");

        //when
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/users/join")
                .then()
                .log().all();

        String token = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/users/login")
                .then()
                .log().all()
                .extract()
                .jsonPath().getString("token");

        //then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "basic " + token)
                .when()
                .get("/users/me/coupons")
                .then()
                .log().all()
                .extract();

        List<String> names = response.jsonPath().getList("name", String.class);
        List<Integer> discountRates = response.jsonPath().getList("discountRate", Integer.class);
        assertThat(names).containsExactly(CouponType.WELCOME_JOIN.getName());
        assertThat(discountRates).containsExactly(CouponType.WELCOME_JOIN.getDiscountRate());
    }

    @DisplayName("중복된 이름으로 회원가입을 할 수 없다.")
    @Test
    void joinWithDuplicateNameTest() {
        //given
        Member member = new Member(new MemberName("testMember"), Password.createFromNaturalPassword("password"));
        MemberJoinDto request = new MemberJoinDto("testMember", "asdf1234");

        memberRepository.save(member);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request)
                .post("/users/join")
                .then()
                .log().all().extract();

        //then
        String errorMessage = response.jsonPath().getString("errorMessage");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorMessage).isEqualTo("중복되는 이름입니다. 입력한 회원 이름 : testMember");
    }
}
