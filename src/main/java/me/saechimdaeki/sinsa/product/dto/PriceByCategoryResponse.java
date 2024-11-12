package me.saechimdaeki.sinsa.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PriceByCategoryResponse {
    private String category;
    private List<BrandPriceResponse> lowestPrice;
    private List<BrandPriceResponse> highestPrice;
}
