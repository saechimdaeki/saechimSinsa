package me.saechimdaeki.sinsa.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;
import me.saechimdaeki.sinsa.product.dto.*;
import me.saechimdaeki.sinsa.product.exception.ErrorCode;
import me.saechimdaeki.sinsa.product.exception.ProductException;
import me.saechimdaeki.sinsa.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public LowestCategoryResponse getLowestPricedProductsByCategory() {
        final List<ProductResponse> items = new ArrayList<>();
        Long totalPrice = 0L;

        final List<Brand> allBrands = productRepository.getAllBrands();

        for (Category category : Category.values()) {
            Product lowestProduct = null;
            for (Brand brand : allBrands) {
                final Product product = brand.getProducts().stream()
                        .filter(p -> p.getCategory() == category)
                        .findFirst()
                        .orElse(null);

                if (product != null) {
                    if (lowestProduct == null || product.getPrice() < lowestProduct.getPrice()) {
                        lowestProduct = product;
                    }
                }
            }

            if (lowestProduct != null) {
                items.add(new ProductResponse(
                        category.name(),
                        lowestProduct.getBrandName(),
                        lowestProduct.getPrice()
                ));
                totalPrice += lowestProduct.getPrice();
            }
        }

        if (items.isEmpty()) {
            throw new ProductException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return new LowestCategoryResponse(items, totalPrice);
    }

    public LowestBrandResponse getBrandWithLowestTotalPrice() {
        final List<Brand> allBrands = productRepository.getAllBrands();
        final Category[] categories = Category.values();

        Brand lowestBrand = null;
        Long lowestTotalPrice = null;
        List<CategoryPriceResponse> lowestBrandCategories = null;

        for (Brand brand : allBrands) {
            final List<Product> products = brand.getProducts();

            boolean hasAllCategories = Arrays.stream(categories)
                    .allMatch(category -> products.stream().anyMatch(p -> p.getCategory() == category));

            if (!hasAllCategories) {
                continue;
            }
            Long totalPrice = 0L;
            final List<CategoryPriceResponse> categoryPrices = new ArrayList<>();

            for (Category category : categories) {
                final Product product = products.stream()
                        .filter(p -> p.getCategory() == category)
                        .findFirst()
                        .orElse(null);

                if (product != null) {
                    totalPrice += product.getPrice();
                    categoryPrices.add(new CategoryPriceResponse(category.name(), product.getPrice()));
                }
            }

            if (lowestTotalPrice == null || totalPrice < lowestTotalPrice) {
                lowestTotalPrice = totalPrice;
                lowestBrand = brand;
                lowestBrandCategories = categoryPrices;
            }
        }

        if (lowestBrand == null) {
            throw new ProductException(ErrorCode.NO_BRAND_HAS_ALL_CATEGORIES);
        }

        return new LowestBrandResponse(
                lowestBrand.getBrandName(),
                lowestBrandCategories,
                lowestTotalPrice
        );
    }

    public PriceByCategoryResponse getCategoryPriceInfo(String categoryName) {

        final Category category = Category.fromName(categoryName);

        final List<Brand> allBrands = productRepository.getAllBrands();
        final List<Product> productsInCategory = new ArrayList<>();

        for (Brand brand : allBrands) {
            brand.getProducts().stream()
                    .filter(p -> p.getCategory() == category)
                    .forEach(productsInCategory::add);
        }

        if (productsInCategory.isEmpty()) {
            throw new ProductException(ErrorCode.NO_PRODUCTS_IN_CATEGORY);
        }

        final Long minPrice = productsInCategory.stream()
                .mapToLong(Product::getPrice)
                .min()
                .orElseThrow(() -> new ProductException(ErrorCode.NO_PRODUCTS_IN_CATEGORY));

        final Long maxPrice = productsInCategory.stream()
                .mapToLong(Product::getPrice)
                .max()
                .orElseThrow(() -> new ProductException(ErrorCode.NO_PRODUCTS_IN_CATEGORY));

        final List<BrandPriceResponse> lowestPriceBrands = productsInCategory.stream()
                .filter(p -> p.getPrice().equals(minPrice))
                .map(p -> new BrandPriceResponse(p.getBrandName(), p.getPrice()))
                .toList();

        final List<BrandPriceResponse> highestPriceBrands = productsInCategory.stream()
                .filter(p -> p.getPrice().equals(maxPrice))
                .map(p -> new BrandPriceResponse(p.getBrandName(), p.getPrice()))
                .toList();

        return new PriceByCategoryResponse(
                category.name(),
                lowestPriceBrands,
                highestPriceBrands
        );
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        return ProductResponse.from(productRepository.addProduct(productRequest.toDomain()));
    }

    public BrandResponse createBrand(BrandRequest brandRequest) {
        return BrandResponse.from(productRepository.addBrand(brandRequest.toDomain()));
    }

    public void deleteBrand(String brandName) {
        productRepository.deleteBrand(brandName);
    }

    public void deleteProduct(String brandName, String categoryName) {
        productRepository.deleteProduct(brandName, Category.fromName(categoryName));
    }

    public ProductResponse updateProduct(String brandName, ProductRequest productRequest) {
        return ProductResponse.from(productRepository.updateProduct(brandName, productRequest.toDomain()));
    }
}
