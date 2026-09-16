package be.bnpparibasfortis.bookstore.order.controller;

import be.bnpparibasfortis.bookstore.BaseControllerTest;
import be.bnpparibasfortis.bookstore.exception.BookNotInStockException;
import be.bnpparibasfortis.bookstore.exception.EmptyCartException;
import be.bnpparibasfortis.bookstore.order.service.IOrderService;
import be.bnpparibasfortis.bookstore.order.service.models.Order;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static be.bnpparibasfortis.bookstore.auth.Constants.TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest extends BaseControllerTest {

    @MockitoBean
    IOrderService orderService;

    @Nested
    class createOrder {
        private static final String CREATE_ORDER_PATH = "/api/orders";

        @Test
        void whenNoAuth_shouldReturnUnauthorised() throws Exception {
            mockMvc.perform(
                    post(CREATE_ORDER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void whenNoCartExists_shouldThrowException() throws Exception {
            whenUserIsLoggedIn();
            when(orderService.createOrder(any())).thenThrow(new EmptyCartException());

            mockMvc.perform(
                    post(CREATE_ORDER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            ).andExpect(status().isBadRequest());
        }

        @Test
        void whenNotInStock_shouldThrowConflict() throws Exception {
            whenUserIsLoggedIn();
            when(orderService.createOrder(any())).thenThrow(new BookNotInStockException());

            mockMvc.perform(
                    post(CREATE_ORDER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            ).andExpect(status().isConflict());
        }

        @Test
        void whenInStock_shouldReturnSuccess() throws Exception {
            whenUserIsLoggedIn();
            when(orderService.createOrder(any())).thenReturn(Order.builder().build());

            mockMvc.perform(
                    post(CREATE_ORDER_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            ).andExpect(status().isOk());
        }
    }
}
