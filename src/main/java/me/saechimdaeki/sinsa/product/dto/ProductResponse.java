package me.saechimdaeki.sinsa.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.saechimdaeki.sinsa.product.domain.Product;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponse {
    private String category;
    private String brandName;
    private Long price;

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getCategory().name(), product.getBrandName(), product.getPrice());
    }
}