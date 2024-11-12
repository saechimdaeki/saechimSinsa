package me.saechimdaeki.sinsa.common.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import me.saechimdaeki.sinsa.product.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponseEntity {
    private int status;
    private String name;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(builder()
                        .status(e.getHttpStatus().value())
                        .name(e.name())
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }
}
