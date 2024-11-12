package me.saechimdaeki.sinsa.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LowestCategoryResponse {
    private List<ProductResponse> items;
    private Long totalPrice;
}