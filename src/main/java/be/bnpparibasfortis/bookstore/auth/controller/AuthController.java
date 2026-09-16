package be.bnpparibasfortis.bookstore.auth.controller;

import be.bnpparibasfortis.bookstore.auth.service.IAuthService;
import be.bnpparibasfortis.bookstore.exception.InternalServerException;
import be.bnpparibasfortis.bookstore.auth.service.models.AuthResponse;
import be.bnpparibasfortis.bookstore.auth.service.models.LoginRequest;
import be.bnpparibasfortis.bookstore.auth.service.models.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return handleAuth(authService.register(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return handleAuth(authService.login(request), HttpStatus.OK);
    }

    private ResponseEntity<Object> handleAuth(AuthResponse response, HttpStatus created) {
        return Optional.of(response)
                .map(authResponse -> ResponseEntity
                        .status(created)
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + authResponse.token()
                        )
                        .build()
                )
                .orElseThrow(InternalServerException::new);
    }
}
