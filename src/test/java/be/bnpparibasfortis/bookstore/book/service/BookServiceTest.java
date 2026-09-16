package be.bnpparibasfortis.bookstore.book.service;

import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.ParameterizedTest;
import be.bnpparibasfortis.bookstore.exception.InvalidQuantityException;
import be.bnpparibasfortis.bookstore.exception.BookNotFoundException;
import be.bnpparibasfortis.bookstore.exception.BookNotInStockException;
import be.bnpparibasfortis.bookstore.book.repository.BookRepository;
import be.bnpparibasfortis.bookstore.book.repository.entity.BookEntity;
import be.bnpparibasfortis.bookstore.book.service.impl.BookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    BookEntity QUANTITY_1_BOOK;

    @BeforeEach
    void setUp(){
        bookService = new BookService(bookRepository);
        QUANTITY_1_BOOK = new BookEntity(
                "BOOK_TITLE",
                "BOOK_AUTHOR",
                BigDecimal.ONE,
                1
        );
    }

    @Nested
    class findBooks {

        @Test
        void whenAvailableIsNull_shouldFetchAllBooks() throws Exception {
            bookService.findBooks(null);

            verify(bookRepository).findAll();
            verify(bookRepository, never()).findByQuantityEquals(anyInt());
            verify(bookRepository, never()).findByQuantityGreaterThan(anyInt());
        }

        @Test
        void whenAvailableIsFalse_shouldFetchNotAvailableBooks() throws Exception {
            bookService.findBooks(false);

            verify(bookRepository, never()).findAll();
            verify(bookRepository).findByQuantityEquals(0);
            verify(bookRepository, never()).findByQuantityGreaterThan(anyInt());
        }

        @Test
        void whenAvailableIsTrue_shouldFetchAvailableBooks() throws Exception {
            bookService.findBooks(true);

            verify(bookRepository, never()).findAll();
            verify(bookRepository, never()).findByQuantityEquals(anyInt());
            verify(bookRepository).findByQuantityGreaterThan(0);
        }

        @Test
        void whenBooksArePresent_shouldMapCorrectly() throws Exception {
            when(bookRepository.findAll()).thenReturn(List.of(QUANTITY_1_BOOK));

            List<Book> books = bookService.findBooks(null);
            verify(bookRepository).findAll();
            verify(bookRepository, never()).findByQuantityEquals(anyInt());
            verify(bookRepository, never()).findByQuantityGreaterThan(anyInt());
            assertEquals(1, books.size());
            Book actual = books.get(0);

            assertEquals(QUANTITY_1_BOOK.getTitle(), actual.title());
            assertEquals(QUANTITY_1_BOOK.getAuthor(), actual.author());
            assertEquals(QUANTITY_1_BOOK.getPrice(), actual.price());
        }
    }

    @Nested
    class findBook {

        @Test
        void whenBookExists_ShouldReturn() {
            UUID bookId = UUID.randomUUID();
            when(bookRepository.findById(bookId)).thenReturn(Optional.of(QUANTITY_1_BOOK));
            Book actual = bookService.findBook(bookId);

            assertEquals(QUANTITY_1_BOOK.getTitle(), actual.title());
            assertEquals(QUANTITY_1_BOOK.getAuthor(), actual.author());
        }

        @Test
        void whenNoBookExists_ShouldThrowException() {
            BookNotFoundException exception = assertThrows(
                    BookNotFoundException.class, () -> bookService.findBook(UUID.randomUUID())
            );
            assertNotNull(exception);
        }
    }

    @Nested
    class decreaseStock {
        @ParameterizedTest
        @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
        void nonPositiveReductionIsRejectedBeforeAccessingInventory(int quantity) {
            assertThrows(InvalidQuantityException.class,
                    () -> bookService.decreaseStock(UUID.randomUUID(), quantity));
            verifyNoInteractions(bookRepository);
        }

        @Test
        void whenNoBookExist_shouldThrowException() {
            BookNotFoundException exception = assertThrows(
                    BookNotFoundException.class, () -> bookService.decreaseStock(UUID.randomUUID(), 1)
            );
            assertNotNull(exception);
        }

        @Test
        void whenRequestedQuantityIsGreaterThanAvailable_shouldThrowException() {
            when(bookRepository.findByIdForUpdate(any())).thenReturn(Optional.of(QUANTITY_1_BOOK));
            BookNotInStockException exception = assertThrows(
                    BookNotInStockException.class, () -> bookService.decreaseStock(UUID.randomUUID(), 2)
            );
            assertNotNull(exception);
        }

        @Test
        void whenBookInStock_shouldUpdateQuantity() {
            BookEntity bookEntity = new BookEntity("", "", BigDecimal.ONE, 2);
            when(bookRepository.findByIdForUpdate(any())).thenReturn(Optional.of(bookEntity));
            bookService.decreaseStock(UUID.randomUUID(), 1);

            assertEquals(1, bookEntity.getQuantity());
        }
    }
}
