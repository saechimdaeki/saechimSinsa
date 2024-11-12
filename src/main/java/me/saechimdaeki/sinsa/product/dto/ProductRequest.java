package me.saechimdaeki.sinsa.product.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;

@AllArgsConstructor
@Getter
public class ProductRequest {
    @NotEmpty(message = "brand name is required")
    private String brandName;

    @NotEmpty(message = "category is required")
    private String category;

    @Positive(message = "price must greater than 0")
    private Long price;

    public Product toDomain() {
        return new Product(brandName, Category.fromName(category), price);
    }
}
