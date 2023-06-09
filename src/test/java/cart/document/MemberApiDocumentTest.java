package cart.document;

import cart.auth.AuthInterceptor;
import cart.auth.MemberArgumentResolver;
import cart.auth.WebMvcConfig;
import cart.member.application.MemberService;
import cart.member.dao.MemberDao;
import cart.member.ui.MemberApiController;
import cart.member.ui.request.DepositRequest;
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

import static cart.fixtures.MemberFixtures.Member_Dooly;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@WebMvcTest(MemberApiDocumentTest.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
public class MemberApiDocumentTest {

    private static final String BASIC_PREFIX = "Basic ";
    private static final String ENCODE_DOOLY = Base64Utils.encodeToString((Member_Dooly.EMAIL + ":" + Member_Dooly.PASSWORD).getBytes());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private WebMvcConfig webMvcConfig;

    @MockBean
    private MemberDao memberDao;


    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        given(memberDao.getMemberByEmail(Member_Dooly.EMAIL)).willReturn(Member_Dooly.ENTITY);

        mockMvc = MockMvcBuilders.standaloneSetup(new MemberApiController(memberService))
                .defaultRequest(MockMvcRequestBuilders
                        .post("/members/cash")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY))
                .defaultRequest(MockMvcRequestBuilders
                        .get("/members/cash")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY))
                .setCustomArgumentResolvers(new MemberArgumentResolver())
                .addInterceptors(new AuthInterceptor(memberDao))
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void 캐시_충전_문서화() throws Exception {
        // given
        given(memberService.depositCash(Member_Dooly.ENTITY, 5000L))
                .willReturn(15000L);
        final DepositRequest request = DepositRequest.from(5000L);

        // when, then
        mockMvc.perform(post("/members/cash")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("members/postDepositCash",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("[Basic Auth] 로그인 정보")
                                ),
                                requestFields(
                                        fieldWithPath("cashToCharge").type(JsonFieldType.NUMBER).description("충전할 금액")
                                ),
                                responseFields(
                                        fieldWithPath("totalCash").type(JsonFieldType.NUMBER).description("충전한 이후의 총 금액")
                                )
                        )
                );
    }

    @Test
    void 캐시_확인_문서화() throws Exception {
        // given
        given(memberService.findCash(Member_Dooly.ENTITY)).willReturn(Member_Dooly.CASH);

        // when, then
        mockMvc.perform(get("/members/cash")
                        .header(HttpHeaders.AUTHORIZATION, BASIC_PREFIX + ENCODE_DOOLY)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("members/getTotalCash",
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("[Basic Auth] 로그인 정보")
                                ),
                                responseFields(
                                        fieldWithPath("totalCash").type(JsonFieldType.NUMBER).description("유저가 가지고 있는 캐시")
                                )
                        )
                );
    }

}
