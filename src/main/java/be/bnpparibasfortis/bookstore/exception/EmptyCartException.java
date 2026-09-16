package be.bnpparibasfortis.bookstore.exception;

public class EmptyCartException extends RuntimeException {
    public static final String MESSAGE = "No cart exists for the user";

    public EmptyCartException() {
        super(MESSAGE);
    }
}
