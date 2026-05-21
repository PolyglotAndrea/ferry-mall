package com.ferry.module.order.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventProducer {

    private final RocketMQTemplate rocketMQTemplate;

    public OrderEventProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void sendOrderCreated(Long orderId, String orderNo, Long memberId) {
        OrderEvent event = new OrderEvent(orderId, orderNo, memberId, "ORDER_CREATED", System.currentTimeMillis());
        rocketMQTemplate.syncSend("order-topic", MessageBuilder.withPayload(JSON.toJSONString(event)).build());
        log.info("[MQ] 订单创建事件已发送: {}", orderNo);
    }

    public void sendOrderPaid(Long orderId, String orderNo) {
        OrderEvent event = new OrderEvent(orderId, orderNo, null, "ORDER_PAID", System.currentTimeMillis());
        rocketMQTemplate.syncSend("order-topic", MessageBuilder.withPayload(JSON.toJSONString(event)).build());
        log.info("[MQ] 订单支付事件已发送: {}", orderNo);
    }

    public record OrderEvent(Long orderId, String orderNo, Long memberId, String eventType, Long timestamp) {}
}
