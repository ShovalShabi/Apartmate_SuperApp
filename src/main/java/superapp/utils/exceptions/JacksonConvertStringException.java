package superapp.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class JacksonConvertStringException extends RuntimeException{
    public JacksonConvertStringException() {
    }

    public JacksonConvertStringException(String message) {
        super(message);
    }
}
