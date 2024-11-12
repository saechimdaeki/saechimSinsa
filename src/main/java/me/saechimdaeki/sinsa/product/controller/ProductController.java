package me.saechimdaeki.sinsa.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.saechimdaeki.sinsa.product.dto.*;
import me.saechimdaeki.sinsa.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/lowest-category")
    public ResponseEntity<LowestCategoryResponse> getProductsByLowestCategory() {
        return ResponseEntity.ok(productService.getLowestPricedProductsByCategory());
    }

    @GetMapping("/lowest-brand")
    public ResponseEntity<LowestBrandResponse> getProductsByBrand() {
        return ResponseEntity.ok(productService.getBrandWithLowestTotalPrice());
    }

    @GetMapping("/category")
    public ResponseEntity<PriceByCategoryResponse> getProductsByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.getCategoryPriceInfo(category));
    }

    @PostMapping("/brand")
    public ResponseEntity<?> createBrand(@RequestBody @Valid BrandRequest brandRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createBrand(brandRequest));
    }

    @PostMapping("/product")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productRequest));
    }

    @PutMapping("/brand/{brandName}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String brandName,
                                                         @RequestBody @Valid ProductRequest productRequest) {
        return ResponseEntity.ok(productService.updateProduct(brandName, productRequest));
    }


    @DeleteMapping("/brand/{brandName}")
    public ResponseEntity<?> deleteBrand(@PathVariable String brandName) {
        productService.deleteBrand(brandName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/brand/{brandName}/category/{categoryName}")
    public ResponseEntity<?> deleteCategory(@PathVariable String brandName, @PathVariable String categoryName) {
        productService.deleteProduct(brandName, categoryName);
        return ResponseEntity.noContent().build();
    }
}
