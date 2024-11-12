package me.saechimdaeki.sinsa.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import me.saechimdaeki.sinsa.product.repository.InMemoryProductRepositoryImpl;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer {

    private final InMemoryProductRepositoryImpl productRepository;

    @PostConstruct
    //과제에서 요구하는 첫 데이터 삽입
    public void initData() {
        productRepository.initData();
    }
}