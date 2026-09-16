package be.bnpparibasfortis.bookstore.order.service.models;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record Order(
        UUID id,
        BigDecimal totalPrice,
        List<OrderItem> items
) {
}
