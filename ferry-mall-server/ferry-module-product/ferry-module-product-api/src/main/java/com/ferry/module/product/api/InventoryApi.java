package com.ferry.module.product.api;

import com.ferry.module.product.api.dto.StockDeductReq;
import java.util.List;

public interface InventoryApi {
    void deduct(Long spuId, int quantity);
    void deductBatch(List<StockDeductReq> items);
    void restore(Long spuId, int quantity);
    int getStock(Long spuId);
}
