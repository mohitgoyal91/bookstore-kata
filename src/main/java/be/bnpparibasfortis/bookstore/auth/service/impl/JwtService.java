package be.bnpparibasfortis.bookstore.auth.service.impl;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.auth.service.IJwtService;
import be.bnpparibasfortis.bookstore.exception.InternalServerException;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class JwtService implements IJwtService {

    private final Algorithm algorithm;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expiration = expiration;
    }

    @Override
    public AuthResponse generateToken(UserEntity userEntity) {
        return Optional.of(Instant.now())
                .map(now -> JWT.create()
                        .withSubject(userEntity.getId().toString())
                        .withIssuedAt(now)
                        .withExpiresAt(now.plusMillis(expiration))
                        .sign(algorithm)
                )
                .map(AuthResponse::new)
                .orElseThrow(InternalServerException::new);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            JWT.require(algorithm)
                    .build()
                    .verify(token);

            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    @Override
    public String extractId(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }
}
