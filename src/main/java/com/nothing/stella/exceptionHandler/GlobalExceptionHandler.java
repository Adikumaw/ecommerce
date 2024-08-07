package com.nothing.stella.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.nothing.stella.errorResponse.*;
import com.nothing.stella.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------------------------------------------
    // UNIVERSAL RUNTIME EXCEPTIONS
    // ----------------------------------------------------------------
    @ExceptionHandler
    public ResponseEntity<String> handleException(RuntimeException exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<NotFoundErrorResponse> handleException(UserNotFoundException exc) {
        // create response entity
        NotFoundErrorResponse resp = new NotFoundErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exc.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<NotFoundErrorResponse> handleException(AddressNotFoundException exc) {
        // create response entity
        NotFoundErrorResponse resp = new NotFoundErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exc.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<IllegalArgumentResponse> handleException(IllegalArgumentException exc) {

        // create response entity
        IllegalArgumentResponse resp = new IllegalArgumentResponse(
                HttpStatus.BAD_REQUEST.value(),
                exc.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(UserExistException exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
