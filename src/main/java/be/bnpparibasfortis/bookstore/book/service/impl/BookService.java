package be.bnpparibasfortis.bookstore.book.service.impl;

import be.bnpparibasfortis.bookstore.book.repository.BookRepository;
import be.bnpparibasfortis.bookstore.book.repository.entity.BookEntity;
import be.bnpparibasfortis.bookstore.book.service.IBookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService implements IBookService {

    @Autowired
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toBook)
                .collect(Collectors.toList());
    }

    private Book toBook(BookEntity entity) {
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getPrice()
        );
    }
}
