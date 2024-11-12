package me.saechimdaeki.sinsa.common.config;


import lombok.extern.slf4j.Slf4j;
import me.saechimdaeki.sinsa.common.annotation.ReadLock;
import me.saechimdaeki.sinsa.common.annotation.WriteLock;
import me.saechimdaeki.sinsa.product.exception.ErrorCode;
import me.saechimdaeki.sinsa.product.exception.ProductException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Aspect
@Component
@Slf4j
public class LockAspect {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Around("@annotation(readLock)")
    public Object aroundReadLock(ProceedingJoinPoint joinPoint, ReadLock readLock) throws Throwable {
        int retries = 0;
        while (retries < readLock.maxRetries()) {
            try {
                if (lock.readLock().tryLock(readLock.retryDelay(), readLock.timeUnit())) {
                    try {
                        return joinPoint.proceed();
                    } finally {
                        lock.readLock().unlock();
                    }
                } else {
                    retries++;
                    log.warn("Retrying to acquire read lock... attempt: {}", retries);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread interrupted while acquiring read lock", e);
            }
        }
        log.error("Failed to acquire Read lock after {} retries", retries);
        throw new ProductException(ErrorCode.DATA_READ_ERROR);
    }

    @Around("@annotation(writeLock)")
    public Object aroundWriteLock(ProceedingJoinPoint joinPoint, WriteLock writeLock) throws Throwable {
        int retries = 0;
        while (retries < writeLock.maxRetries()) {
            try {
                if (lock.writeLock().tryLock(writeLock.retryDelay(), writeLock.timeUnit())) {
                    try {
                        return joinPoint.proceed();
                    } finally {
                        lock.writeLock().unlock();
                    }
                } else {
                    retries++;
                    log.warn("Retrying to acquire read lock... attempt: {}", retries);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread interrupted while acquiring write lock", e);
            }
        }
        log.error("Failed to acquire write lock after {} retries", retries);
        throw new ProductException(ErrorCode.DATA_SAVE_ERROR);
    }
}
