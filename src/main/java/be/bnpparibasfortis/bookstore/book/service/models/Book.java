package be.bnpparibasfortis.bookstore.book.service.models;
import java.math.BigDecimal;
import java.util.UUID;

public record Book(
        UUID id,
        String title,
        String author,
        BigDecimal price
) {
}
