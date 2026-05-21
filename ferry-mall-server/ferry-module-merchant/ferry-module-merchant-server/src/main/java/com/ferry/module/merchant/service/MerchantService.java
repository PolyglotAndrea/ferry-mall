package com.ferry.module.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.merchant.api.dto.MerchantApplyReq;
import com.ferry.module.merchant.api.dto.MerchantResp;
import com.ferry.module.merchant.dal.dataobject.MerchantInfoDO;
import com.ferry.module.merchant.dal.mapper.MerchantInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantService {
    private final MerchantInfoMapper merchantInfoMapper;

    public MerchantService(MerchantInfoMapper merchantInfoMapper) {
        this.merchantInfoMapper = merchantInfoMapper;
    }

    public PageResult<MerchantResp> page(PageParam pageParam) {
        Page<MerchantInfoDO> page = merchantInfoMapper.selectPage(new Page<>(pageParam.pageNo(), pageParam.pageSize()), new LambdaQueryWrapper<MerchantInfoDO>()
            .orderByDesc(MerchantInfoDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public MerchantResp apply(MerchantApplyReq req) {
        MerchantInfoDO merchant = new MerchantInfoDO();
        merchant.setName(req.name());
        merchant.setContactName(req.contactName());
        merchant.setContactMobile(req.contactMobile());
        merchant.setLicenseNo(req.licenseNo());
        merchant.setStatus(10);
        merchantInfoMapper.insert(merchant);
        return toResp(merchant);
    }

    @Transactional(rollbackFor = Exception.class)
    public MerchantResp approve(Long id) {
        MerchantInfoDO merchant = merchantInfoMapper.selectById(id);
        if (merchant == null) {
            throw new FerryBusinessException(404, "商家不存在");
        }
        merchant.setStatus(20);
        merchantInfoMapper.updateById(merchant);
        return toResp(merchant);
    }

    @Transactional(rollbackFor = Exception.class)
    public MerchantResp reject(Long id) {
        MerchantInfoDO merchant = merchantInfoMapper.selectById(id);
        if (merchant == null) {
            throw new FerryBusinessException(404, "商家不存在");
        }
        merchant.setStatus(30);
        merchantInfoMapper.updateById(merchant);
        return toResp(merchant);
    }

    public MerchantResp detail(Long id) {
        MerchantInfoDO merchant = merchantInfoMapper.selectById(id);
        if (merchant == null) {
            throw new FerryBusinessException(404, "商家不存在");
        }
        return toResp(merchant);
    }

    private MerchantResp toResp(MerchantInfoDO merchant) {
        return new MerchantResp(merchant.getId(), merchant.getName(), merchant.getContactName(), merchant.getContactMobile(), merchant.getStatus(), statusText(merchant.getStatus()));
    }

    private String statusText(Integer status) {
        return switch (status == null ? 0 : status) {
            case 10 -> "待审核";
            case 20 -> "已通过";
            case 30 -> "已拒绝";
            default -> "未知";
        };
    }
}
