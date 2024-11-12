package me.saechimdaeki.sinsa.product.repository;

import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;
import me.saechimdaeki.sinsa.product.exception.ErrorCode;
import me.saechimdaeki.sinsa.product.exception.ProductException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class InMemoryProductRepositoryImplTest {

    @Autowired
    private ProductRepository repository;

    @AfterEach
    void clearData() {
        repository.clearAllData();
    }

    @Test
    @DisplayName("brand 추가 기능이 성공한 뒤에는 해당 브랜드 이름으로 조히가 가능해야한다")
    void addBrandTest() {

        // given

        final String brandName = "testBrand";
        final Brand brand = new Brand(brandName, List.of());

        // when

        repository.addBrand(brand);

        // then

        final List<Brand> allBrands = repository.getAllBrands();

        assertThat(allBrands).containsExactly(brand);
    }

    @Test
    @DisplayName("상품을 추가할 때에 기존에 존재하지 않던 브랜드 이름이라면 브랜드도 추가되어야 한다")
    void addProductTest_NoBrand() {

        // given

        final String brandName = "testBrand";
        final Category category = Category.BAG;
        final Long price = 1000L;

        final Product product = new Product(brandName, category, price);

        final Brand brand = repository.getBrand(brandName);

        assertThat(brand).isNull();

        // when

        repository.addProduct(product);

        // then

        final Brand savedBrand = repository.getBrand(brandName);
        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getProducts()).isNotEmpty();
        assertThat(savedBrand.getProducts()).hasSize(1);
        assertThat(savedBrand.getProducts().get(0).getCategory()).isEqualTo(Category.BAG);
    }

    @Test
    @DisplayName("상품을 추가할 때에 기존에 존재하는 브랜드라면 해당 브랜드에 상품이 추가되어야 한다")
    void addProductTest() {
        // given
        final String brandName = "testBrand";
        final Brand brand = new Brand(brandName, new ArrayList<>());
        repository.addBrand(brand);

        final Category category = Category.SOCKS;
        final Long price = 1002L;
        final Product product = new Product(brandName, category, price);

        // when

        repository.addProduct(product);

        // then
        final Brand savedBrand = repository.getBrand(brandName);
        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getProducts()).isNotEmpty();
        assertThat(savedBrand.getProducts()).containsExactly(product);

    }

    @Test
    @DisplayName("존재하지 않는 브랜드의 상품을 업데이트 하려고 하면 브랜드를 찾지 못했다는 예외를 발생시킨다")
    void updateProductTest_NoBrand() {
        // given
        final String brandName = "NO-BRAND";
        final Category category = Category.SOCKS;
        final Long price = 1002L;
        final Product product = new Product(brandName, category, price);
        // when then
        assertThatThrownBy(() -> repository.updateProduct(brandName, product))
                .isInstanceOf(ProductException.class)
                .hasMessage(ErrorCode.BRAND_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("존재하지 않는 상품을 업데이트 하려고 하면 상품을 찾지 못했다는 예외를 발생시킨다")
    void updateProductTest_NoProduct() {
        // given
        final String brandName = "NO-BRAND";
        repository.addBrand(new Brand(brandName, new ArrayList<>()));
        final Category category = Category.SOCKS;
        final Long price = 1002L;
        final Product product = new Product(brandName, category, price);

        // when then
        assertThatThrownBy(() -> repository.updateProduct(brandName, product))
                .isInstanceOf(ProductException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("해당 상품과 브랜드가 존재한다면 상품 업데이트 요청은 정상적으로 이뤄져야한다")
    void updateProductTest() {
        // given
        final String brandName = "testBrand";
        repository.addBrand(new Brand(brandName, new ArrayList<>()));
        final Category category = Category.SOCKS;
        final Long price = 1002L;
        final Product product = new Product(brandName, category, price);
        repository.addProduct(product);

        final Long changePrice = 2000L;
        Product changeProduct = new Product(brandName, category, changePrice);

        // when

        repository.updateProduct(brandName, changeProduct);

        // then
        final Brand savedBrand = repository.getBrand(brandName);
        assertThat(savedBrand).isNotNull();
        List<Product> products = savedBrand.getProducts();
        assertThat(products).isNotEmpty();

        Product updatedProduct = products.stream().filter(it -> it.getCategory().equals(category)).findFirst().get();
        assertThat(updatedProduct.getPrice()).isEqualTo(changeProduct.getPrice());
    }

    @Test
    @DisplayName("존재하지 않는 브랜드의 상품을 삭제 하려고 하면 브랜드를 찾지 못했다는 예외를 발생시킨다")
    void deleteProductTest_NoBrand() {
        // given
        final String brandName = "NO-BRAND";
        final Category category = Category.SOCKS;
        // when then
        assertThatThrownBy(() -> repository.deleteProduct(brandName, category))
                .isInstanceOf(ProductException.class)
                .hasMessage(ErrorCode.BRAND_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("존재하지 않는 상품을 삭제 하려고 하면 상품을 찾지 못했다는 예외를 발생시킨다")
    void deleteProductTest_NoProduct() {
        // given
        final String brandName = "NO-BRAND";
        repository.addBrand(new Brand(brandName, new ArrayList<>()));
        final Category category = Category.SOCKS;

        // when then
        assertThatThrownBy(() -> repository.deleteProduct(brandName, category))
                .isInstanceOf(ProductException.class)
                .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("해당 상품과 브랜드가 존재한다면 상품 삭제 요청은 정상적으로 이뤄져야한다")
    void deleteProductTest() {
        // given
        final String brandName = "testBrand";
        final Category category = Category.SOCKS;
        final Long price = 1002L;
        final Product product = new Product(brandName, category, price);
        repository.addProduct(product);

        // when
        repository.deleteProduct(brandName, category);

        // then
        final Brand brand = repository.getBrand(brandName);
        assertThat(brand.getProducts()).doesNotContain(product);
    }

    @Test
    @DisplayName("브랜드 삭제시 없는 브랜드를 삭제시키려 한다면 예외를 발생시켜야한다")
    void deleteBrandTest_noBrand() {
        // given
        final String brandName = "NO-BRAND";
        // when then
        assertThatThrownBy(() -> repository.deleteBrand(brandName))
                .isInstanceOf(ProductException.class)
                .hasMessage(ErrorCode.NO_BRAND_DELETED.getMessage());
    }

    @Test
    @DisplayName("브랜드 삭제 명령이 정상적으로 실행되었을 경우 해당 브랜드는 삭제되어야 한다")
    void deleteBrandTest() {
        // given
        final String brandName = "testBrand";
        final Category category = Category.SOCKS;
        final Long price = 1002L;
        final Product product = new Product(brandName, category, price);
        repository.addProduct(product);

        Brand brand = repository.getBrand(brandName);
        assertThat(brand).isNotNull();
        assertThat(brand.getProducts()).isNotEmpty();
        // when
        repository.deleteBrand(brandName);

        // then
        Brand deletedBrand = repository.getBrand(brandName);
        assertThat(deletedBrand).isNull();

    }

}