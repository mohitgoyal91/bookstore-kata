package be.bnpparibasfortis.bookstore.auth.service.exception;

public class InternalServerException extends RuntimeException {
    public static final String MESSAGE = "Something went wrong";

    public InternalServerException() {
        super(MESSAGE);
    }
}
