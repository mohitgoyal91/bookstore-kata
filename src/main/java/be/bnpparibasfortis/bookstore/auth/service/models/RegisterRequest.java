package be.bnpparibasfortis.bookstore.auth.service.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First Name is required")
        @Size(min = 2, message = "First Name must contain at least 2 character")
        String firstName,

        @NotBlank(message = "Last Name is required")
        @Size(min = 2, message = "Last Name must contain at least 2 character")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 4, message = "Password must contain at least 8 characters")
        String password
) {
}