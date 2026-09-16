package be.bnpparibasfortis.bookstore.exception;

public class BookNotInStockException extends RuntimeException {
    public static final String MESSAGE = "Book with the requested quantity is not in stock";

    public BookNotInStockException() {
        super(MESSAGE);
    }
}
