package com.ferry.module.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.module.order.api.dto.OrderCreateReq;
import com.ferry.module.order.api.dto.OrderItemCreateReq;
import com.ferry.module.order.dal.dataobject.OrderInfoDO;
import com.ferry.module.order.dal.dataobject.OrderItemDO;
import com.ferry.module.order.dal.mapper.OrderInfoMapper;
import com.ferry.module.order.dal.mapper.OrderItemMapper;
import com.ferry.module.product.api.InventoryApi;
import com.ferry.module.product.api.ProductCatalogApi;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTradeServiceTest {

    @Mock
    private OrderInfoMapper orderInfoMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ProductCatalogApi productCatalogApi;

    @Mock
    private InventoryApi inventoryApi;

    @InjectMocks
    private OrderTradeService orderTradeService;

    @Test
    void createOrder_success() {
        ProductSpuSnapshot product = new ProductSpuSnapshot(
            1L, 1L, 1L, "iPhone 15", null, null, 599900, 699900, 100, 0, 1);

        when(productCatalogApi.detail(1L)).thenReturn(product);
        doNothing().when(inventoryApi).deductBatch(any());

        OrderInfoDO savedOrder = new OrderInfoDO();
        savedOrder.setId(1L);
        savedOrder.setOrderNo("FM202401010000001234");
        savedOrder.setTotalAmountCent(599900);
        savedOrder.setDiscountAmountCent(0);
        savedOrder.setPayAmountCent(599900);
        savedOrder.setStatus(OrderStatusMachine.PENDING_PAYMENT);
        when(orderInfoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(savedOrder);

        OrderItemDO savedItem = new OrderItemDO();
        savedItem.setOrderId(1L);
        savedItem.setSpuId(1L);
        savedItem.setProductName("iPhone 15");
        savedItem.setPriceCent(599900);
        savedItem.setQuantity(1);
        savedItem.setTotalCent(599900);
        when(orderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(savedItem));

        var req = new OrderCreateReq(
            List.of(new OrderItemCreateReq(1L, null, 1)),
            "张三", "13800000000", "上海市", null);

        var resp = orderTradeService.create(req);

        assertNotNull(resp);
        assertTrue(resp.orderNo().startsWith("FM"));
        assertEquals(599900, resp.totalAmountCent());
        assertEquals(1, resp.items().size());

        verify(orderInfoMapper).insert(any(OrderInfoDO.class));
        verify(orderItemMapper).insert(any(OrderItemDO.class));
    }

    @Test
    void pay_success() {
        OrderInfoDO order = new OrderInfoDO();
        order.setId(1L);
        order.setOrderNo("FM202401010000001234");
        order.setStatus(OrderStatusMachine.PENDING_PAYMENT);

        when(orderInfoMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(order);

        var resp = orderTradeService.pay("FM202401010000001234");

        assertNotNull(resp);
        assertEquals(OrderStatusMachine.PAID, order.getStatus());
    }
}
