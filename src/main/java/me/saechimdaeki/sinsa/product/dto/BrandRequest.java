package me.saechimdaeki.sinsa.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Product;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class BrandRequest {
    @NotEmpty(message = "brand name is required")
    private String brandName;
    @Valid
    private List<ProductRequest> products;

    public Brand toDomain() {
        return new Brand(brandName, CollectionUtils.isEmpty(products) ? new ArrayList<>() : products.stream().map(ProductRequest::toDomain).toList());
    }
}
