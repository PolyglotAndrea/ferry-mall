package com.ferry.module.aftermarket.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.aftermarket.api.dto.AftermarketApplyReq;
import com.ferry.module.aftermarket.api.dto.AftermarketResp;
import com.ferry.module.aftermarket.dal.dataobject.AftermarketRecordDO;
import com.ferry.module.aftermarket.dal.mapper.AftermarketRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AftermarketService {
    private final AftermarketRecordMapper aftermarketRecordMapper;

    public AftermarketService(AftermarketRecordMapper aftermarketRecordMapper) {
        this.aftermarketRecordMapper = aftermarketRecordMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public AftermarketResp apply(AftermarketApplyReq req) {
        AftermarketRecordDO record = new AftermarketRecordDO();
        record.setOrderId(req.orderId());
        record.setReason(req.reason());
        record.setStatus(10);
        aftermarketRecordMapper.insert(record);
        return toResp(record);
    }

    public PageResult<AftermarketResp> page(PageParam pageParam) {
        Page<AftermarketRecordDO> page = aftermarketRecordMapper.selectPage(new Page<>(pageParam.pageNo(), pageParam.pageSize()), new LambdaQueryWrapper<AftermarketRecordDO>()
            .orderByDesc(AftermarketRecordDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public AftermarketResp approve(Long id) {
        AftermarketRecordDO record = aftermarketRecordMapper.selectById(id);
        if (record == null) {
            throw new FerryBusinessException(404, "售后记录不存在");
        }
        record.setStatus(20);
        aftermarketRecordMapper.updateById(record);
        return toResp(record);
    }

    @Transactional(rollbackFor = Exception.class)
    public AftermarketResp reject(Long id, String reason) {
        AftermarketRecordDO record = aftermarketRecordMapper.selectById(id);
        if (record == null) {
            throw new FerryBusinessException(404, "售后记录不存在");
        }
        record.setStatus(40);
        aftermarketRecordMapper.updateById(record);
        return toResp(record);
    }

    @Transactional(rollbackFor = Exception.class)
    public AftermarketResp complete(Long id) {
        AftermarketRecordDO record = aftermarketRecordMapper.selectById(id);
        if (record == null) {
            throw new FerryBusinessException(404, "售后记录不存在");
        }
        record.setStatus(30);
        aftermarketRecordMapper.updateById(record);
        return toResp(record);
    }

    private AftermarketResp toResp(AftermarketRecordDO record) {
        return new AftermarketResp(record.getId(), record.getOrderId(), record.getReason(), record.getStatus(), statusText(record.getStatus()), record.getCreatedAt());
    }

    private String statusText(Integer status) {
        return switch (status == null ? 0 : status) {
            case 10 -> "待审核";
            case 20 -> "处理中";
            case 30 -> "已完成";
            case 40 -> "已拒绝";
            default -> "未知";
        };
    }
}
