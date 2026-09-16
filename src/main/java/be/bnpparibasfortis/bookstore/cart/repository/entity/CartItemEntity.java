package be.bnpparibasfortis.bookstore.cart.repository.entity;

import jakarta.persistence.*;
import lombok.*;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cartId", nullable = false)
    @Setter(AccessLevel.PACKAGE)
    private CartEntity cart;

    @Column(name = "bookId", nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    @Setter
    private int quantity;

    public CartItemEntity(UUID bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }
}
