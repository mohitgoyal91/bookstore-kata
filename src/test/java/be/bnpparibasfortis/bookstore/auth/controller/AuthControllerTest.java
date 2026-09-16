package be.bnpparibasfortis.bookstore.auth.controller;

import be.bnpparibasfortis.bookstore.BaseControllerTest;
import be.bnpparibasfortis.bookstore.models.ErrorResponse;
import be.bnpparibasfortis.bookstore.exception.EmailAlreadyExistsException;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;
import be.bnpparibasfortis.bookstore.auth.service.models.LoginRequest;
import be.bnpparibasfortis.bookstore.auth.service.models.RegisterRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static be.bnpparibasfortis.bookstore.auth.Constants.TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    
    @Nested
    class register {

        private static final String REGISTER_PATH = "/api/auth/register";

        private static final String VALID_REGISTER_REQUEST = """
                {
                    "email": "abc@def.com",
                    "firstName": "ABC",
                    "lastName": "DEF",
                    "password": "secure-password"
                }
                """;

        @Nested
        class whenEmail {
            @Test
            void IsBlank_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "firstName": "ABC",
                            "lastName": "DEF",
                            "password": "secure-password"
                        }
                        """, "Email is required", REGISTER_PATH);
            }

            @Test
            void NotCorrect_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abcdef",
                            "firstName": "ABC",
                            "lastName": "DEF",
                            "password": "secure-password"
                        }
                        """, "Email must be valid", REGISTER_PATH);
            }
        }

        @Nested
        class whenFirstName {

            @Test
            void IsBlank_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com",
                            "lastName": "DEF",
                            "password": "secure-password"
                        }
                        """, "First Name is required", REGISTER_PATH);
            }

            @Test
            void IsIncorrect_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com",
                            "firstName": "A",
                            "lastName": "DEF",
                            "password": "secure-password"
                        }
                        """, "First Name must contain at least 2 character", REGISTER_PATH);
            }
        }

        @Nested
        class whenLastName {

            @Test
            void IsBlank_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com",
                            "firstName": "ABC",
                            "password": "secure-password"
                        }
                        """, "Last Name is required", REGISTER_PATH);
            }

            @Test
            void IsIncorrect_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com",
                            "firstName": "ABC",
                            "lastName": "D",
                            "password": "secure-password"
                        }
                        """, "Last Name must contain at least 2 character", REGISTER_PATH);
            }
        }

        @Nested
        class whenPassword {

            @Test
            void IsBlank_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com",
                            "firstName": "ABC",
                            "lastName": "DEF"
                        }
                        """, "Password is required", REGISTER_PATH);
            }

            @Test
            void IsIncorrect_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com",
                            "firstName": "ABC",
                            "lastName": "DEF",
                            "password": "sec"
                        }
                        """, "Password must contain at least 8 characters", REGISTER_PATH);
            }
        }

        @Test
        void whenUserExists_shouldReturnConflict() throws Exception {
            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new EmailAlreadyExistsException());

            MvcResult result = mockMvc.perform(
                            post(REGISTER_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(VALID_REGISTER_REQUEST))
                    .andExpect(status().isConflict())
                    .andReturn();

            ErrorResponse actual = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    ErrorResponse.class
            );

            ErrorResponse expected = new ErrorResponse(
                    "An account with the requested email already exists"
            );

            assertEquals(expected, actual);
        }

        @Test
        void shouldSucceed() throws Exception {
            when(authService.register(any(RegisterRequest.class)))
                    .thenReturn(new AuthResponse(TOKEN));

            mockMvc.perform(
                            post(REGISTER_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(VALID_REGISTER_REQUEST))
                    .andExpect(status().isCreated())
                    .andExpect(header().string(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + TOKEN
                    ))
                    .andExpect(content().string(""));
        }
    }

    @Nested
    class login {

        private static final String LOGIN_PATH = "/api/auth/login";

        private static final String VALID_LOGIN_REQUEST = """
                {
                    "email": "abc@def.com",
                    "password": "secure-password"
                }
                """;

        @Nested
        class whenEmail {
            @Test
            void IsBlank_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "password": "secure-password"
                        }
                        """, "Email is required", LOGIN_PATH);
            }

            @Test
            void NotCorrect_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abcdef",
                            "password": "secure-password"
                        }
                        """, "Email must be valid", LOGIN_PATH);
            }
        }

        @Nested
        class whenPassword {

            @Test
            void IsBlank_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com"
                        }
                        """, "Password is required", LOGIN_PATH);
            }

            @Test
            void IsIncorrect_shouldReturnBadRequest() throws Exception {
                whenBadRequest("""
                        {
                            "email": "abc@def.com",
                            "password": "sec"
                        }
                        """, "Password must contain at least 8 characters", LOGIN_PATH);
            }
        }

        @Test
        void shouldSucceed() throws Exception {
            when(authService.login(any(LoginRequest.class)))
                    .thenReturn(new AuthResponse(TOKEN));

            mockMvc.perform(
                            post(LOGIN_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(VALID_LOGIN_REQUEST))
                    .andExpect(status().isOk())
                    .andExpect(header().string(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + TOKEN
                    ))
                    .andExpect(content().string(""));
        }
    }

    private void whenBadRequest(String content, String message, String path) throws Exception {
        MvcResult result = mockMvc.perform(
                        post(path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ErrorResponse.class
        );

        ErrorResponse expected = new ErrorResponse(message);

        assertEquals(expected, actual);
    }
}
