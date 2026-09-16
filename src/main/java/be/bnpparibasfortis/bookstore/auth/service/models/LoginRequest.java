package be.bnpparibasfortis.bookstore.auth.controller.models;

public record LoginRequest(
        String email,
        String password
) {
}