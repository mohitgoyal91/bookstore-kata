package be.bnpparibasfortis.bookstore.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public static final String MESSAGE = "An account with the requested email already exists";

    public EmailAlreadyExistsException() {
        super(MESSAGE);
    }
}
