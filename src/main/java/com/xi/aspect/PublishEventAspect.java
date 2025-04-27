package com.xi.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xi.annotation.PublishEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class PublishEventAspect {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Around("@annotation(publishEvent)")
    public Object publishEvent(ProceedingJoinPoint joinPoint, PublishEvent publishEvent) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = methodSignature.getParameterNames();

        // 获取方法参数
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            params.put(paramNames[i], args[i]);
        }

        // 执行原方法
        Object result = joinPoint.proceed();

        // 创建事件对象
        Object event = new Object();
        if (publishEvent.eventType() != Object.class) {
            try {
                event = publishEvent.eventType().getDeclaredConstructor().newInstance();
                populateEventFromParams(event, params);
            } catch (Exception e) {
                // todo
            }
        }

        // 发布事件
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            final Object finalEvent = event;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    applicationEventPublisher.publishEvent(finalEvent);
                }
            });
        } else {
            applicationEventPublisher.publishEvent(event);
        }

        return result;
    }

    private void populateEventFromParams(Object event, Map<String, Object> params) {
        Class<?> eventClass = event.getClass();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();

            String setterName  = "set" + capitalize(paramName);
            try {
                Method setter = eventClass.getMethod(setterName, paramValue.getClass());
                setter.invoke(event, paramValue);
            } catch (NoSuchMethodException e) {
                try {
                    Field field = eventClass.getDeclaredField(paramName);
                    field.setAccessible(Boolean.TRUE);
                    field.set(event, paramValue);
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    // todo
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                // todo
            }
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
