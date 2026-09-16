package be.bnpparibasfortis.bookstore.book;

import be.bnpparibasfortis.bookstore.book.repository.BookRepository;
import be.bnpparibasfortis.bookstore.book.repository.entity.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    BookEntity QUANTITY_0_BOOK;
    BookEntity QUANTITY_1_BOOK;
    BookEntity QUANTITY_2_BOOK;

    @BeforeEach
    void setUp(){
        QUANTITY_0_BOOK = new BookEntity(
                "BOOK_TITLE",
                "BOOK_AUTHOR",
                BigDecimal.ONE,
                0
        );
        QUANTITY_1_BOOK = new BookEntity(
                "BOOK_TITLE",
                "BOOK_AUTHOR",
                BigDecimal.ONE,
                1
        );
        QUANTITY_2_BOOK = new BookEntity(
                "BOOK_TITLE",
                "BOOK_AUTHOR",
                BigDecimal.ONE,
                2
        );
    }

    @Nested
    class findByQuantityGreaterThan {
        @Test
        void whenNoBooksExist_shouldReturnEmptyList(){
            List<BookEntity> actual = bookRepository.findByQuantityGreaterThan(0);

            assertThat(actual.isEmpty()).isTrue();
        }

        @Test
        void whenBooksExist_shouldReturnGreaterThanRequestedQuantity() {
            bookRepository.saveAll(List.of(QUANTITY_0_BOOK, QUANTITY_1_BOOK, QUANTITY_2_BOOK));
            List<BookEntity> actual = bookRepository.findByQuantityGreaterThan(0);

            assertEquals(2, actual.size());
        }
    }

    @Nested
    class findByQuantityEquals {
        @Test
        void whenBooksExist_shouldReturnEqualToQuantity() {
            bookRepository.saveAll(List.of(QUANTITY_0_BOOK, QUANTITY_1_BOOK, QUANTITY_2_BOOK));
            List<BookEntity> actual = bookRepository.findByQuantityEquals(0);

            assertEquals(1, actual.size());
        }
    }
}
