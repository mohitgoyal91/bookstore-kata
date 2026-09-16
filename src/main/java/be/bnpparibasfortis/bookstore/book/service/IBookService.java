package be.bnpparibasfortis.bookstore.book.service;

import be.bnpparibasfortis.bookstore.book.service.models.Book;

import java.util.List;

public interface IBookService {
    List<Book> findBooks();
}
