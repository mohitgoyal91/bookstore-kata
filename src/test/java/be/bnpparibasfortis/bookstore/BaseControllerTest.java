package be.bnpparibasfortis.bookstore;

import be.bnpparibasfortis.bookstore.auth.repository.UserRepository;
import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.auth.security.SecurityConfig;
import be.bnpparibasfortis.bookstore.auth.service.IJwtService;
import be.bnpparibasfortis.bookstore.auth.service.impl.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static be.bnpparibasfortis.bookstore.auth.Constants.*;
import static org.mockito.Mockito.when;

@Import(SecurityConfig.class)
public class BaseControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected IJwtService jwtService;

    @MockitoBean
    protected UserRepository userRepository;

    protected UserEntity user;

    @BeforeEach
    public void setUp(){
        user = new UserEntity(EMAIL, FIRST_NAME, LAST_NAME, PASSWORD);
        user.setId(UUID.randomUUID());
    }

    protected void whenUserIsLoggedIn() {
        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractId(TOKEN)).thenReturn(user.getId().toString());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }
}
