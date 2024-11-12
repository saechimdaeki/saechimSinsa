package me.saechimdaeki.sinsa.product.service;


import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;
import me.saechimdaeki.sinsa.product.dto.BrandRequest;
import me.saechimdaeki.sinsa.product.dto.BrandResponse;
import me.saechimdaeki.sinsa.product.dto.ProductRequest;
import me.saechimdaeki.sinsa.product.dto.ProductResponse;
import me.saechimdaeki.sinsa.product.exception.ErrorCode;
import me.saechimdaeki.sinsa.product.exception.ProductException;
import me.saechimdaeki.sinsa.product.repository.InMemoryProductRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class IntegrationProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private InMemoryProductRepositoryImpl productRepository;

    @Test
    @DisplayName("상품 생성시 정상적으로 생성이 되어야 한다")
    void createProductTest() {
        // given
        final String brandName = "testBrandName";
        final String categoryName = "sneakers";
        final Long price = 150L;
        final ProductRequest productRequest = new ProductRequest(brandName, categoryName, price);

        // when

        final ProductResponse productResponse = productService.createProduct(productRequest);

        // then

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getBrandName()).isEqualTo(brandName);

        Brand brand = productRepository.getBrand(brandName);
        assertThat(brand).isNotNull();
        final Product product = brand.getProducts().stream().filter(it -> it.getCategory().equals(Category.fromName(categoryName)) && it.getPrice().equals(price))
                .findFirst().orElseThrow();

        assertThat(product).isNotNull();
        assertThat(product.getBrandName()).isEqualTo(brandName);
        assertThat(product.getPrice()).isEqualTo(price);
    }

    @Test
    @DisplayName("브랜드 생성시 정상적으로 브랜드가 생성되어야 한다")
    void createBrandTest() {

        // given
        final String brandName = "testBrandName";
        final BrandRequest brandRequest = new BrandRequest(brandName, new ArrayList<>());

        // when
        final BrandResponse brandResponse = productService.createBrand(brandRequest);

        // then
        assertThat(brandResponse).isNotNull();
        assertThat(brandResponse.getBrandName()).isEqualTo(brandName);
        final Brand brand = productRepository.getBrand(brandName);
        assertThat(brand).isNotNull();
        assertThat(brand.getBrandName()).isEqualTo(brandName);
    }

    @Test
    @DisplayName("브랜드 삭제시 정상적으로 브랜드가 삭제되고 해당하는 상품들도 지워져야 한다")
    void deleteBrandTest() {

        // given

        final String brandName = "C";
        final Brand brand = productRepository.getBrand(brandName);
        final List<Product> brandProducts = brand.getProducts();
        assertThat(brandProducts).isNotEmpty();

        // when

        productService.deleteBrand(brandName);

        // then

        final List<Brand> allBrands = productRepository.getAllBrands();
        final List<Product> allProducts = allBrands.stream().flatMap(allBrand -> allBrand.getProducts().stream()).toList();

        assertThat(productRepository.getBrand(brandName)).isNull();
        assertThat(allProducts).doesNotContain(brandProducts.get(0), brandProducts.get(1), brandProducts.get(2));

    }

    @Test
    @DisplayName("상품 삭제시 해당 상품이 존재 한다면 정상적으로 상품이 삭제되어야 한다")
    void deleteProductTest() {

        // given
        final String brandName = "C";
        final String categoryName = "sneakers";
        final Brand brand = productRepository.getBrand(brandName);
        final List<Product> brandProducts = brand.getProducts();

        final Optional<Product> product = brandProducts.stream().filter(it -> it.getCategory().equals(Category.fromName(categoryName)))
                .findFirst();

        assertThat(product).isPresent();

        // when
        productService.deleteProduct(brandName, categoryName);

        // then
        final Brand findedBrand = productRepository.getBrand(brandName);
        final List<Product> findedProducts = findedBrand.getProducts();
        final Optional<Product> deletedProduct = findedProducts.stream().filter(it -> it.getCategory().equals(Category.fromName(categoryName)))
                .findFirst();
        assertThat(deletedProduct).isNotPresent();

    }

    @Test
    @DisplayName("상품 삭제시 입력한 브랜드가 존재하지 않는다면 예외를 발생시킨다")
    void deleteProductTest_NoBrand() {

        // given
        final String brandName = "L";
        final String categoryName = "sneakers";

        // when then
        assertThatThrownBy(() -> productService.deleteProduct(brandName, categoryName))
                .isInstanceOf(ProductException.class)
                .hasMessage(ErrorCode.BRAND_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("상품 업데이트 요청이 성공하면 해당 브랜드의 카테고리 상품은 변경이 완료 된다")
    void updateProductTest() {
        // given
        final String brandName = "B";
        final String categoryName = "hat";
        final Long price = 100L;
        final ProductRequest productRequest = new ProductRequest(brandName, categoryName, price);

        Brand brand = productRepository.getBrand(brandName);
        final List<Product> brandProducts = brand.getProducts();
        Product originProduct = brandProducts.stream().filter(it -> it.getCategory().equals(Category.fromName(categoryName))).findFirst().orElseThrow();

        // when

        ProductResponse productResponse = productService.updateProduct(brandName, productRequest);

        // then
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getBrandName()).isEqualTo(brandName);
        assertThat(productResponse.getPrice()).isEqualTo(price);
        assertThat(Category.fromName(productResponse.getCategory())).isEqualTo(Category.fromName(categoryName));

        brand = productRepository.getBrand(brandName);
        Product updatedProduct = brand.getProducts().stream().filter(it -> it.getCategory().equals(Category.fromName(categoryName))).findFirst().orElseThrow();
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getCategory()).isEqualTo(Category.fromName(categoryName));
        assertThat(updatedProduct.getPrice()).isEqualTo(price);
        assertThat(updatedProduct).isNotEqualTo(originProduct);
    }

    @Test
    @DisplayName("상품 업데이트 요청이 실패하면 해당 브랜드의 카테고리 상품은 변경이 되지 않고 예외를 발생시킨다")
    void updateProductTest_fail() {
        // given
        final String brandName = "NO_BRAND";
        final String categoryName = "hat";
        final Long price = 100L;

        final ProductRequest productRequest = new ProductRequest(brandName, categoryName, price);
        // when then
        assertThatThrownBy(() -> productService.updateProduct(brandName, productRequest))
                .isInstanceOf(ProductException.class)
                .hasMessage(ErrorCode.BRAND_NOT_FOUND.getMessage());

    }

    @BeforeEach
    void setUp() {
        productRepository.initData();
    }

    @AfterEach
    void clear() {
        productRepository.clearAllData();
    }
}