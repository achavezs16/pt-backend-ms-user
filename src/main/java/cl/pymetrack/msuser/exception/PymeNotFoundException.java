package cl.pymetrack.msuser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PymeNotFoundException extends RuntimeException {

    public PymeNotFoundException(String message) {
        super(message);
    }

    public PymeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
