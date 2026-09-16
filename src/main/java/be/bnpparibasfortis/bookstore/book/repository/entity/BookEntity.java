package be.bnpparibasfortis.bookstore.book.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "books")
@Data
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;
    private String author;
    private BigDecimal price;
}
