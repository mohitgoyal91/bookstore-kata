package be.bnpparibasfortis.bookstore.book.service.impl;

import be.bnpparibasfortis.bookstore.exception.BookNotFoundException;
import be.bnpparibasfortis.bookstore.exception.InvalidQuantityException;
import be.bnpparibasfortis.bookstore.exception.BookNotInStockException;
import be.bnpparibasfortis.bookstore.book.repository.BookRepository;
import be.bnpparibasfortis.bookstore.book.repository.entity.BookEntity;
import be.bnpparibasfortis.bookstore.book.service.IBookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService implements IBookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findBooks(Boolean available) {
        return Optional.ofNullable(available)
                .map(this::findBooksByAvailability)
                .orElseGet(bookRepository::findAll)
                .stream()
                .map(this::toBook)
                .toList();
    }

    @Override
    public Book findBook(UUID id) {
        return bookRepository.findById(id)
                .map(this::toBook)
                .orElseThrow(BookNotFoundException::new);
    }

    @Override
    public void decreaseStock(UUID id, int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }

        BookEntity book = bookRepository.findByIdForUpdate(id)
                .orElseThrow(BookNotFoundException::new);

        if (book.getQuantity() < quantity) {
            throw new BookNotInStockException();
        }

        book.setQuantity(book.getQuantity() - quantity);
    }

    private List<BookEntity> findBooksByAvailability(Boolean available) {
        if(available) {
            return bookRepository.findByQuantityGreaterThan(0);
        } else {
            return bookRepository.findByQuantityEquals(0);
        }
    }

    private Book toBook(BookEntity entity) {
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getPrice(),
                entity.getQuantity());
    }
}
