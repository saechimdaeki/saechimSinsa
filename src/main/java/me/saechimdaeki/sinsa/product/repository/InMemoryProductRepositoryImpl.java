package me.saechimdaeki.sinsa.product.repository;

import lombok.extern.slf4j.Slf4j;
import me.saechimdaeki.sinsa.common.annotation.ReadLock;
import me.saechimdaeki.sinsa.common.annotation.WriteLock;
import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;
import me.saechimdaeki.sinsa.product.exception.ErrorCode;
import me.saechimdaeki.sinsa.product.exception.ProductException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
public class InMemoryProductRepositoryImpl implements ProductRepository {

    private final Map<String, Brand> brandMap = new HashMap<>();

    @Override
    @WriteLock
    public Brand addBrand(Brand brand) {
        brandMap.put(brand.getBrandName(), brand);
        return brand;
    }

    @Override
    @WriteLock
    public void clearAllData() {
        brandMap.clear();
    }

    @Override
    public Brand getBrand(String brandName) {
        return brandMap.get(brandName);
    }

    @Override
    @WriteLock
    public Product addProduct(Product product) {
        final String brandName = product.getBrandName();
        Brand brand = brandMap.get(brandName);
        if (brand == null) {
            brand = new Brand(brandName, new ArrayList<>());
            brandMap.put(brandName, brand);
        }
        // 요구사항에 맞춰 한 브랜드는 한 카테고리당 상품이 하나이므로 덮어씌워진다.
        brand.getProducts().removeIf(product1 -> product1.getCategory().equals(product.getCategory()));
        brand.getProducts().add(product);

        return product;
    }

    @Override
    @WriteLock
    public Product updateProduct(String brandName, Product product) {
        final Brand brand = brandMap.get(brandName);
        if (brand == null) {
            throw new ProductException(ErrorCode.BRAND_NOT_FOUND);
        }

        Optional<Product> savedProduct = brand.getProducts().stream()
                .filter(p -> p.getCategory().equals(product.getCategory()))
                .findFirst();

        if (savedProduct.isEmpty()) {
            throw new ProductException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        brand.getProducts().remove(savedProduct.get());
        brand.getProducts().add(product);

        return product;
    }

    @Override
    @WriteLock
    public void deleteProduct(String brandName, Category category) {
        final Brand brand = brandMap.get(brandName);
        if (brand == null) {
            throw new ProductException(ErrorCode.BRAND_NOT_FOUND);
        }

        Optional<Product> product = brand.getProducts().stream()
                .filter(p -> p.getCategory().equals(category))
                .findFirst();

        if (product.isEmpty()) {
            throw new ProductException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        brand.getProducts().remove(product.get());

        Brand brand1 = brandMap.get(brandName);
        System.out.println(brand1);
    }

    @Override
    @ReadLock
    public List<Brand> getAllBrands() {
        return new ArrayList<>(brandMap.values());
    }

    @Override
    @WriteLock
    public void deleteBrand(String brandName) {
        if (!brandMap.containsKey(brandName)) {
            throw new ProductException(ErrorCode.NO_BRAND_DELETED);
        }
        brandMap.remove(brandName);
    }


    // 과제에서 요구하는 초기 데이터 셋팅
    public void initData() {
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
            brandMap.put(brandName, brand);
        }
    }
}
