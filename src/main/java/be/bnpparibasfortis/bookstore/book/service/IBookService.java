package be.bnpparibasfortis.bookstore.book.service;

import be.bnpparibasfortis.bookstore.book.service.models.Book;

import java.util.List;
import java.util.UUID;

public interface IBookService {
    List<Book> findBooks(Boolean available);
    Book findBook(UUID uuid);
    void decreaseStock(UUID bookId, int quantity);
}
