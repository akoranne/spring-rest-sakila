package com.example.app.common.exception;

import com.example.app.common.constant.ErrorCode;
import com.example.app.common.domain.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity.HeadersBuilder<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        final var code = ErrorCode.RESOURCE_NOT_FOUND;
        log.error(code.getPhrase() + ": {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.of(ErrorResponseDto.of(code));
    }

    @ExceptionHandler(ResourceNotAvailableException.class)
    public ResponseEntity.HeadersBuilder<?> handleResourceNotAvailableException(ResourceNotAvailableException e) {
        final var code = ErrorCode.RESOURCE_NOT_AVAILABLE;
        log.error(code.getPhrase() + ": {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.of(ErrorResponseDto.of(code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity.HeadersBuilder<?> handleException(Exception e) {
        final var code = ErrorCode.GENERIC_ERROR;
        log.error(code.getPhrase() + ": {}", e.getMessage());
        e.printStackTrace();
        return ResponseEntity.of(ErrorResponseDto.of(code));
    }
}
