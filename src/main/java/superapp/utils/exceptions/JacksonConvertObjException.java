package superapp.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class JacksonConvertObjException extends RuntimeException{
    public JacksonConvertObjException() {
    }

    public JacksonConvertObjException(String message) {
        super(message);
    }
}
