package be.bnpparibasfortis.bookstore.cart.service.models;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public record CartItem(@NotNull(message = "Book ID is required") UUID bookId, int quantity) {
}
