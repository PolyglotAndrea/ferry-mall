package com.ferry.module.store.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.store.api.dto.StoreResp;
import com.ferry.module.store.dal.dataobject.StoreInfoDO;
import com.ferry.module.store.dal.mapper.StoreInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {
    private final StoreInfoMapper storeInfoMapper;

    public StoreService(StoreInfoMapper storeInfoMapper) {
        this.storeInfoMapper = storeInfoMapper;
    }

    public PageResult<StoreResp> page(PageParam pageParam) {
        Page<StoreInfoDO> page = storeInfoMapper.selectPage(new Page<>(pageParam.pageNo(), pageParam.pageSize()), new LambdaQueryWrapper<StoreInfoDO>()
            .eq(StoreInfoDO::getStatus, 1)
            .orderByDesc(StoreInfoDO::getScore)
            .orderByAsc(StoreInfoDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    public StoreResp detail(Long id) {
        StoreInfoDO store = storeInfoMapper.selectById(id);
        if (store == null || !Integer.valueOf(1).equals(store.getStatus())) {
            throw new FerryBusinessException(404, "店铺不存在");
        }
        return toResp(store);
    }

    @Transactional(rollbackFor = Exception.class)
    public StoreResp create(Long merchantId, String name, String logoUrl, String description) {
        StoreInfoDO store = new StoreInfoDO();
        store.setMerchantId(merchantId);
        store.setName(name);
        store.setLogoUrl(logoUrl);
        store.setDescription(description);
        store.setStatus(1);
        store.setScore(5.0);
        storeInfoMapper.insert(store);
        return toResp(store);
    }

    @Transactional(rollbackFor = Exception.class)
    public StoreResp update(Long id, String name, String logoUrl, String description) {
        StoreInfoDO store = storeInfoMapper.selectById(id);
        if (store == null) {
            throw new FerryBusinessException(404, "店铺不存在");
        }
        store.setName(name);
        store.setLogoUrl(logoUrl);
        store.setDescription(description);
        storeInfoMapper.updateById(store);
        return toResp(store);
    }

    private StoreResp toResp(StoreInfoDO store) {
        return new StoreResp(store.getId(), store.getMerchantId(), store.getName(), store.getLogoUrl(), store.getDescription(), store.getStatus(), store.getScore());
    }
}
