package com.ferry.mall.server.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.module.order.dal.dataobject.OrderInfoDO;
import com.ferry.module.order.dal.mapper.OrderInfoMapper;
import com.ferry.module.order.service.OrderStatusMachine;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderTimeoutJob {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutJob.class);

    private final OrderInfoMapper orderInfoMapper;

    public OrderTimeoutJob(OrderInfoMapper orderInfoMapper) {
        this.orderInfoMapper = orderInfoMapper;
    }

    @XxlJob("cancelTimeoutOrder")
    public void cancelTimeoutOrder() {
        log.info("[XXL-JOB] 开始扫描超时未支付订单");
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);

        List<OrderInfoDO> timeoutOrders = orderInfoMapper.selectList(
            new LambdaQueryWrapper<OrderInfoDO>()
                .eq(OrderInfoDO::getStatus, OrderStatusMachine.PENDING_PAYMENT)
                .lt(OrderInfoDO::getCreatedAt, deadline));

        for (OrderInfoDO order : timeoutOrders) {
            order.setStatus(OrderStatusMachine.CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("超时未支付，系统自动取消");
            orderInfoMapper.updateById(order);
            log.info("[XXL-JOB] 订单 {} 已自动取消", order.getOrderNo());
        }

        log.info("[XXL-JOB] 扫描完成，共取消 {} 笔超时订单", timeoutOrders.size());
    }
}
