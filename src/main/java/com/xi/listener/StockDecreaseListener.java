package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.SystemConstant;
import com.xi.constant.TagConstant;
import com.xi.constant.TopicConstant;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.SkuDto;
import com.xi.domain.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.BasketService;
import com.xi.service.SkuService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.ORDER_CREATE_TOPIC,
        consumerGroup = ConsumerConstant.STOCK_DECREASE_GROUP,
        selectorExpression = TagConstant.ORDER_TAG_DIRECT_PURCHASE + TagConstant.SEPARATOR + TagConstant.ORDER_TAG_BASKET_PURCHASE +
        TagConstant.SEPARATOR + TagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE + TagConstant.SEPARATOR + TagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE
)
public class StockDecreaseListener implements RocketMQListener<OrderParam> {

    @Resource
    private SkuService skuService;

    @Resource
    private BasketService basketService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(OrderParam orderParam) {

        if (TagConstant.ORDER_TAG_DIRECT_PURCHASE.equals(orderParam.getTag())) {
            RLock rLock = redissonClient.getLock(orderParam.getSkuId());
            try {
                // 尝试上锁
                rLock.tryLock(50, 3000, TimeUnit.MILLISECONDS);

                // 乐观锁库存扣减
                SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
                Boolean success = skuService.updateStocksLock(orderParam.getSkuId(), orderParam.getProdCount(), skuDto.getVersion());

                rLock.unlock();

                // 库存扣减结果发送至不同主题
                String nextTopic = success ? TopicConstant.PAYMENT_TOPIC : TopicConstant.ORDER_ROLLBACK_TOPIC;
                rocketMQTemplate.syncSend(nextTopic, orderParam);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // 进入死信队列 todo
                throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
            } finally {
                // 释放锁
                rLock.unlock();
            }
        } else if (TagConstant.ORDER_TAG_BASKET_PURCHASE.equals(orderParam.getTag())) {
            List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

            // Redisson分布式锁
            RLock[] rLocks = new RLock[basketDtoList.size()];

            // SkuID排序 避免死锁
            basketService.sortListBySkuIdAsc(basketDtoList);

            for (BasketDto basketDto : basketDtoList) {
                rLocks[0] = redissonClient.getLock(SystemConstant.LOCK + basketDto.getSkuId());
            }
            RedissonMultiLock redissonMultiLock = new RedissonMultiLock(rLocks);

            try {
                // 设置锁的等待时间和超时时间
                boolean acquired = redissonMultiLock.tryLock(50, 3000, TimeUnit.MILLISECONDS);
                boolean flag = true;

                // 获取到锁 业务执行
                if (acquired) {
                    for (BasketDto basketDto : basketDtoList) {
                        // 乐观锁库存扣减
                        SkuDto skuDto = skuService.getStocksAndVersionBySkuId(basketDto.getSkuId());
                        Boolean success = skuService.updateStocksLock(basketDto.getSkuId(), basketDto.getStocks(), skuDto.getVersion());
                        flag &= success;
                    }

                    redissonMultiLock.unlock();

                    // 根据结果判断是否回滚与主题发送
                    if (!flag) {
                        rocketMQTemplate.syncSend(TopicConstant.ORDER_ROLLBACK_TOPIC, orderParam);
                        throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
                    }
                    rocketMQTemplate.syncSend(TopicConstant.PAYMENT_TOPIC, orderParam);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // 释放锁
                redissonMultiLock.unlock();
            }
        } else if (TagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE.equals(orderParam.getTag())) {

            // 乐观锁库存扣减
            SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
            Boolean success = skuService.updateStocksLock(orderParam.getSkuId(), orderParam.getProdCount(), skuDto.getVersion());

            // 库存扣减结果发送至不同主题
            String nextTopic = success ? TopicConstant.PAYMENT_TOPIC : TopicConstant.ORDER_ROLLBACK_TOPIC;
            rocketMQTemplate.syncSend(nextTopic, orderParam);

        } else if (TagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE.equals(orderParam.getTag())) {
            boolean flag = true;

            for (BasketDto basketDto : orderParam.getBasketDtoList()) {
                // 乐观锁库存扣减
                SkuDto skuDto = skuService.getStocksAndVersionBySkuId(basketDto.getSkuId());
                Boolean success = skuService.updateStocksLock(basketDto.getSkuId(), basketDto.getStocks(), skuDto.getVersion());
                flag &= success;
            }

            // 库存扣减结果发送至不同主题
            String nextTopic = flag ? TopicConstant.PAYMENT_TOPIC : TopicConstant.ORDER_ROLLBACK_TOPIC;
            rocketMQTemplate.syncSend(nextTopic, orderParam);
        }

    }
}
