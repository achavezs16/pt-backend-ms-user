package cl.pymetrack.msuser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class PymeServiceException extends RuntimeException {

    public PymeServiceException(String message) {
        super(message);
    }

    public PymeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
