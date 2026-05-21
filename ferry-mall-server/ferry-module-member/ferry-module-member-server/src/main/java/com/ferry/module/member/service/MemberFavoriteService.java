package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.MemberFavoriteDO;
import com.ferry.module.member.dal.mapper.MemberFavoriteMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberFavoriteService {
    private final MemberFavoriteMapper memberFavoriteMapper;

    public MemberFavoriteService(MemberFavoriteMapper memberFavoriteMapper) {
        this.memberFavoriteMapper = memberFavoriteMapper;
    }

    public List<MemberFavoriteDO> list(Long memberId) {
        return memberFavoriteMapper.selectList(new LambdaQueryWrapper<MemberFavoriteDO>()
            .eq(MemberFavoriteDO::getTenantId, TenantContext.getTenantId())
            .eq(MemberFavoriteDO::getMemberId, memberId)
            .orderByDesc(MemberFavoriteDO::getCreatedAt));
    }

    @Transactional(rollbackFor = Exception.class)
    public MemberFavoriteDO add(Long memberId, Long spuId, String spuName, String spuCover, Integer priceCent) {
        MemberFavoriteDO favorite = new MemberFavoriteDO();
        favorite.setTenantId(TenantContext.getTenantId());
        favorite.setMemberId(memberId);
        favorite.setSpuId(spuId);
        favorite.setSpuName(spuName);
        favorite.setSpuCover(spuCover);
        favorite.setPriceCent(priceCent);
        favorite.setCreatedAt(LocalDateTime.now());
        memberFavoriteMapper.insert(favorite);
        return favorite;
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(Long memberId, Long spuId) {
        memberFavoriteMapper.delete(new LambdaQueryWrapper<MemberFavoriteDO>()
            .eq(MemberFavoriteDO::getTenantId, TenantContext.getTenantId())
            .eq(MemberFavoriteDO::getMemberId, memberId)
            .eq(MemberFavoriteDO::getSpuId, spuId));
    }

    public boolean exists(Long memberId, Long spuId) {
        return memberFavoriteMapper.selectCount(new LambdaQueryWrapper<MemberFavoriteDO>()
            .eq(MemberFavoriteDO::getTenantId, TenantContext.getTenantId())
            .eq(MemberFavoriteDO::getMemberId, memberId)
            .eq(MemberFavoriteDO::getSpuId, spuId)) > 0;
    }
}
