package com.ajithkumar.ragchatmanagement.exception;

import com.ajithkumar.ragchatmanagement.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

// Hidden to exclude from API documentation
@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Global exception handler for IllegalArgumentException.
     *
     * @param exception the exception
     * @return response entity with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception) {

        log.error("Invalid argument error: {}", exception.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), "IllegalArgumentException", LocalDateTime.now());
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .status(HttpStatus.BAD_REQUEST.value())
//                .message(exception.getMessage())
//                .error("IllegalArgumentException")
//                .timestamp(LocalDateTime.now())
//                .path(request.getDescription(false).replace("uri=", ""))
//                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Global exception handler for general exceptions.
     *
     * @param exception the exception
     * @return response entity with error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception) {

        log.error("Unexpected error occurred", exception);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), "An internal error occurred", LocalDateTime.now());


//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message("An internal error occurred")
//                .error(exception.getClass().getSimpleName())
//                .timestamp(LocalDateTime.now())
//                .path(request.getDescription(false).replace("uri=", ""))
//                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {

        log.error("Runtime error occurred", exception);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), "Runtime error occurred", LocalDateTime.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }



}
