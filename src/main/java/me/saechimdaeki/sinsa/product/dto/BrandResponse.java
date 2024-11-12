package me.saechimdaeki.sinsa.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Product;

import java.util.List;

@Getter
@AllArgsConstructor
public class BrandResponse {
    private String brandName;
    private List<Product> products;

    public static BrandResponse from(Brand brand) {
        return new BrandResponse(brand.getBrandName(), brand.getProducts());
    }
}
