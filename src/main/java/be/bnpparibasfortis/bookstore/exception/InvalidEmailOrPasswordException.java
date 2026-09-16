package be.bnpparibasfortis.bookstore.exception;

public class InvalidEmailOrPasswordException extends RuntimeException{

    public static final String MESSAGE = "Invalid Email or Password";

    public InvalidEmailOrPasswordException() {
        super(MESSAGE);
    }
}
