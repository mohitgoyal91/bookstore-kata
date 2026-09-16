package be.bnpparibasfortis.bookstore.cart.controller;

import be.bnpparibasfortis.bookstore.exception.InvalidQuantityException;
import be.bnpparibasfortis.bookstore.BaseControllerTest;
import be.bnpparibasfortis.bookstore.exception.BookNotFoundException;
import be.bnpparibasfortis.bookstore.exception.BookNotInStockException;
import be.bnpparibasfortis.bookstore.cart.service.ICartService;
import be.bnpparibasfortis.bookstore.cart.service.models.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static be.bnpparibasfortis.bookstore.auth.Constants.TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
public class CartControllerTest extends BaseControllerTest {

    @MockitoBean
    ICartService cartService;

    @Nested
    class getCart {

        private static final String GET_CART_PATH = "/api/cart";

        @Test
        void whenNoAuth_shouldReturnUnauthorised() throws Exception {
            mockMvc.perform(
                    get(GET_CART_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void whenUserIsLoggedIn_shouldPassLoggedInUser() throws Exception {
            whenUserIsLoggedIn();
            mockMvc.perform(
                    get(GET_CART_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            ).andExpect(status().isOk());

            verify(cartService).getOrCreateCart(user);
        }
    }

    @Nested
    class addToCart {

        private static final String POST_BOOK_CART_PATH = "/api/cart/books";

        @Test
        void missingBookIdReturnsBadRequestWithoutCallingService() throws Exception {
            mockMvc.perform(post(POST_BOOK_CART_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"quantity\":1}")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(cartService);
        }

        @Test
        void invalidResultingQuantityReturnsBadRequest() throws Exception {
            when(cartService.updateCart(any(), any())).thenThrow(
                    new InvalidQuantityException());
            mockMvc.perform(post(POST_BOOK_CART_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"bookId\":\"%s\",\"quantity\":-3}".formatted(UUID.randomUUID()))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                                    InvalidQuantityException.MESSAGE));
        }


        @BeforeEach
        public void setUp() {
            whenUserIsLoggedIn();
        }

        @Test
        void whenNoBookExist_shouldThrowNotFound() throws Exception {
            UUID bookId = UUID.randomUUID();
            when(cartService.updateCart(any(), any())).thenThrow(BookNotFoundException.class);
            mockMvc.perform(
                    post(POST_BOOK_CART_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                        {
                                            "bookId": "%s",
                                            "quantity": 1
                                        }
                                    """.formatted(bookId))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            ).andExpect(status().isNotFound());
        }

        @Test
        void whenBookExistsAndQuantityIsNotAllowed_shouldReturnConflict() throws Exception {
            UUID bookId = UUID.randomUUID();
            when(cartService.updateCart(any(), any())).thenThrow(BookNotInStockException.class);
            mockMvc.perform(
                    post(POST_BOOK_CART_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                        {
                                            "bookId": "%s",
                                            "quantity": 1
                                        }
                                    """.formatted(bookId))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            ).andExpect(status().isConflict());
        }

        @Test
        void whenBookExistsAndQuantityIsOk_shouldReturnUpdatedCart() throws Exception {
            UUID bookId = UUID.randomUUID();
            when(cartService.updateCart(any(), any())).thenReturn(new Cart(UUID.randomUUID(), user.getId(), List.of()));
            mockMvc.perform(
                    post(POST_BOOK_CART_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                        {
                                            "bookId": "%s",
                                            "quantity": 1
                                        }
                                    """.formatted(bookId))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
            ).andExpect(status().isOk());
        }
    }
}
