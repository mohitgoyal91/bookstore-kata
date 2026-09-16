package be.bnpparibasfortis.bookstore.cart.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "carts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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
        this.userId = Objects.requireNonNull(userId, "userId is required");
    }

    public CartEntity(UUID userId, List<CartItemEntity> items) {
        this(userId);

        List<CartItemEntity> suppliedItems = List.copyOf(items);
        Set<UUID> bookIds = new HashSet<>();

        for (CartItemEntity item : suppliedItems) {
            validateUnattachedItem(item);

            if (!bookIds.add(item.getBookId())) {
                throw new IllegalArgumentException("Duplicate book in cart");
            }
        }

        suppliedItems.forEach(this::addItem);
    }

    public void addItem(CartItemEntity item) {
        validateUnattachedItem(item);

        boolean alreadyPresent = items.stream()
                .anyMatch(existing ->
                        existing.getBookId().equals(item.getBookId()));

        if (alreadyPresent) {
            throw new IllegalArgumentException("Book already exists in cart");
        }

        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItemEntity item) {
        Objects.requireNonNull(item, "item is required");

        if (items.remove(item)) {
            item.setCart(null);
        }
    }

    private static void validateUnattachedItem(CartItemEntity item) {
        Objects.requireNonNull(item, "item is required");
        Objects.requireNonNull(item.getBookId(), "bookId is required");

        if (item.getCart() != null) {
            throw new IllegalArgumentException(
                    "Item already belongs to a cart"
            );
        }
    }
}
