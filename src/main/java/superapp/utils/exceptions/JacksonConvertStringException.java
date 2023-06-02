package superapp.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is a conflict during string conversion with Jackson.
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class JacksonConvertStringException extends RuntimeException{

    /**
     * Constructs a new JacksonConvertStringException with no message.
     */
    public JacksonConvertStringException() {
    }

    /**
     * Constructs a new JacksonConvertStringException with the specified detail message.
     *
     * @param message the detail message
     */
    public JacksonConvertStringException(String message) {
        super(message);
    }
}
