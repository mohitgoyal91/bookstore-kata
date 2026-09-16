package be.bnpparibasfortis.bookstore.exception;

public class BookNotFoundException extends RuntimeException {
    public static final String MESSAGE = "Book with the given ID doesn't exist";

    public BookNotFoundException() {
        super(MESSAGE);
    }
}
