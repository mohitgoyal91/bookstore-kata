package be.bnpparibasfortis.bookstore.auth.service;

import be.bnpparibasfortis.bookstore.auth.repository.entity.UserEntity;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;

public interface IJwtService {
    AuthResponse generateToken(UserEntity userEntity);
    boolean isTokenValid(String token);
    String extractId(String token);
}
