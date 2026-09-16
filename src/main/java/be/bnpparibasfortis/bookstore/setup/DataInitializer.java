package be.bnpparibasfortis.bookstore.setup;

import be.bnpparibasfortis.bookstore.book.repository.BookRepository;
import be.bnpparibasfortis.bookstore.book.repository.entity.BookEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initializeBooks(BookRepository bookRepository) {
        return args -> {
            if (bookRepository.count() > 0) {
                return;
            }

            List<BookEntity> books = List.of(
                    createBook(
                            "Clean Code",
                            "Robert C. Martin",
                            new BigDecimal("34.99"),
                            10
                    ),
                    createBook(
                            "Effective Java",
                            "Joshua Bloch",
                            new BigDecimal("44.99"),
                            8
                    ),
                    createBook(
                            "Design Patterns",
                            "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides",
                            new BigDecimal("49.99"),
                            5
                    ),
                    createBook(
                            "The Pragmatic Programmer",
                            "David Thomas, Andrew Hunt",
                            new BigDecimal("39.99"),
                            12
                    )
            );

            bookRepository.saveAll(books);
        };
    }

    private BookEntity createBook(
            String title,
            String author,
            BigDecimal price,
            int quantity) {

        return new BookEntity(title, author, price, quantity);
    }
}