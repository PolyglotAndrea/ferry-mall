package com.ferry.module.product.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.product.api.InventoryApi;
import com.ferry.module.product.api.dto.StockDeductReq;
import com.ferry.module.product.dal.dataobject.ProductSpuDO;
import com.ferry.module.product.dal.mapper.ProductSpuMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService implements InventoryApi {
    private final ProductSpuMapper productSpuMapper;

    public InventoryService(ProductSpuMapper productSpuMapper) {
        this.productSpuMapper = productSpuMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deduct(Long spuId, int quantity) {
        ProductSpuDO product = productSpuMapper.selectById(spuId);
        if (product == null) {
            throw new FerryBusinessException(404, "商品不存在");
        }
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new FerryBusinessException(400, "商品「" + product.getName() + "」库存不足");
        }

        int affected = productSpuMapper.update(null, new LambdaUpdateWrapper<ProductSpuDO>()
            .eq(ProductSpuDO::getId, spuId)
            .ge(ProductSpuDO::getStock, quantity)
            .setSql("stock = stock - " + quantity + ", sales = sales + " + quantity));

        if (affected == 0) {
            throw new FerryBusinessException(409, "商品「" + product.getName() + "」库存不足，请重新下单");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductBatch(List<StockDeductReq> items) {
        for (var item : items) {
            deduct(item.spuId(), item.quantity());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long spuId, int quantity) {
        productSpuMapper.update(null, new LambdaUpdateWrapper<ProductSpuDO>()
            .eq(ProductSpuDO::getId, spuId)
            .setSql("stock = stock + " + quantity + ", sales = sales - " + quantity));
    }

    @Override
    public int getStock(Long spuId) {
        ProductSpuDO product = productSpuMapper.selectById(spuId);
        return product != null && product.getStock() != null ? product.getStock() : 0;
    }
}
