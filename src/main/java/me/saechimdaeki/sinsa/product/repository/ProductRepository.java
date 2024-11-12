package me.saechimdaeki.sinsa.product.repository;

import me.saechimdaeki.sinsa.product.domain.Brand;
import me.saechimdaeki.sinsa.product.domain.Category;
import me.saechimdaeki.sinsa.product.domain.Product;

import java.util.List;

public interface ProductRepository {

    List<Brand> getAllBrands();

    void deleteBrand(String brandName);

    void deleteProduct(String brandName, Category category);

    Product updateProduct(String brandName, Product product);

    Product addProduct(Product product);

    Brand addBrand(Brand brand);

    void clearAllData();

    Brand getBrand(String brandName);
}
