package me.saechimdaeki.sinsa.common.handler;

import me.saechimdaeki.sinsa.product.exception.ProductException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductException.class)
    protected ResponseEntity<ErrorResponseEntity> handleUserException(ProductException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseEntity> handleBindException(BindException ex) {
        final FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            return new ResponseEntity<>(new ErrorResponseEntity(HttpStatus.BAD_REQUEST.value(),
                    "Invalid request", "BindException", ""), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ErrorResponseEntity(HttpStatus.BAD_REQUEST.value(),
                fieldError.getDefaultMessage(), "BindException", fieldError.getField()), HttpStatus.BAD_REQUEST);
    }

}
