package be.bnpparibasfortis.bookstore.order.service.models;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItem(
        UUID id,
        UUID bookId,
        String title,
        BigDecimal price,
        int quantity,
        BigDecimal totalPrice
) {
}
