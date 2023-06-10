package cart.ui;

import cart.Fixture;
import cart.WebMvcConfig;
import cart.application.CartItemService;
import cart.domain.Member;
import cart.dto.CartItemQuantityUpdateRequest;
import cart.dto.CartItemRequest;
import cart.dto.CartItemResponse;
import cart.dto.CheckoutResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

import static cart.ui.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CartItemApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfig.class)
        }
)
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
class CartItemApiControllerTest {

    @MockBean
    MemberArgumentResolver memberArgumentResolver;
    @MockBean
    CartItemService cartItemService;

    @Autowired
    CartItemApiController cartItemApiController;
    @Autowired
    RestDocumentationResultHandler restDocs;
    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setUp(@Autowired final RestDocumentationContextProvider provider) throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(cartItemApiController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .setCustomArgumentResolvers(memberArgumentResolver)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(restDocs)
                .build();


        given(memberArgumentResolver.supportsParameter(any())).willReturn(true);
        given(memberArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(Fixture.memberA);
    }

    @Test
    void showCartItems() throws Exception {
        final CartItemResponse response = CartItemResponse.of(Fixture.cartItem1);
        given(cartItemService.findByMember(any(Member.class))).willReturn(List.of(response));

        mockMvc.perform(get("/cart-items")
                        .header(HttpHeaders.AUTHORIZATION, "Basic ababababaababab"))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("사용자 Basic 인증 정보").attributes(field("constraint", "Basic 형식 토큰"))
                                ),
                                responseFields(
                                        fieldWithPath("[0].id").type(JsonFieldType.NUMBER).description("장바구니 상품 ID"),
                                        fieldWithPath("[0].quantity").type(JsonFieldType.NUMBER).description("장바구니 상품 수량"),
                                        fieldWithPath("[0].product.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                        fieldWithPath("[0].product.name").type(JsonFieldType.STRING).description("상품 이름"),
                                        fieldWithPath("[0].product.price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                        fieldWithPath("[0].product.imageUrl").type(JsonFieldType.STRING).description("상품 이미지 주소")
                                )
                        )
                );
    }

    @Test
    void addCartItems() throws Exception {
        final CartItemRequest request = new CartItemRequest(1L);
        given(cartItemService.add(any(Member.class), any(CartItemRequest.class))).willReturn(1L);

        mockMvc.perform(post("/cart-items")
                        .header(HttpHeaders.AUTHORIZATION, "Basic ababababaababab")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("사용자 Basic 인증 정보").attributes(field("constraint", "Basic 형식 토큰"))
                                ),
                                requestFields(
                                        fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 ID").attributes(field("constraint", "양수"))
                                ),
                                responseHeaders(
                                        headerWithName(LOCATION).description("생성된 장바구니 상품 리소스 URL")
                                )
                        )
                );
    }

    @Test
    void updateCartItemQuantity() throws Exception {
        final CartItemQuantityUpdateRequest request = new CartItemQuantityUpdateRequest(3);
        willDoNothing().given(cartItemService).updateQuantity(any(Member.class), anyLong(), any(CartItemQuantityUpdateRequest.class));

        mockMvc.perform(patch("/cart-items/{id}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Basic ababababaababab")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("사용자 Basic 인증 정보").attributes(field("constraint", "Basic 형식 토큰"))
                                ),
                                pathParameters(
                                        parameterWithName("id").description("장바구니 상품 ID")
                                ),
                                requestFields(
                                        fieldWithPath("quantity").type(JsonFieldType.NUMBER).description("장바구니 상품 수량").attributes(field("constraint", "양수"))
                                )
                        )
                );

    }

    @Test
    void removeCartItems() throws Exception {
        willDoNothing().given(cartItemService).remove(any(Member.class), anyLong());

        mockMvc.perform(delete("/cart-items/{id}", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Basic ababababaababab"))
                .andExpect(status().isNoContent())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("사용자 Basic 인증 정보").attributes(field("constraint", "Basic 형식 토큰"))
                                ),
                                pathParameters(
                                        parameterWithName("id").description("장바구니 상품 ID")
                                )
                        )
                );
    }

    @Test
    void checkoutOrder() throws Exception {
        final CheckoutResponse response = CheckoutResponse.of(Fixture.orderCheckout1);

        given(cartItemService.makeCheckout(any(Member.class), any())).willReturn(response);

        mockMvc.perform(get("/cart-items/checkout?ids=1")
                        .header(HttpHeaders.AUTHORIZATION, "Basic ababababaababab"))
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("사용자 Basic 인증 정보").attributes(field("constraint", "Basic 형식 토큰"))
                                ),
                                responseFields(
                                        fieldWithPath("cartItems.[0].id").type(JsonFieldType.NUMBER).description("장바구니 상품 ID"),
                                        fieldWithPath("cartItems.[0].quantity").type(JsonFieldType.NUMBER).description("장바구니 상품 수량"),
                                        fieldWithPath("cartItems.[0].product.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                        fieldWithPath("cartItems.[0].product.name").type(JsonFieldType.STRING).description("상품 이름"),
                                        fieldWithPath("cartItems.[0].product.price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                        fieldWithPath("cartItems.[0].product.imageUrl").type(JsonFieldType.STRING).description("상품 이미지 주소"),
                                        fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("주문 총 금액"),
                                        fieldWithPath("currentPoints").type(JsonFieldType.NUMBER).description("소유 포인트"),
                                        fieldWithPath("earnedPoints").type(JsonFieldType.NUMBER).description("적립 예정 포인트"),
                                        fieldWithPath("availablePoints").type(JsonFieldType.NUMBER).description("사용 가능 포인트")
                                )
                        )
                );
    }
}
