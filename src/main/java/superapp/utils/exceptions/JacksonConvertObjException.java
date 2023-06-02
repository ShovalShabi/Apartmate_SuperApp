package superapp.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is a conflict during object conversion with Jackson.
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class JacksonConvertObjException extends RuntimeException{

    /**
     * Constructs a new JacksonConvertObjException with no message.
     */
    public JacksonConvertObjException() {
    }

    /**
     * Constructs a new JacksonConvertObjException with the specified detail message.
     *
     * @param message the detail message
     */
    public JacksonConvertObjException(String message) {
        super(message);
    }
}
