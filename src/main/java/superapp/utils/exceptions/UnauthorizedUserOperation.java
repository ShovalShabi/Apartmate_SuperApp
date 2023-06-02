package superapp.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 The UnauthorizedUserOperation class is a custom exception class that represents an unauthorized user operation.
 It extends the RuntimeException class and is annotated with @ResponseStatus to indicate the HTTP status code
 to be returned when this exception occurs (in this case, HttpStatus.UNAUTHORIZED).
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedUserOperation extends RuntimeException {

    /**
     * Constructs a new UnauthorizedUserOperation instance without a specified message.
     */
    public UnauthorizedUserOperation() {super();}

    /**
      * Constructs a new UnauthorizedUserOperation instance with the specified message.
      * @param message The detailed message explaining the unauthorized user operation.
     */
    public UnauthorizedUserOperation(String message) {
        super(message);
    }
}
