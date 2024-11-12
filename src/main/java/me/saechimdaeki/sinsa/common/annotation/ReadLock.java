package me.saechimdaeki.sinsa.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadLock {
    int maxRetries() default 3;
    long retryDelay() default 100;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
