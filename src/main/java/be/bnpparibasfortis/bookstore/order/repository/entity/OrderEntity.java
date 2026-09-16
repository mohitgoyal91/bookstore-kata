package be.bnpparibasfortis.bookstore.order.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @Getter
    private List<OrderItemEntity> items = new ArrayList<>();

    public OrderEntity(UUID userId) {
        this.userId = userId;
        this.createdAt = Instant.now();
    }

    public void addItem(OrderItemEntity item) {
        items.add(item);
        totalPrice = totalPrice.add(item.getTotalPrice());
        item.setOrder(this);
    }
}