package be.bnpparibasfortis.bookstore;

import be.bnpparibasfortis.bookstore.exception.*;
import be.bnpparibasfortis.bookstore.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BookStoreExceptionHandler {

    @ExceptionHandler(InvalidEmailOrPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidEmailOrPasswordException exception) {
        return handleException(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(RuntimeException exception) {
        return handleException(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(RuntimeException exception) {
        return handleException(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(BookNotInStockException.class)
    public ResponseEntity<ErrorResponse> handleBookNotInStockException(RuntimeException exception) {
        return handleException(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuantityException(InvalidQuantityException exception) {
        return handleException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleCartNotFoundException(RuntimeException exception) {
        return handleException(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        return handleException(HttpStatus.BAD_REQUEST, message);
    }

    private static ResponseEntity<ErrorResponse> handleException(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message));
    }
}
