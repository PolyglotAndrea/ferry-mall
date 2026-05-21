package com.ferry.module.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.marketing.dal.dataobject.SeckillActivityDO;
import com.ferry.module.marketing.dal.dataobject.SeckillProductDO;
import com.ferry.module.marketing.dal.mapper.SeckillActivityMapper;
import com.ferry.module.marketing.dal.mapper.SeckillProductMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SeckillService {
    private final SeckillActivityMapper seckillActivityMapper;
    private final SeckillProductMapper seckillProductMapper;

    public SeckillService(SeckillActivityMapper seckillActivityMapper, SeckillProductMapper seckillProductMapper) {
        this.seckillActivityMapper = seckillActivityMapper;
        this.seckillProductMapper = seckillProductMapper;
    }

    public List<SeckillActivityDO> listActivities() {
        LocalDateTime now = LocalDateTime.now();
        return seckillActivityMapper.selectList(new LambdaQueryWrapper<SeckillActivityDO>()
            .eq(SeckillActivityDO::getTenantId, TenantContext.getTenantId())
            .eq(SeckillActivityDO::getStatus, 1)
            .le(SeckillActivityDO::getStartTime, now)
            .ge(SeckillActivityDO::getEndTime, now)
            .orderByDesc(SeckillActivityDO::getId));
    }

    public List<SeckillProductDO> listProducts(Long activityId) {
        return seckillProductMapper.selectList(new LambdaQueryWrapper<SeckillProductDO>()
            .eq(SeckillProductDO::getTenantId, TenantContext.getTenantId())
            .eq(SeckillProductDO::getActivityId, activityId)
            .eq(SeckillProductDO::getStatus, 1)
            .orderByAsc(SeckillProductDO::getId));
    }

    public SeckillProductDO getProductDetail(Long productId) {
        SeckillProductDO product = seckillProductMapper.selectById(productId);
        if (product == null || !product.getTenantId().equals(TenantContext.getTenantId())) {
            throw new FerryBusinessException(404, "秒杀商品不存在");
        }
        if (product.getStock() <= product.getSold()) {
            throw new FerryBusinessException(400, "秒杀商品已售罄");
        }
        return product;
    }
}
