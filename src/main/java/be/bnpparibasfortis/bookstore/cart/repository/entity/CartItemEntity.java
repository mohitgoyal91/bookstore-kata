package be.bnpparibasfortis.bookstore.cart.repository.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "cartItems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_book",
                        columnNames = {"cartId", "bookId"}
                )
        }
)
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cartId", nullable = false)
    private CartEntity cart;

    @Column(name = "bookId", nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private int quantity;

    public CartItemEntity(UUID bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }
}
