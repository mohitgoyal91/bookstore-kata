package be.bnpparibasfortis.bookstore.book.controller;

import be.bnpparibasfortis.bookstore.book.service.IBookService;
import be.bnpparibasfortis.bookstore.book.service.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * TODO
 *  Following things need to be taken care of before submitting
 *      1. Test Cases (TDD)
 *      2. Exception Handling
 *      3. Notes about improvements
 * */
@Controller
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private final IBookService bookService;

    public BookController(IBookService bookService) {
        this.bookService = bookService;
    }

    /**
     * TODO
     *  1. Provide the ability to filter the Books
     * */
    @GetMapping
    public ResponseEntity<List<Book>> findBooks(){
        return ResponseEntity.ok(bookService.findBooks());
    }
}
