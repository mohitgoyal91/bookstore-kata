package be.bnpparibasfortis.bookstore.book.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * TODO:
 *  implement indices
 * */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;
    private String author;
    private BigDecimal price;
    @PositiveOrZero
    private int quantity;

    public BookEntity(String title, String author, BigDecimal price, int quantity) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
    }
}
