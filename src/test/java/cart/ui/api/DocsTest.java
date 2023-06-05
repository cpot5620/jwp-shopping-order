package cart.ui.api;

import cart.application.CartItemService;
import cart.application.OrderService;
import cart.application.ProductService;
import cart.configuration.WebMvcConfig;
import cart.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = {CartItemApiController.class, OrderApiController.class, ProductApiController.class},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfig.class),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "cart\\.authentication\\..*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "cart\\.configuration\\.resolver\\..*")
        }
)
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class DocsTest {

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    ProductService productService;

    @MockBean
    CartItemService cartItemService;

    @MockBean
    OrderService orderService;

    @Autowired
    ProductApiController productApiController;

    @Autowired
    CartItemApiController cartItemApiController;

    @Autowired
    OrderApiController orderApiController;

    @Autowired
    RestDocumentationResultHandler restDocs;

    @Autowired
    RestDocumentationContextProvider provider;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;
}
