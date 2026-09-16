package be.bnpparibasfortis.bookstore.cart.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItemEntity> items = new ArrayList<>();

    public CartEntity(UUID userId) {
        this.userId = userId;
    }

    public CartEntity(UUID userId, List<CartItemEntity> items) {
        this.userId = userId;
        this.items = items;
    }

    public void addItem(CartItemEntity item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItemEntity item) {
        items.remove(item);
        item.setCart(null);
    }
}
