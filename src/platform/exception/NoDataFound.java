package platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class NoDataFound extends RuntimeException {

    public NoDataFound(String message) {
        super(message);
    }
}
