package superapp.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a resource already exists.
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class AlreadyExistException extends RuntimeException{

    /**
     * Constructs a new AlreadyExistException with no message.
     */
    public AlreadyExistException() {
        super();
    }

    /**
     * Constructs a new AlreadyExistException with the specified detail message.
     *
     * @param message the detail message
     */
    public AlreadyExistException(String message) {
        super(message);
    }
}
