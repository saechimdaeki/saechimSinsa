package me.saechimdaeki.sinsa.product.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ProductException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 340254415700516296L;
    private final ErrorCode errorCode;

    public ProductException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
