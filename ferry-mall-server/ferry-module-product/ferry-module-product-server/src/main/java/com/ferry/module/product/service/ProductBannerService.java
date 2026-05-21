package com.ferry.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.product.dal.dataobject.ProductBannerDO;
import com.ferry.module.product.dal.mapper.ProductBannerMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductBannerService {
    private final ProductBannerMapper productBannerMapper;

    public ProductBannerService(ProductBannerMapper productBannerMapper) {
        this.productBannerMapper = productBannerMapper;
    }

    public List<ProductBannerDO> list() {
        return productBannerMapper.selectList(new LambdaQueryWrapper<ProductBannerDO>()
            .eq(ProductBannerDO::getTenantId, TenantContext.getTenantId())
            .eq(ProductBannerDO::getStatus, 1)
            .orderByAsc(ProductBannerDO::getSort));
    }
}
