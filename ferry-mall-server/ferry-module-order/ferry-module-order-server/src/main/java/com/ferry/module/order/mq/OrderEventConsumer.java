package com.ferry.module.order.mq;

import com.alibaba.fastjson.JSON;
import com.ferry.module.order.mq.OrderEventProducer.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic = "order-topic", consumerGroup = "order-consumer-group")
public class OrderEventConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        try {
            OrderEvent event = JSON.parseObject(message, OrderEvent.class);
            switch (event.eventType()) {
                case "ORDER_CREATED" -> handleOrderCreated(event);
                case "ORDER_PAID" -> handleOrderPaid(event);
                default -> log.warn("[MQ] 未知事件类型: {}", event.eventType());
            }
        } catch (Exception e) {
            log.error("[MQ] 消息处理失败: {}", message, e);
        }
    }

    private void handleOrderCreated(OrderEvent event) {
        log.info("[MQ] 处理订单创建事件: orderNo={}, memberId={}", event.orderNo(), event.memberId());
    }

    private void handleOrderPaid(OrderEvent event) {
        log.info("[MQ] 处理订单支付事件: orderNo={}", event.orderNo());
    }
}
