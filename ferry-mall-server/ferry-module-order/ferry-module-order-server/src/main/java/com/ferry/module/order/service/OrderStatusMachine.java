package com.ferry.module.order.service;

import com.ferry.framework.web.exception.FerryBusinessException;
import java.util.Map;
import java.util.Set;

public final class OrderStatusMachine {

    public static final int PENDING_PAYMENT = 10;
    public static final int PAID = 20;
    public static final int SHIPPED = 30;
    public static final int COMPLETED = 40;
    public static final int CANCELLED = 50;
    public static final int REFUNDING = 60;
    public static final int REFUNDED = 70;

    private static final Map<Integer, Set<Integer>> TRANSITIONS = Map.of(
        PENDING_PAYMENT, Set.of(PAID, CANCELLED),
        PAID, Set.of(SHIPPED, REFUNDING),
        SHIPPED, Set.of(COMPLETED, REFUNDING),
        COMPLETED, Set.of(REFUNDING),
        REFUNDING, Set.of(REFUNDED),
        CANCELLED, Set.of(),
        REFUNDED, Set.of()
    );

    private static final Map<Integer, String> STATUS_TEXT = Map.of(
        PENDING_PAYMENT, "待支付",
        PAID, "待发货",
        SHIPPED, "待收货",
        COMPLETED, "已完成",
        CANCELLED, "已取消",
        REFUNDING, "退款中",
        REFUNDED, "已退款"
    );

    private OrderStatusMachine() {}

    public static void checkTransition(int from, int to) {
        Set<Integer> allowed = TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new FerryBusinessException(400,
                "订单状态不允许从 " + textOf(from) + " 变为 " + textOf(to));
        }
    }

    public static String textOf(int status) {
        return STATUS_TEXT.getOrDefault(status, "未知");
    }

    public static boolean canCancel(int status) {
        return status == PENDING_PAYMENT;
    }

    public static boolean canPay(int status) {
        return status == PENDING_PAYMENT;
    }

    public static boolean canShip(int status) {
        return status == PAID;
    }

    public static boolean canReceive(int status) {
        return status == SHIPPED;
    }
}
