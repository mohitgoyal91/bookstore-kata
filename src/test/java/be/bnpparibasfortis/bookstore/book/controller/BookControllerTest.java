package be.bnpparibasfortis.bookstore.book.controller;

import be.bnpparibasfortis.bookstore.BaseControllerTest;
import be.bnpparibasfortis.bookstore.auth.controller.AuthController;
import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.book.service.IBookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;
import java.util.UUID;

import static be.bnpparibasfortis.bookstore.auth.Constants.*;
import static java.util.Objects.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest extends BaseControllerTest {

    @MockitoBean
    IBookService bookService;

    @Nested
    class findBooks {
        private static final String FIND_BOOKS_PATH = "/api/books";

        @Test
        void whenNoAuth_shouldReturnUnauthorised() throws Exception {
            mockMvc.perform(
                    get(FIND_BOOKS_PATH)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void whenNoAvailableQueryParam_shouldReturnAll() throws Exception {
            whenUserIsLoggedIn();

            mockMvc.perform(
                    get(FIND_BOOKS_PATH)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());

            verify(bookService).findBooks(null);
        }

        @Test
        void whenAvailableFalse_shouldInvokeServiceWithFalse() throws Exception {
            whenUserIsLoggedIn();
            mockMvc.perform(
                    get(FIND_BOOKS_PATH + "?available=false")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
            verify(bookService).findBooks(false);
        }

        @Test
        void whenAvailableTrue_shouldInvokeServiceWithTrue() throws Exception {
            whenUserIsLoggedIn();
            mockMvc.perform(
                    get(FIND_BOOKS_PATH + "?available=true")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
                            .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
            verify(bookService).findBooks(true);
        }
    }
}
