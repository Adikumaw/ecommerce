package com.nothing.stella.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
