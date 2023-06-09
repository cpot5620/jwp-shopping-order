package cart.document;

import cart.auth.AuthInterceptor;
import cart.auth.MemberArgumentResolver;
import cart.auth.WebMvcConfig;
import cart.member.dao.MemberDao;
import cart.order.application.OrderService;
import cart.order.application.dto.OrderDto;
import cart.order.application.dto.OrderedProductDto;
import cart.order.domain.OrderHistory;
import cart.order.ui.OrderApiController;
import cart.order.ui.request.OrderCartItemRequest;
import cart.order.ui.request.OrderCartItemsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;

import java.time.LocalDateTime;
import java.util.List;

import static cart.fixtures.MemberFixtures.Member_Dooly;
import static cart.fixtures.ProductFixtures.CHICKEN;
import static cart.fixtures.ProductFixtures.PIZZA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@WebMvcTest(OrderApiController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
public class OrderApiDocumentTest {

    private static final String BASIC_PREFIX = "Basic ";
    private static final String ENCODE_DOOLY = Base64Utils.encodeToString((Member_Dooly.EMAIL + ":" + Member_Dooly.PASSWORD).getBytes());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private WebMvcConfig webMvcConfig;

    @MockBean
    private MemberDao memberDao;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        given(memberDao.getMemberByEmail(Member_Dooly.EMAIL)).willReturn(Member_Dooly.ENTITY);

        mockMvc = MockMvcBuilders.standaloneSetup(new OrderApiController(orderService))
                .defaultRequest(MockMvcRequestBuilders
                        .get("/orders")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY))
                .defaultRequest(MockMvcRequestBuilders
                        .post("/orders")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY))
                .setCustomArgumentResolvers(new MemberArgumentResolver())
                .addInterceptors(new AuthInterceptor(memberDao))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void 특정_유저가_주문한_모든_주문_조회_문서화() throws Exception {
        // given
        final List<OrderedProductDto> orderedProductDtos = List.of(new OrderedProductDto(CHICKEN.ENTITY, 3),
                new OrderedProductDto(PIZZA.ENTITY, 2));
        final OrderHistory orderHistory = new OrderHistory(1L, Member_Dooly.ENTITY, 56000L, LocalDateTime.now());
        final OrderDto orderDto = new OrderDto(orderHistory, orderedProductDtos);

        given(orderService.findAllByMemberId(Member_Dooly.ID))
                .willReturn(List.of(orderDto));

        // when, then
        mockMvc.perform(get("/orders")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY))
                .andExpect(status().isOk())
                .andDo(document("orders/getOrders",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("[Basic Auth] 로그인 정보")
                        ),
                        responseFields(
                                fieldWithPath("orders").type(JsonFieldType.ARRAY).description("모든 주문 조회 목록"),
                                fieldWithPath("orders.[].orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("orders.[].orderedDateTime").type(JsonFieldType.STRING).description("주문한 시각"),
                                fieldWithPath("orders.[].products").type(JsonFieldType.ARRAY).description("주문한 상품과 수량"),
                                fieldWithPath("orders.[].products.[].product.id").type(JsonFieldType.NUMBER).description("주문한 상품의 ID"),
                                fieldWithPath("orders.[].products.[].product.name").type(JsonFieldType.STRING).description("주문한 상품의 이름"),
                                fieldWithPath("orders.[].products.[].product.price").type(JsonFieldType.NUMBER).description("주문한 상품의 가격"),
                                fieldWithPath("orders.[].products.[].product.imageUrl").type(JsonFieldType.STRING).description("주문한 상품의 이미지 경로"),
                                fieldWithPath("orders.[].products.[].quantity").type(JsonFieldType.NUMBER).description("주문한 상품의 수량"),
                                fieldWithPath("orders.[].totalPrice").type(JsonFieldType.NUMBER).description("주문한 총 금액")
                        )
                ));
    }

    @Test
    void 특정_유저가_주문한_특정_주문_조회_문서화() throws Exception {
        // given
        final List<OrderedProductDto> orderedProductDtos = List.of(new OrderedProductDto(CHICKEN.ENTITY, 3),
                new OrderedProductDto(PIZZA.ENTITY, 2));
        final OrderHistory orderHistory = new OrderHistory(1L, Member_Dooly.ENTITY, 56000L, LocalDateTime.now());
        final OrderDto orderDto = new OrderDto(orderHistory, orderedProductDtos);

        given(orderService.findByOrderHistoryId(orderHistory.getId()))
                .willReturn(orderDto);

        // when, then
        mockMvc.perform(get("/orders/{orderHistoryId}", orderHistory.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY))
                .andExpect(status().isOk())
                .andDo(document("orders/getOrder",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("[Basic Auth] 로그인 정보")
                        ),
                        pathParameters(
                                parameterWithName("orderHistoryId").description("조회할 주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").type(JsonFieldType.NUMBER).description("주문 ID"),
                                fieldWithPath("orderedDateTime").type(JsonFieldType.STRING).description("주문한 시각"),
                                fieldWithPath("products").type(JsonFieldType.ARRAY).description("주문한 상품과 수량"),
                                fieldWithPath("products.[].product.id").type(JsonFieldType.NUMBER).description("주문한 상품의 ID"),
                                fieldWithPath("products.[].product.name").type(JsonFieldType.STRING).description("주문한 상품의 이름"),
                                fieldWithPath("products.[].product.price").type(JsonFieldType.NUMBER).description("주문한 상품의 가격"),
                                fieldWithPath("products.[].product.imageUrl").type(JsonFieldType.STRING).description("주문한 상품의 이미지 경로"),
                                fieldWithPath("products.[].quantity").type(JsonFieldType.NUMBER).description("주문한 상품의 수량"),
                                fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("주문한 총 금액")
                        )
                ));
    }

    @Test
    void 특정_유저의_주문하기_문서화() throws Exception {
        // given
        final OrderCartItemRequest oneDto = new OrderCartItemRequest(1L, CHICKEN.NAME, CHICKEN.PRICE, CHICKEN.IMAGE_URL);
        final OrderCartItemRequest twoDto = new OrderCartItemRequest(2L, PIZZA.NAME, PIZZA.PRICE, PIZZA.IMAGE_URL);
        final List<OrderCartItemRequest> orderCartItemDtos = List.of(oneDto, twoDto);
        final OrderCartItemsRequest request = new OrderCartItemsRequest(orderCartItemDtos);

        given(orderService.addOrderHistory(any(), any()))
                .willReturn(1L);

        // when, then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/orders/" + 1L))
                .andDo(document("orders/postOrder",
                                preprocessRequest(prettyPrint()),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("[Basic Auth] 로그인 정보")
                                ),
                                requestFields(
                                        fieldWithPath("orderCartItemDtos").type(JsonFieldType.ARRAY).description("주문하려는 장바구니 목록"),
                                        fieldWithPath("orderCartItemDtos.[].cartItemId").type(JsonFieldType.NUMBER).description("장바구니 ID"),
                                        fieldWithPath("orderCartItemDtos.[].orderCartItemName").type(JsonFieldType.STRING).description("주문 시점의 상품 이름"),
                                        fieldWithPath("orderCartItemDtos.[].orderCartItemPrice").type(JsonFieldType.NUMBER).description("주문 시점의 상품 가격"),
                                        fieldWithPath("orderCartItemDtos.[].orderCartItemImageUrl").type(JsonFieldType.STRING).description("주문 시점의 상품 이미지 경로")
                                )
                        )
                );
    }
}
