package be.bnpparibasfortis.bookstore.auth.service;

import be.bnpparibasfortis.bookstore.auth.repository.UserRepository;
import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.exception.EmailAlreadyExistsException;
import be.bnpparibasfortis.bookstore.exception.InternalServerException;
import be.bnpparibasfortis.bookstore.exception.InvalidEmailOrPasswordException;
import be.bnpparibasfortis.bookstore.auth.service.impl.AuthService;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;
import be.bnpparibasfortis.bookstore.auth.service.models.LoginRequest;
import be.bnpparibasfortis.bookstore.auth.service.models.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static be.bnpparibasfortis.bookstore.auth.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IJwtService jwtService;

    private AuthService authService;

    private UserEntity defaultUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );

        defaultUser = new UserEntity(
                EMAIL,
                FIRST_NAME,
                LAST_NAME,
                HASHED_PASSWORD
        );
    }

    @Nested
    class Register {

        private RegisterRequest request;

        @BeforeEach
        void setUp() {
            request = new RegisterRequest(
                    FIRST_NAME,
                    LAST_NAME,
                    EMAIL,
                    PASSWORD
            );
        }

        @Test
        void whenEmailAlreadyExists_shouldThrowException() {
            when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

            assertThatThrownBy(() -> authService.register(request)).isInstanceOf(EmailAlreadyExistsException.class);

            verify(userRepository).existsByEmail(EMAIL);
            verify(userRepository, never()).save(any(UserEntity.class));
            verifyNoInteractions(passwordEncoder, jwtService);
        }

        @Test
        void whenEmailDoesNotExist_shouldSaveUserAndReturnToken() {

            AuthResponse expected = new AuthResponse(TOKEN);

            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
            when(userRepository.save(any(UserEntity.class))).thenReturn(defaultUser);
            when(jwtService.generateToken(defaultUser)).thenReturn(expected);

            AuthResponse actual = authService.register(request);

            assertThat(actual).isEqualTo(expected);

            verify(userRepository).existsByEmail(EMAIL);
            verify(passwordEncoder).encode(PASSWORD);
            verify(userRepository).save(any(UserEntity.class));
            verify(jwtService).generateToken(defaultUser);
        }

        @Test
        void whenRegistering_shouldEncodePasswordBeforeSaving() {

            when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
            when(userRepository.save(any(UserEntity.class))).thenReturn(defaultUser);
            when(jwtService.generateToken(defaultUser)).thenReturn(new AuthResponse(TOKEN));

            ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

            authService.register(request);

            verify(userRepository).save(userCaptor.capture());
            UserEntity savedUser = userCaptor.getValue();
            verifyUser(savedUser);
        }

        @Test
        void whenSavingUserReturnsNull_shouldThrowInternalServerException() {

            when(userRepository.existsByEmail(EMAIL))
                    .thenReturn(false);

            when(passwordEncoder.encode(PASSWORD))
                    .thenReturn(HASHED_PASSWORD);

            when(userRepository.save(any(UserEntity.class)))
                    .thenReturn(null);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(
                            InternalServerException.class
                    );

            verify(jwtService, never())
                    .generateToken(any());
        }

        private static void verifyUser(UserEntity savedUser) {
            assertThat(savedUser.getEmail()).isEqualTo(EMAIL);
            assertThat(savedUser.getFirstName()).isEqualTo(FIRST_NAME);
            assertThat(savedUser.getLastName()).isEqualTo(LAST_NAME);
            assertThat(savedUser.getPassword()).isEqualTo(HASHED_PASSWORD);
        }
    }

    @Nested
    class Login {

        private LoginRequest request;

        @BeforeEach
        void setUp() {
            request = new LoginRequest(
                    EMAIL,
                    PASSWORD
            );
        }

        @Test
        void whenUserDoesNotExist_shouldThrowException() {

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request)).isInstanceOf(InvalidEmailOrPasswordException.class);

            verify(userRepository).findByEmail(EMAIL);
            verifyNoInteractions(passwordEncoder, jwtService);
        }

        @Test
        void whenPasswordIsIncorrect_shouldThrowException() {

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(defaultUser));

            when(passwordEncoder.matches(
                    PASSWORD,
                    HASHED_PASSWORD
            )).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request)).isInstanceOf(InvalidEmailOrPasswordException.class);

            verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);
            verifyNoInteractions(jwtService);
        }

        @Test
        void whenCredentialsAreCorrect_shouldReturnToken() {

            AuthResponse expected = new AuthResponse(TOKEN);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(defaultUser));
            when(passwordEncoder.matches(
                    PASSWORD,
                    HASHED_PASSWORD
            )).thenReturn(true);
            when(jwtService.generateToken(defaultUser)).thenReturn(expected);

            AuthResponse actual = authService.login(request);

            assertThat(actual).isEqualTo(expected);

            verify(userRepository).findByEmail(EMAIL);
            verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);
            verify(jwtService).generateToken(defaultUser);
        }
    }
}