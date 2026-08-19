package school.faang.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import school.faang.user_service.dto.error.ErrorResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(
            DataValidationException ex,
            HttpServletRequest request
    ){
        log.warn("Business validation failed on endpoint [{}]: {}", request.getRequestURI(), ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> errors = bindingResult.getFieldErrors();
        Map<String, List<String>> messages = new HashMap<>();

        for (var error : errors){
            messages
                    .computeIfAbsent(error.getField(), key -> new ArrayList<>())
                    .add(error.getDefaultMessage());
        }

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "NOT_VALID_DATA",
                null,
                request.getRequestURI(),
                messages
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex,
            HttpServletRequest request
    ){
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "ENTITY_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request
    ){
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "ACCESS_FORBIDDEN",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request
    ){
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getRequestURI(),
                null
        );

        log.error("Unexpected error on endpoint [{}]", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}