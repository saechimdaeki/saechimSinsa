package me.saechimdaeki.sinsa.product.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.saechimdaeki.sinsa.product.exception.ErrorCode;
import me.saechimdaeki.sinsa.product.exception.ProductException;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum Category {
    TOP("상의"),
    OUTER("아우터"),
    PANTS("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORY("액세서리");

    private final String categoryName;


    private static final Map<String, Category> CATEGORY_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(m -> m.name().toLowerCase(), Function.identity()))
    );

    public static Category fromName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ProductException(ErrorCode.INVALID_CATEGORY);
        }
        Category category = CATEGORY_MAP.get(name.toLowerCase());
        if (category == null) {
            throw new ProductException(ErrorCode.INVALID_CATEGORY);
        }
        return category;
    }

}
