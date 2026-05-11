package com.internship.tool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AssetValidationException extends RuntimeException {
    public AssetValidationException(String message) {
        super(message);
    }
}
