package be.bnpparibasfortis.bookstore.auth.service.exception;

public class InvalidEmailOrPasswordException extends RuntimeException{

    public static final String MESSAGE = "Invalid Email or Password";

    public InvalidEmailOrPasswordException() {
        super(MESSAGE);
    }
}
