package com.vikas.exception;

import com.vikas.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.PortUnreachableException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message(ex.getMessage()).build(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidOtp(InvalidOtpException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleOtpExpired(OtpExpiredException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(OtpAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleOtpAlreadyVerified(OtpAlreadyVerifiedException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message(ex.getMessage()).build(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message(ex.getMessage()).build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message(ex.getMessage()).build(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabledException(DisabledException ex) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder().success(false).status(false).message("Account is not activated. Please verify your OTP first.").build(),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(SellerException.class)
    public ResponseEntity<ErrorDetails> sellerExceptionHandler(SellerException se, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setError(se.getMessage());
        errorDetails.setDetails(webRequest.getDescription(false));
        errorDetails.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PortUnreachableException.class)
    public ResponseEntity<ErrorDetails> productExceptionHandler(SellerException se, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setError(se.getMessage());
        errorDetails.setDetails(webRequest.getDescription(false));
        errorDetails.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
