package me.saechimdaeki.sinsa.product.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Brand {
    private String brandName;
    private List<Product> products;
}
