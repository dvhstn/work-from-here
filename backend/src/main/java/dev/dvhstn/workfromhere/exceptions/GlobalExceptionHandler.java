package dev.dvhstn.workfromhere.exceptions;

import dev.dvhstn.workfromhere.spaces.exception.SpaceTypeNotFoundException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceAlreadyExistsException;
import dev.dvhstn.workfromhere.spaces.exception.SpaceResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResource> handleException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ApiErrorResource error = ApiErrorResource.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .message(message)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(SpaceResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResource> handleException(SpaceResourceNotFoundException ex, HttpServletRequest request) {
        ApiErrorResource error = ApiErrorResource.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SpaceTypeNotFoundException.class)
    public ResponseEntity<ApiErrorResource> handleException(SpaceTypeNotFoundException ex, HttpServletRequest request) {
        ApiErrorResource error = ApiErrorResource.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(SpaceResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResource> handleException(SpaceResourceAlreadyExistsException ex, HttpServletRequest request) {
        ApiErrorResource error = ApiErrorResource.builder()
                .status(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
