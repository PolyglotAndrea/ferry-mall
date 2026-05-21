package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.MemberUserDO;
import com.ferry.module.member.dal.dataobject.PointsExchangeDO;
import com.ferry.module.member.dal.dataobject.PointsProductDO;
import com.ferry.module.member.dal.mapper.MemberUserMapper;
import com.ferry.module.member.dal.mapper.PointsExchangeMapper;
import com.ferry.module.member.dal.mapper.PointsProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointsMallService {

    private final PointsProductMapper pointsProductMapper;
    private final PointsExchangeMapper pointsExchangeMapper;
    private final MemberUserMapper memberUserMapper;

    public PointsMallService(PointsProductMapper pointsProductMapper,
                             PointsExchangeMapper pointsExchangeMapper,
                             MemberUserMapper memberUserMapper) {
        this.pointsProductMapper = pointsProductMapper;
        this.pointsExchangeMapper = pointsExchangeMapper;
        this.memberUserMapper = memberUserMapper;
    }

    public List<PointsProductDO> listProducts() {
        return pointsProductMapper.selectList(
            new LambdaQueryWrapper<PointsProductDO>()
                .eq(PointsProductDO::getStatus, 1)
                .eq(PointsProductDO::getTenantId, TenantContext.getTenantId())
                .gt(PointsProductDO::getStock, 0)
                .orderByAsc(PointsProductDO::getSort));
    }

    public PageResult<PointsProductDO> pageProducts(PageParam pageParam) {
        LambdaQueryWrapper<PointsProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsProductDO::getStatus, 1)
               .eq(PointsProductDO::getTenantId, TenantContext.getTenantId())
               .gt(PointsProductDO::getStock, 0)
               .orderByAsc(PointsProductDO::getSort);
        Page<PointsProductDO> page = pointsProductMapper.selectPage(
                new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    public PointsProductDO getProductDetail(Long productId) {
        PointsProductDO product = pointsProductMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new FerryBusinessException(404, "商品不存在或已下架");
        }
        return product;
    }

    @Transactional(rollbackFor = Exception.class)
    public void exchange(Long memberId, Long productId) {
        PointsProductDO product = pointsProductMapper.selectById(productId);
        if (product == null || product.getStatus() != 1 || product.getStock() <= 0) {
            throw new FerryBusinessException(400, "商品不存在或已下架");
        }
        MemberUserDO member = memberUserMapper.selectById(memberId);
        if (member == null) {
            throw new FerryBusinessException(404, "会员不存在");
        }
        int currentPoints = member.getPoints() != null ? member.getPoints() : 0;
        if (currentPoints < product.getPoints()) {
            throw new FerryBusinessException(400, "积分不足");
        }
        member.setPoints(currentPoints - product.getPoints());
        memberUserMapper.updateById(member);

        product.setStock(product.getStock() - 1);
        pointsProductMapper.updateById(product);

        PointsExchangeDO record = new PointsExchangeDO();
        record.setTenantId(TenantContext.getTenantId());
        record.setMemberId(memberId);
        record.setProductId(productId);
        record.setProductName(product.getName());
        record.setPoints(product.getPoints());
        record.setStatus(1);
        record.setCreatedAt(LocalDateTime.now());
        pointsExchangeMapper.insert(record);
    }

    public List<PointsExchangeDO> getExchanges(Long memberId) {
        return pointsExchangeMapper.selectList(
            new LambdaQueryWrapper<PointsExchangeDO>()
                .eq(PointsExchangeDO::getMemberId, memberId)
                .eq(PointsExchangeDO::getTenantId, TenantContext.getTenantId())
                .orderByDesc(PointsExchangeDO::getId));
    }

    public PageResult<PointsExchangeDO> pageExchangeRecords(Long memberId, PageParam pageParam) {
        LambdaQueryWrapper<PointsExchangeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsExchangeDO::getMemberId, memberId)
               .eq(PointsExchangeDO::getTenantId, TenantContext.getTenantId())
               .orderByDesc(PointsExchangeDO::getId);
        Page<PointsExchangeDO> page = pointsExchangeMapper.selectPage(
                new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }
}
