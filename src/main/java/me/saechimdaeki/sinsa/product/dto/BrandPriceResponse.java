package me.saechimdaeki.sinsa.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrandPriceResponse {
    private String brandName;
    private Long price;
}
