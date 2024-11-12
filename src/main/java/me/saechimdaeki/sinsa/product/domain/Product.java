package me.saechimdaeki.sinsa.product.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Product {
    private String brandName;
    private Category category;
    private Long price;
}
