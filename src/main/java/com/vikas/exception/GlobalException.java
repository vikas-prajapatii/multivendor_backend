package com.vikas.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.PortUnreachableException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(SellerException.class)
    public ResponseEntity<ErrorDetails> sellerExceptionHandler(SellerException se, WebRequest webRequest)  {
         ErrorDetails errorDetails = new ErrorDetails();
         errorDetails.setError(se.getMessage());
         errorDetails.setDetails(webRequest.getDescription(false));
         errorDetails.setTimeStamp(LocalDateTime.now());
         return new  ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(PortUnreachableException.class)
    public ResponseEntity<ErrorDetails> ProductExceptionHandler(SellerException se, WebRequest webRequest)  {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setError(se.getMessage());
        errorDetails.setDetails(webRequest.getDescription(false));
        errorDetails.setTimeStamp(LocalDateTime.now());
        return new  ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

}
