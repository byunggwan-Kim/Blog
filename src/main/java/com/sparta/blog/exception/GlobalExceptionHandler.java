package com.sparta.blog.exception;

import com.sparta.blog.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ApiResponse<?>> handleException(CustomException ex) {
        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ApiResponse.error(ex.getErrorCode().getMessage()));
    }
}