package be.bnpparibasfortis.bookstore.exception;

public class InvalidQuantityException extends RuntimeException {
    public static final String MESSAGE = "The quantity requested for the book is invalid";

    public InvalidQuantityException() {
        super(MESSAGE);
    }
}
