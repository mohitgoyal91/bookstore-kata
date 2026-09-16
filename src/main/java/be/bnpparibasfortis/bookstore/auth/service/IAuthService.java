package be.bnpparibasfortis.bookstore.auth.service;

import be.bnpparibasfortis.bookstore.auth.service.models.LoginRequest;
import be.bnpparibasfortis.bookstore.auth.service.models.RegisterRequest;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
