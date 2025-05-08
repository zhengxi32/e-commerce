package com.xi.aspect;

import cn.hutool.core.collection.CollUtil;
import com.xi.annotation.StockDecreaseResult;
import com.xi.entity.message.SkuMessage;
import com.xi.entity.param.OrderParam;
import com.xi.entity.result.ProcessResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class StockDecreaseResultAspect {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Around("@annotation(stockDecreaseResult)")
    public Object handleStockDecreaseResult(ProceedingJoinPoint joinPoint, StockDecreaseResult stockDecreaseResult) throws Throwable {
        // 获取方法签名和注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取方法参数中的OrderParam
        Object[] args = joinPoint.getArgs();
        OrderParam orderParam = null;
        for (Object arg : args) {
            if (arg instanceof OrderParam) {
                orderParam = (OrderParam) arg;
                break;
            }
        }

        if (orderParam == null) {
            throw new IllegalArgumentException("Stock decrease method must have OrderParam parameter");
        }

        // 执行原方法
        Object result = joinPoint.proceed();

        // 根据方法返回值决定发送到哪个主题
        if (result instanceof Boolean) {
            boolean success = (Boolean) result;

            if (success) {
                // 发送到订单支付和库存缓存同步主题
                rocketMQTemplate.syncSend(stockDecreaseResult.paymentTopic(), orderParam);
                if (CollUtil.isEmpty(orderParam.getBasketDtoList())) {
                    SkuMessage skuMessage = new SkuMessage();
                    skuMessage.setSkuId(orderParam.getSkuId());

                }
                rocketMQTemplate.syncSend(stockDecreaseResult.skuCacheStockSync(), orderParam);
            } else {
                // 发送到订单回滚主题
                rocketMQTemplate.syncSend(stockDecreaseResult.orderRollbackTopic(), orderParam);
            }
        } else {
            log.warn("Method {} does not return boolean, cannot determine message topic", method.getName());
        }

        return result;
    }
}