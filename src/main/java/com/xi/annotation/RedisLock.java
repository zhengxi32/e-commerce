package com.xi.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
     *
     * @return 锁键
     */
    String key() default "";

    /**
     * 锁等待时间
     * @return 默认为 50 毫秒
     */
    int waitTime() default 50;

    /**
     * 锁过期时间
     * @return 默认为 3000 毫秒
     */
    int leastTime() default 3000;

    /**
     *
     * @return 超时时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
