package me.saechimdaeki.sinsa.product.service;

import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;
import me.saechimdaeki.sinsa.product.dto.*;
import me.saechimdaeki.sinsa.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private final List<Brand> brandList = new ArrayList<>();


    @Test
    @DisplayName("카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회할 수 있어야 한다")
    void getLowestPricedProductsByCategoryTest() {

        // given

        BDDMockito.given(productRepository.getAllBrands())
                .willReturn(brandList);

        // when
        final LowestCategoryResponse lowestCategoryResponse = productService.getLowestPricedProductsByCategory();

        // then

        assertThat(lowestCategoryResponse).isNotNull();
        assertThat(lowestCategoryResponse.getItems()).isNotEmpty();
        assertThat(lowestCategoryResponse.getTotalPrice()).isPositive();

    }



    @Test
    @DisplayName("- 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회할 수 있어야 한다")
    void getBrandWithLowestTotalPriceTest() {
        // given
        BDDMockito.given(productRepository.getAllBrands())
                .willReturn(brandList);
        // when
        final LowestBrandResponse lowestBrandResponse = productService.getBrandWithLowestTotalPrice();

        // then

        assertThat(lowestBrandResponse).isNotNull();
        assertThat(lowestBrandResponse.getTotalPrice()).isPositive();
        assertThat(lowestBrandResponse.getCategories()).isNotEmpty();
    }

    @Test
    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회할 수 있어야 한다")
    void getCategoryPriceInfoTest() {
        // given

        final String categoryName = "socks";

        BDDMockito.given(productRepository.getAllBrands())
                .willReturn(brandList);

        // when

        final PriceByCategoryResponse priceByCategoryResponse = productService.getCategoryPriceInfo(categoryName);

        // then
        assertThat(priceByCategoryResponse).isNotNull();
        assertThat(priceByCategoryResponse.getHighestPrice()).isNotEmpty();
        assertThat(priceByCategoryResponse.getLowestPrice()).isNotEmpty();
        assertThat(priceByCategoryResponse.getCategory()).isEqualTo(Category.fromName(categoryName).name());
    }

    @Test
    @DisplayName("상품 생성요청시 문제가 없다면 정상적인 값을 반환해야한다")
    void createProductTest() {
        // given
        final String brandName = "testBrand";
        final String category = "hat";
        final Long price = 1250L;
        final ProductRequest productRequest = new ProductRequest(brandName, category, price);

        Product productDomain = productRequest.toDomain();

        BDDMockito.given(productRepository.addProduct(productDomain))
                .willReturn(productDomain);

        // when

        final ProductResponse productResponse = productService.createProduct(productRequest);

        // then
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getBrandName()).isEqualTo(brandName);
        assertThat(productResponse.getCategory()).isEqualTo(Category.fromName(category).name());
        assertThat(productResponse.getPrice()).isEqualTo(price);
    }

    @Test
    @DisplayName("브랜드 생성요청시 문제가 없다면 정상적인 값을 반환해야 한다")
    void createBrandTest() {
        // given
        final String brandName = "testBrand";
        final BrandRequest brandRequest = new BrandRequest(brandName, new ArrayList<>());
        final Brand brandDomain = brandRequest.toDomain();
        BDDMockito.given(productRepository.addBrand(brandDomain))
                .willReturn(brandDomain);

        // when

        final BrandResponse brandResponse = productService.createBrand(brandRequest);

        // then
        assertThat(brandResponse).isNotNull();
        assertThat(brandResponse.getBrandName()).isEqualTo(brandName);
    }

    @Test
    @DisplayName("상품 수정요청시 문제가 없다면 정상적인 값을 반환해야 한다")
    void updateProductTest() {

        // given
        final String brandName = "C";
        final ProductRequest productRequest = new ProductRequest(brandName, "bag", 200L);
        final Product productDomain = productRequest.toDomain();
        BDDMockito.given(productRepository.updateProduct(brandName, productDomain)).willReturn(productDomain);
        // when
        final ProductResponse productResponse = productService.updateProduct(brandName, productRequest);

        // then
        assertThat(productResponse).isNotNull();
        assertThat(productResponse.getBrandName()).isEqualTo(brandName);
        assertThat(productResponse.getPrice()).isEqualTo(productRequest.getPrice());
    }

    @BeforeEach
    void initData() {
        MockitoAnnotations.openMocks(this);
        final String[] brandNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        final Map<String, List<Long>> priceData = new HashMap<>();
        priceData.put("A", List.of(11200L, 5500L, 4200L, 9000L, 2000L, 1700L, 1800L, 2300L));
        priceData.put("B", List.of(10500L, 5900L, 3800L, 9100L, 2100L, 2000L, 2000L, 2200L));
        priceData.put("C", List.of(10000L, 6200L, 3300L, 9200L, 2200L, 1900L, 2200L, 2100L));
        priceData.put("D", List.of(10100L, 5100L, 3000L, 9500L, 2500L, 1500L, 2400L, 2000L));
        priceData.put("E", List.of(10700L, 5000L, 3800L, 9900L, 2300L, 1800L, 2100L, 2100L));
        priceData.put("F", List.of(11200L, 7200L, 4000L, 9300L, 2100L, 1600L, 2300L, 1900L));
        priceData.put("G", List.of(10500L, 5800L, 3900L, 9000L, 2200L, 1700L, 2100L, 2000L));
        priceData.put("H", List.of(10800L, 6300L, 3100L, 9700L, 2100L, 1600L, 2000L, 2000L));
        priceData.put("I", List.of(11400L, 6700L, 3200L, 9500L, 2400L, 1700L, 1700L, 2400L));

        for (String brandName : brandNames) {
            final List<Product> products = new ArrayList<>();
            final List<Long> prices = priceData.get(brandName);
            final Category[] categories = Category.values();
            for (int i = 0; i < categories.length; i++) {
                Product product = new Product(
                        brandName,
                        categories[i],
                        prices.get(i)
                );
                products.add(product);
            }
            final Brand brand = new Brand(brandName, products);
            brandList.add(brand);
        }
    }

    @AfterEach
    void clearData() {
        brandList.clear();
    }
}