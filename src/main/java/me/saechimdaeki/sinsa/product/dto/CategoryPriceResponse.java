package me.saechimdaeki.sinsa.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryPriceResponse {
    private String category;
    private Long price;
}