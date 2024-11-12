package me.saechimdaeki.sinsa.product.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Brand not found check Brand name"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "Product not found check Brand name And category"),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "P003", "Invalid category value"),
    NO_PRODUCT_DELETED(HttpStatus.BAD_REQUEST, "P004", "No Product deleted check Data"),
    NO_BRAND_DELETED(HttpStatus.BAD_REQUEST, "P005", "No Brand deleted check Data"),
    NO_BRAND_HAS_ALL_CATEGORIES(HttpStatus.NOT_FOUND, "P006", "No Brand has all categories check Data"),
    NO_PRODUCTS_IN_CATEGORY(HttpStatus.NOT_FOUND, "P007", "No Products in Category check Data"),
    DATA_READ_ERROR(HttpStatus.CONFLICT, "P008", "Data read error please try again"),
    DATA_SAVE_ERROR(HttpStatus.CONFLICT, "P009", "Data save error please try again"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
