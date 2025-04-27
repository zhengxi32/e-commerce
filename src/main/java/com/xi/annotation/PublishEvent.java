package com.xi.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PublishEvent {
    /**
     *
     * @return 事件类型
     */
    Class<?> eventType() default Object.class;

}