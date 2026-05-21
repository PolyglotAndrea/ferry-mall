package com.ferry.module.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.order.api.dto.OrderCancelReq;
import com.ferry.module.order.api.dto.OrderCreateReq;
import com.ferry.module.order.api.dto.OrderItemCreateReq;
import com.ferry.module.order.api.dto.OrderItemResp;
import com.ferry.module.order.api.dto.OrderPayReq;
import com.ferry.module.order.api.dto.OrderResp;
import com.ferry.module.order.api.dto.OrderShipReq;
import com.ferry.module.order.dal.dataobject.OrderInfoDO;
import com.ferry.module.order.dal.dataobject.OrderItemDO;
import com.ferry.module.order.dal.mapper.OrderInfoMapper;
import com.ferry.module.order.dal.mapper.OrderItemMapper;
import com.ferry.module.order.api.OrderApi;
import com.ferry.module.product.api.InventoryApi;
import com.ferry.module.product.api.ProductCatalogApi;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import com.ferry.module.product.api.dto.StockDeductReq;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderTradeService implements OrderApi {
    private final OrderInfoMapper orderInfoMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductCatalogApi productCatalogApi;
    private final InventoryApi inventoryApi;

    public OrderTradeService(OrderInfoMapper orderInfoMapper, OrderItemMapper orderItemMapper,
                             ProductCatalogApi productCatalogApi, InventoryApi inventoryApi) {
        this.orderInfoMapper = orderInfoMapper;
        this.orderItemMapper = orderItemMapper;
        this.productCatalogApi = productCatalogApi;
        this.inventoryApi = inventoryApi;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResp create(OrderCreateReq req) {
        long memberId = currentMemberId();

        List<OrderItemCreateReq> items = req.items();
        List<Long> spuIds = items.stream().map(OrderItemCreateReq::spuId).distinct().toList();
        Map<Long, ProductSpuSnapshot> productMap = spuIds.stream()
            .map(productCatalogApi::detail)
            .collect(Collectors.toMap(ProductSpuSnapshot::id, p -> p));

        for (var item : items) {
            ProductSpuSnapshot p = productMap.get(item.spuId());
            if (p == null || !Integer.valueOf(1).equals(p.status())) {
                throw new FerryBusinessException(404, "商品不存在或已下架");
            }
        }

        List<StockDeductReq> deducts = items.stream()
            .map(i -> new StockDeductReq(i.spuId(), i.quantity()))
            .toList();
        inventoryApi.deductBatch(deducts);

        int totalAmountCent = items.stream()
            .mapToInt(i -> productMap.get(i.spuId()).priceCent() * i.quantity())
            .sum();

        String orderNo = generateOrderNo();
        OrderInfoDO order = new OrderInfoDO();
        order.setOrderNo(orderNo);
        order.setMemberId(memberId);
        order.setTotalAmountCent(totalAmountCent);
        order.setDiscountAmountCent(0);
        order.setPayAmountCent(totalAmountCent);
        order.setStatus(OrderStatusMachine.PENDING_PAYMENT);
        order.setReceiverName(defaultIfBlank(req.receiverName(), "Ferry 用户"));
        order.setReceiverMobile(defaultIfBlank(req.receiverMobile(), "13800000000"));
        order.setReceiverAddress(defaultIfBlank(req.receiverAddress(), "上海市浦东新区 Ferry Road 1号"));
        order.setRemark(req.remark());
        orderInfoMapper.insert(order);

        for (var item : items) {
            ProductSpuSnapshot p = productMap.get(item.spuId());
            OrderItemDO orderItem = new OrderItemDO();
            orderItem.setOrderId(order.getId());
            orderItem.setSpuId(item.spuId());
            orderItem.setSkuId(item.skuId());
            orderItem.setStoreId(p.storeId());
            orderItem.setProductName(p.name());
            orderItem.setProductImage(p.coverUrl());
            orderItem.setPriceCent(p.priceCent());
            orderItem.setQuantity(item.quantity());
            orderItem.setTotalCent(p.priceCent() * item.quantity());
            orderItemMapper.insert(orderItem);
        }

        return detail(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResp pay(String orderNo) {
        OrderInfoDO order = getByOrderNo(orderNo);
        OrderStatusMachine.checkTransition(order.getStatus(), OrderStatusMachine.PAID);

        order.setStatus(OrderStatusMachine.PAID);
        order.setPayTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        return detail(orderNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResp pay(OrderPayReq req) {
        return pay(req.orderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResp ship(OrderShipReq req) {
        OrderInfoDO order = getByOrderNo(req.orderNo());
        OrderStatusMachine.checkTransition(order.getStatus(), OrderStatusMachine.SHIPPED);

        order.setStatus(OrderStatusMachine.SHIPPED);
        order.setDeliveryTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        return detail(req.orderNo());
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResp receive(String orderNo) {
        OrderInfoDO order = getByOrderNo(orderNo);
        OrderStatusMachine.checkTransition(order.getStatus(), OrderStatusMachine.COMPLETED);

        order.setStatus(OrderStatusMachine.COMPLETED);
        order.setReceiveTime(LocalDateTime.now());
        orderInfoMapper.updateById(order);

        return detail(orderNo);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResp cancel(OrderCancelReq req) {
        OrderInfoDO order = getByOrderNo(req.orderNo());
        if (!OrderStatusMachine.canCancel(order.getStatus())) {
            throw new FerryBusinessException(400, "当前订单状态不允许取消");
        }

        List<OrderItemDO> items = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItemDO>().eq(OrderItemDO::getOrderId, order.getId()));
        for (OrderItemDO item : items) {
            inventoryApi.restore(item.getSpuId(), item.getQuantity());
        }

        order.setStatus(OrderStatusMachine.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(req.reason());
        orderInfoMapper.updateById(order);

        return detail(req.orderNo());
    }

    public OrderResp detail(String orderNo) {
        OrderInfoDO order = orderInfoMapper.selectOne(
            new LambdaQueryWrapper<OrderInfoDO>().eq(OrderInfoDO::getOrderNo, orderNo));
        if (order == null) {
            throw new FerryBusinessException(404, "订单不存在");
        }
        List<OrderItemDO> items = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItemDO>().eq(OrderItemDO::getOrderId, order.getId()));
        return toResp(order, items);
    }

    public PageResult<OrderResp> page(PageParam pageParam) {
        long memberId = currentMemberId();
        Page<OrderInfoDO> page = orderInfoMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<OrderInfoDO>()
                .eq(OrderInfoDO::getMemberId, memberId)
                .orderByDesc(OrderInfoDO::getId));

        List<Long> orderIds = page.getRecords().stream().map(OrderInfoDO::getId).toList();
        Map<Long, List<OrderItemDO>> itemMap = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemDO>().in(OrderItemDO::getOrderId, orderIds))
            .stream().collect(Collectors.groupingBy(OrderItemDO::getOrderId));

        List<OrderResp> list = page.getRecords().stream()
            .map(o -> toResp(o, itemMap.getOrDefault(o.getId(), List.of())))
            .toList();
        return PageResult.of(list, page.getTotal(), pageParam.pageSize());
    }

    private OrderInfoDO getByOrderNo(String orderNo) {
        OrderInfoDO order = orderInfoMapper.selectOne(
            new LambdaQueryWrapper<OrderInfoDO>().eq(OrderInfoDO::getOrderNo, orderNo));
        if (order == null) {
            throw new FerryBusinessException(404, "订单不存在");
        }
        return order;
    }

    private OrderResp toResp(OrderInfoDO o, List<OrderItemDO> items) {
        List<OrderItemResp> itemResps = items.stream()
            .map(i -> new OrderItemResp(i.getSpuId(), i.getSkuId(), i.getProductName(),
                i.getProductImage(), i.getPriceCent(), i.getQuantity(), i.getTotalCent()))
            .toList();
        return new OrderResp(
            o.getId(), o.getOrderNo(), o.getTotalAmountCent(), o.getDiscountAmountCent(),
            o.getPayAmountCent(), o.getStatus(), OrderStatusMachine.textOf(o.getStatus()),
            o.getReceiverName(), o.getReceiverMobile(), o.getReceiverAddress(),
            o.getRemark(), o.getPayTime(), o.getDeliveryTime(), o.getReceiveTime(),
            o.getCancelTime(), o.getCancelReason(), o.getCreatedAt(), itemResps);
    }

    private String generateOrderNo() {
        return "FM" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
            + String.format("%04d", (int) (Math.random() * 10000));
    }

    private long currentMemberId() {
        return 10001L;
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
