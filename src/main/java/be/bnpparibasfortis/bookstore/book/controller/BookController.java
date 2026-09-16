package be.bnpparibasfortis.bookstore.book.controller;

import be.bnpparibasfortis.bookstore.book.service.IBookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/api/books")
public class BookController {

    private final IBookService bookService;

    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> findBooks(@RequestParam(required = false) Boolean available){
        return ResponseEntity.ok(bookService.findBooks(available));
    }
}
