package com.xi.aspect;

import com.xi.annotation.RedisLock;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RedisLockAspect {

    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(redisLock)")
    public Object redisLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        RLock rLock = redissonClient.getLock(redisLock.key());

        rLock.lock(redisLock.expire(), redisLock.timeUnit());

        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            rLock.unlock();
        }

        return result;
    }

}
