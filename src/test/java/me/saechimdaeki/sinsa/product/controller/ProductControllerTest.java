package me.saechimdaeki.sinsa.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;
import me.saechimdaeki.sinsa.product.dto.*;
import me.saechimdaeki.sinsa.product.repository.InMemoryProductRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryProductRepositoryImpl productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("초기에 주어지는 데이터로 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API를 반환해야 한다")
    void getProductsByLowestCategoryTest() throws Exception {

        // given when
        final ResultActions resultActions = mockMvc.perform(get("/store/lowest-category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        final LowestCategoryResponse lowestCategoryResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        // then

        assertThat(lowestCategoryResponse).isNotNull();
        assertThat(lowestCategoryResponse.getTotalPrice()).isPositive();

    }

    @Test
    @DisplayName("초기에 주어지는 데이터로 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API를 반환해야한다")
    void getProductsByBrandTest() throws Exception {
        // given when
        final ResultActions resultActions = mockMvc.perform(get("/store/lowest-brand")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        final String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        final LowestBrandResponse lowestBrandResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        // then
        assertThat(lowestBrandResponse).isNotNull();
        assertThat(lowestBrandResponse.getTotalPrice()).isPositive();
        assertThat(lowestBrandResponse.getBrand()).isNotNull();
    }

    @Test
    @DisplayName("초기에 주어지는 데이터로 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API를 반환해야 한다")
    void getProductsByCategoryTest() throws Exception {
        // given when
        final String categoryName = "top";
        final ResultActions resultActions = mockMvc.perform(get("/store/category")
                        .param("category", categoryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        final String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        final PriceByCategoryResponse priceByCategoryResponse = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        assertThat(priceByCategoryResponse).isNotNull();
        assertThat(Category.fromName(priceByCategoryResponse.getCategory())).isEqualTo(Category.fromName(categoryName));
        assertThat(priceByCategoryResponse.getLowestPrice().get(0).getPrice()).isEqualTo(10000);
        assertThat(priceByCategoryResponse.getLowestPrice().get(0).getBrandName()).isEqualTo("C");

        assertThat(priceByCategoryResponse.getHighestPrice().get(0).getPrice()).isEqualTo(11400);
        assertThat(priceByCategoryResponse.getHighestPrice().get(0).getBrandName()).isEqualTo("I");
    }

    @Test
    @DisplayName("브랜드 생성 요청시 브랜드 이름이 비어있다면 예외를 발생시킨다")
    void createBrandTest_EmptyBrandName() throws Exception {
        // given
        final String brandName = "";
        final BrandRequest brandRequest = new BrandRequest(brandName, new ArrayList<>());
        final String requestJson = objectMapper.writeValueAsString(brandRequest);
        // when then
        mockMvc.perform(post("/store/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("브랜드 생성 요청이 성공적으로 처리 된다면 정상 응답을 반환한다")
    void createBrandTest() throws Exception {
        // given
        final String brandName = "saechimBrand";
        final BrandRequest brandRequest = new BrandRequest(brandName, new ArrayList<>());
        final String requestJson = objectMapper.writeValueAsString(brandRequest);
        // when then
        mockMvc.perform(post("/store/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        final Brand brand = productRepository.getBrand(brandName);
        assertThat(brand).isNotNull();
        assertThat(brand.getBrandName()).isEqualTo(brandName);
    }

    @Test
    @DisplayName("상품 생성 요청시 요청 값이 포맷에 맞지 않다면 예외를 반환한다")
    void createProductTest_With_InvalidInput() throws Exception {
        // given
        final String brandName = "newbrand";
        final String category = "";
        final Long price = 123L;

        final ProductRequest productRequest = new ProductRequest(brandName, category, price);
        final String requestJson = objectMapper.writeValueAsString(productRequest);

        // when then
        mockMvc.perform(post("/store/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("상품 생성 요청이 성공한다면 상품이 정상적으로 생성되어야 한다")
    void createProductTest() throws Exception {
        // given
        final String brandName = "newbrand";
        final String category = "hat";
        final Long price = 123L;

        final ProductRequest productRequest = new ProductRequest(brandName, category, price);
        final String requestJson = objectMapper.writeValueAsString(productRequest);

        // when then
        mockMvc.perform(post("/store/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        final Brand brand = productRepository.getBrand(brandName);
        assertThat(brand).isNotNull();
        assertThat(brand.getBrandName()).isEqualTo(brandName);
        final List<Product> products = brand.getProducts();
        final Product product = products.get(0);
        assertThat(product).isNotNull();
        assertThat(product.getBrandName()).isEqualTo(brandName);
        assertThat(product.getCategory()).isEqualTo(Category.fromName(category));
        assertThat(product.getPrice()).isEqualTo(price);

    }
    
    @Test
    @DisplayName("상품 업데이트 요청시 존재하지 않는 브랜드의 상품이라면 예외가 발생한다")
    void updateProductTest_NoBrandName() throws Exception {
        // given
        final String brandName = "newbrand";
        final String category = "hat";
        final Long price = 123L;
        final ProductRequest productRequest = new ProductRequest(brandName, category, price);
        final String requestJson = objectMapper.writeValueAsString(productRequest);

        // when then
        mockMvc.perform(put("/store/brand/{brandName}", brandName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("상품 업데이트 요청시 존재하는 상품이라면 업데이트가 성공해야 한다")
    void updateProductTest() throws Exception {
        final String brandName = "B";
        final String category = "hat";
        final Long price = 123L;
        final ProductRequest productRequest = new ProductRequest(brandName, category, price);
        final String requestJson = objectMapper.writeValueAsString(productRequest);

        // when then
        mockMvc.perform(put("/store/brand/{brandName}", brandName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        final Brand brand = productRepository.getBrand(brandName);
        assertThat(brand).isNotNull();
        assertThat(brand.getBrandName()).isEqualTo(brandName);
        final List<Product> products = brand.getProducts();
        Product product = products.stream().filter(it -> it.getCategory().equals(Category.fromName(category)))
                .findFirst().orElseThrow();
        assertThat(product).isNotNull();
        assertThat(product.getBrandName()).isEqualTo(brandName);
        assertThat(product.getCategory()).isEqualTo(Category.fromName(category));
        assertThat(product.getPrice()).isEqualTo(price);
    }

    @Test
    @DisplayName("브랜드 삭제 요청시 존재하지 않는 브랜드라면 예외를 발생시킨다")
    void deleteProductTest_NoBrand() throws Exception {
        // given
        final String brandName = "BCADEADE";
        // when then
        mockMvc.perform(delete("/store/brand/{brandName}", brandName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("브랜드 삭제 요청이 성공시에 정상적인 반환값을 응답받아야 한다")
    void deleteProductTest() throws Exception {
        // given
        final String brandName = "C";
        // when then
        mockMvc.perform(delete("/store/brand/{brandName}", brandName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        final Brand brand = productRepository.getBrand(brandName);
        assertThat(brand).isNull();
    }

    @Test
    @DisplayName("브랜드의 카테고리 상품 삭제요청 성공시 성공적으로 지워져야 한다")
    void deleteCategoryTest() throws Exception {

        // given
        final String brandName = "B";
        final String categoryName = "hat";

        // when then
        mockMvc.perform(delete("/store/brand/{brandName}/category/{categoryName}", brandName,categoryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        final Brand brand = productRepository.getBrand(brandName);
        assertThat(brand).isNotNull();
        assertThat(brand.getProducts()).isNotEmpty();
        final Optional<Product> findProduct = brand.getProducts().stream().filter(product -> product.getCategory().equals(Category.fromName(categoryName)))
                .findFirst();
        assertThat(findProduct).isEmpty();


    }

    @BeforeEach
    void initData() {
        productRepository.initData();
    }

    @AfterEach
    void clearData() {
        productRepository.clearAllData();
    }
}