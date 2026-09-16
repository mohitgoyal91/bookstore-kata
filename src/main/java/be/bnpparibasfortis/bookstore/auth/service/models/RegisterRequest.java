package be.bnpparibasfortis.bookstore.auth.controller.models;

public record RegisterRequest(
        String firstName,
        String email,
        String password
) {
}