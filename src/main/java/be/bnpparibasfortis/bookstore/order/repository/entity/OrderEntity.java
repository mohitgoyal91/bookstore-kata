package be.bnpparibasfortis.bookstore.order.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    @Getter
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItemEntity> items = new ArrayList<>();

    public List<OrderItemEntity> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderEntity(UUID userId) {
        this.userId = userId;
        this.createdAt = Instant.now();
    }

    public void addItem(OrderItemEntity item) {
        Objects.requireNonNull(item, "item is required");

        if (item.getOrder() != null) {
            throw new IllegalArgumentException(
                    "Item already belongs to an order"
            );
        }

        BigDecimal updatedTotal = totalPrice.add(item.getTotalPrice());

        items.add(item);
        item.setOrder(this);
        totalPrice = updatedTotal;
    }
}