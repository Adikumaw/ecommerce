package com.nothing.stella.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidOrderItemIdException extends OrderException {
    public InvalidOrderItemIdException(String message) {
        super(message);
    }
}
