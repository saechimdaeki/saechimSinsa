package me.saechimdaeki.sinsa.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LowestBrandResponse {
    private String brand;

    private List<CategoryPriceResponse> categories;

    private Long totalPrice;
}