package com.ferry.module.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.system.api.dto.TenantCreateReq;
import com.ferry.module.system.api.dto.TenantResp;
import com.ferry.module.system.dal.dataobject.SysTenantDO;
import com.ferry.module.system.dal.mapper.SysTenantMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SysTenantService {

    private final SysTenantMapper sysTenantMapper;

    public SysTenantService(SysTenantMapper sysTenantMapper) {
        this.sysTenantMapper = sysTenantMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public TenantResp create(TenantCreateReq req) {
        SysTenantDO tenant = new SysTenantDO();
        tenant.setName(req.name());
        tenant.setContactName(req.contactName());
        tenant.setContactMobile(req.contactMobile());
        tenant.setPackageId(req.packageId());
        tenant.setExpireTime(req.expireTime());
        tenant.setAccountCount(req.accountCount() != null ? req.accountCount() : 0);
        tenant.setStatus(1);
        sysTenantMapper.insert(tenant);
        return toResp(tenant);
    }

    @Transactional(rollbackFor = Exception.class)
    public TenantResp update(Long id, TenantCreateReq req) {
        SysTenantDO tenant = sysTenantMapper.selectById(id);
        if (tenant == null) {
            throw new FerryBusinessException(404, "租户不存在");
        }
        tenant.setName(req.name());
        tenant.setContactName(req.contactName());
        tenant.setContactMobile(req.contactMobile());
        tenant.setPackageId(req.packageId());
        tenant.setExpireTime(req.expireTime());
        if (req.accountCount() != null) {
            tenant.setAccountCount(req.accountCount());
        }
        tenant.setUpdatedAt(LocalDateTime.now());
        sysTenantMapper.updateById(tenant);
        return toResp(tenant);
    }

    public TenantResp detail(Long id) {
        SysTenantDO tenant = sysTenantMapper.selectById(id);
        if (tenant == null) {
            throw new FerryBusinessException(404, "租户不存在");
        }
        return toResp(tenant);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysTenantMapper.deleteById(id);
    }

    public PageResult<TenantResp> page(PageParam pageParam) {
        Page<SysTenantDO> page = sysTenantMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<SysTenantDO>()
                .orderByDesc(SysTenantDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    private TenantResp toResp(SysTenantDO t) {
        return new TenantResp(t.getId(), t.getName(), t.getContactName(), t.getContactMobile(),
            t.getPackageId(), t.getExpireTime(), t.getAccountCount(), t.getStatus(), t.getCreatedAt());
    }
}
