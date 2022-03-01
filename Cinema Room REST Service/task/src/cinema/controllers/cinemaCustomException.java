package cinema.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class cinemaCustomException extends RuntimeException {

    public cinemaCustomException(String cause) {
        super(cause);
    }
}