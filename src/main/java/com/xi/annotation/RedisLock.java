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
     *
     * @return 锁过期时间，默认为 5 毫秒
     */
    int expire() default 5000;

    /**
     *
     * @return 超时时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
