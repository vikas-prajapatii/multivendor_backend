package com.vikas.exception;

public class OtpAlreadyVerifiedException extends RuntimeException {
    public OtpAlreadyVerifiedException(String message) {
        super(message);
    }
}
