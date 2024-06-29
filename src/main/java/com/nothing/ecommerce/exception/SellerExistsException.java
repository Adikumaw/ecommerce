package com.nothing.ecommerce.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SellerExistsException extends SellerException {
    public SellerExistsException(String message) {
        super(message);
    }
}
