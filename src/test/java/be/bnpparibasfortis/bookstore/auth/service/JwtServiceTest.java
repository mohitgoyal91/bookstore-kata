package be.bnpparibasfortis.bookstore.auth.service;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.auth.service.impl.JwtService;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET =
            "this-is-a-long-enough-test-secret-key-for-hmac256";

    private static final long EXPIRATION = 3_600_000L;

    private JwtService jwtService;

    private UserEntity defaultUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                SECRET,
                EXPIRATION
        );

        defaultUser = new UserEntity(
                "abc@def.com",
                "ABC",
                "DEF",
                "hashed-password"
        );

        defaultUser.setId(
                UUID.fromString("1dad772e-3608-4d51-968d-c921414f35af")
        );
    }

    @Nested
    class GenerateToken {

        @Test
        void whenGeneratingToken_shouldReturnToken() {

            AuthResponse response = jwtService.generateToken(defaultUser);

            assertThat(response).isNotNull();
            assertThat(response.token()).isNotBlank();
        }

        @Test
        void whenGeneratingToken_shouldContainUserIdAsSubject() {

            AuthResponse response = jwtService.generateToken(defaultUser);

            var decodedToken = JWT.decode(response.token());

            assertThat(decodedToken.getSubject()).isEqualTo(defaultUser.getId().toString());
        }
    }

    @Nested
    class IsTokenValid {

        @Test
        void whenTokenIsValid_shouldReturnTrue() {

            String token = jwtService.generateToken(defaultUser).token();

            assertThat(jwtService.isTokenValid(token)).isTrue();
        }

        @Test
        void whenTokenHasInvalidSignature_shouldReturnFalse() {

            String token = jwtService.generateToken(defaultUser).token();

            String invalidToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalid-signature";

            assertThat(jwtService.isTokenValid(invalidToken)).isFalse();
        }

        @Test
        void whenTokenIsMalformed_shouldReturnFalse() {

            assertThat(jwtService.isTokenValid("not-a-jwt")).isFalse();
        }

        @Test
        void whenTokenIsEmpty_shouldReturnFalse() {

            assertThat(jwtService.isTokenValid("")).isFalse();
        }

        @Test
        void whenTokenIsExpired_shouldReturnFalse() {

            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            String expiredToken = JWT.create()
                    .withSubject(defaultUser.getId().toString())
                    .withIssuedAt(
                            Instant.now().minusSeconds(120)
                    )
                    .withExpiresAt(
                            Instant.now().minusSeconds(60)
                    )
                    .sign(algorithm);

            assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
        }

        @Test
        void whenTokenIsSignedWithDifferentSecret_shouldReturnFalse() {

            Algorithm differentAlgorithm =
                    Algorithm.HMAC256(
                            "another-test-secret-key-that-is-different"
                    );

            String token = JWT.create()
                    .withSubject(defaultUser.getId().toString())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(
                            Instant.now().plusMillis(EXPIRATION)
                    )
                    .sign(differentAlgorithm);

            assertThat(jwtService.isTokenValid(token)).isFalse();
        }
    }

    @Nested
    class ExtractId {

        @Test
        void whenTokenIsValid_shouldReturnUserId() {

            String token = jwtService.generateToken(defaultUser).token();

            String id = jwtService.extractId(token);

            assertThat(id).isEqualTo(defaultUser.getId().toString());
        }

        @Test
        void whenTokenIsInvalid_shouldThrowException() {

            String token = jwtService.generateToken(defaultUser).token();

            String invalidToken = token.substring(0, token.lastIndexOf('.') + 1) + "invalid-signature";

            assertThatThrownBy(
                    () -> jwtService.extractId(invalidToken)
            ).isInstanceOf(JWTVerificationException.class);
        }

        @Test
        void whenTokenIsMalformed_shouldThrowException() {

            assertThatThrownBy(
                    () -> jwtService.extractId("not-a-jwt")
            ).isInstanceOf(JWTVerificationException.class);
        }

        @Test
        void whenTokenIsExpired_shouldThrowException() {

            Algorithm algorithm = Algorithm.HMAC256(SECRET);

            String expiredToken = JWT.create()
                    .withSubject(defaultUser.getId().toString())
                    .withIssuedAt(
                            Instant.now().minusSeconds(120)
                    )
                    .withExpiresAt(
                            Instant.now().minusSeconds(60)
                    )
                    .sign(algorithm);

            assertThatThrownBy(
                    () -> jwtService.extractId(expiredToken)
            ).isInstanceOf(JWTVerificationException.class);
        }
    }
}