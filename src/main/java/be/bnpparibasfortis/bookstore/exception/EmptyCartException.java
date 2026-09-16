package be.bnpparibasfortis.bookstore.auth.service.exception;

public class EmptyCartException extends RuntimeException {
    public static final String MESSAGE = "No cart exists for the user";

    public EmptyCartException() {
        super(MESSAGE);
    }
}
