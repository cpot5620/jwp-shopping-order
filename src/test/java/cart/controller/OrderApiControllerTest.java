package cart.controller;

import cart.application.OrderService;
import cart.dao.MemberDao;
import cart.domain.Order;
import cart.domain.OrderInfo;
import cart.dto.OrderRequest;
import cart.dto.OrderResponse;
import cart.ui.OrdersApiController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static cart.ShoppingOrderFixture.chicken;
import static cart.ShoppingOrderFixture.member1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@WebMvcTest(OrdersApiController.class)
class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberDao memberDao;

    @MockBean
    private OrderService orderService;

    @DisplayName("장바구니에 담긴 상품을 주문한다")
    @Test
    void order() throws Exception {
        String body = objectMapper.writeValueAsString(
                new OrderRequest(new ArrayList<Long>(List.of(1L, 2L)), 23000L, 5000L, 2300L));

        when(memberDao.getMemberByEmail("a@a.com")).thenReturn(member1);
        when(orderService.order(anyLong(), any())).thenReturn(1L);

        this.mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Basic YUBhLmNvbTpwYXNzd29yZDE=")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/1"));
    }

    @DisplayName("사용자별로 주문 목록을 확인한다")
    @Test
    void getOrders() throws Exception {
        when(memberDao.getMemberByEmail("a@a.com")).thenReturn(member1);
        when(orderService.findOrdersByMember(member1))
                .thenReturn(List.of(OrderResponse.of(new Order(1L, member1, 10000L, 5000L, 1000L), List.of(new OrderInfo(1L, chicken, 1L)))));

        this.mockMvc.perform(get("/orders")
                        .header("Authorization", "Basic YUBhLmNvbTpwYXNzd29yZDE="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].orderId").exists())
                .andExpect(jsonPath("$[*].orderInfos[*].productId").exists())
                .andExpect(jsonPath("$[*].orderInfos[*].price").exists())
                .andExpect(jsonPath("$[*].orderInfos[*].name").exists())
                .andExpect(jsonPath("$[*].orderInfos[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].orderInfos[*].quantity").exists());
    }

    @DisplayName("특정 주문의 상세 정보를 확인한다")
    @Test
    void getOrderDetail() throws Exception {
        when(memberDao.getMemberByEmail("a@a.com")).thenReturn(member1);
        when(orderService.findOrderDetail(1L))
                .thenReturn(OrderResponse.of(new Order(1L, member1, 10000L, 5000L, 1000L), List.of(new OrderInfo(1L, chicken, 1L))));


        this.mockMvc.perform(get("/orders/{orderId}", 1)
                        .header("Authorization", "Basic YUBhLmNvbTpwYXNzd29yZDE="))
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.originalPrice").exists())
                .andExpect(jsonPath("$.usedPoint").exists())
                .andExpect(jsonPath("$.pointToAdd").exists())
                .andExpect(jsonPath("$.orderInfos[*].productId").exists())
                .andExpect(jsonPath("$.orderInfos[*].price").exists())
                .andExpect(jsonPath("$.orderInfos[*].name").exists())
                .andExpect(jsonPath("$.orderInfos[*].imageUrl").exists())
                .andExpect(jsonPath("$.orderInfos[*].quantity").exists());
    }
}
