package com.ferry.module.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.settlement.api.dto.SettlementBillResp;
import com.ferry.module.settlement.dal.dataobject.SettlementBillDO;
import com.ferry.module.settlement.dal.mapper.SettlementBillMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementBillService {
    private final SettlementBillMapper settlementBillMapper;

    public SettlementBillService(SettlementBillMapper settlementBillMapper) {
        this.settlementBillMapper = settlementBillMapper;
    }

    public PageResult<SettlementBillResp> page(PageParam pageParam) {
        Page<SettlementBillDO> page = settlementBillMapper.selectPage(new Page<>(pageParam.pageNo(), pageParam.pageSize()), new LambdaQueryWrapper<SettlementBillDO>()
            .orderByDesc(SettlementBillDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    private SettlementBillResp toResp(SettlementBillDO bill) {
        return new SettlementBillResp(bill.getId(), bill.getMerchantId(), bill.getMerchantName(), bill.getOrderAmountCent(), bill.getCommissionCent(), bill.getPayableCent(), bill.getStatus(), statusText(bill.getStatus()));
    }

    @Transactional(rollbackFor = Exception.class)
    public SettlementBillResp createBill(Long merchantId, String merchantName, Integer orderAmountCent, Integer commissionCent) {
        SettlementBillDO bill = new SettlementBillDO();
        bill.setMerchantId(merchantId);
        bill.setMerchantName(merchantName);
        bill.setOrderAmountCent(orderAmountCent);
        bill.setCommissionCent(commissionCent);
        bill.setPayableCent(orderAmountCent - commissionCent);
        bill.setStatus(10);
        settlementBillMapper.insert(bill);
        return toResp(bill);
    }

    @Transactional(rollbackFor = Exception.class)
    public SettlementBillResp settle(Long id) {
        SettlementBillDO bill = settlementBillMapper.selectById(id);
        if (bill == null) {
            throw new FerryBusinessException(404, "结算单不存在");
        }
        bill.setStatus(20);
        settlementBillMapper.updateById(bill);
        return toResp(bill);
    }

    private String statusText(Integer status) {
        return switch (status == null ? 0 : status) {
            case 10 -> "待结算";
            case 20 -> "已结算";
            default -> "未知";
        };
    }
}
