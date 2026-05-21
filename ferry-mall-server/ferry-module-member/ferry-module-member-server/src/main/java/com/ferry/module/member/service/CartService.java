package com.ferry.module.member.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.member.dal.dataobject.CartDO;
import com.ferry.module.member.dal.mapper.CartMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartMapper cartMapper;

    public CartService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    public List<CartDO> list(Long memberId) {
        return cartMapper.selectList(new LambdaQueryWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId)
            .orderByDesc(CartDO::getUpdatedAt));
    }

    @Transactional(rollbackFor = Exception.class)
    public CartDO add(Long memberId, Long spuId, Long skuId, Integer quantity) {
        LambdaQueryWrapper<CartDO> wrapper = new LambdaQueryWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId)
            .eq(CartDO::getSpuId, spuId);
        if (skuId != null) {
            wrapper.eq(CartDO::getSkuId, skuId);
        } else {
            wrapper.isNull(CartDO::getSkuId);
        }

        CartDO exist = cartMapper.selectOne(wrapper);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            exist.setUpdatedAt(LocalDateTime.now());
            cartMapper.updateById(exist);
            return exist;
        }

        CartDO cart = new CartDO();
        cart.setTenantId(TenantContext.getTenantId());
        cart.setMemberId(memberId);
        cart.setSpuId(spuId);
        cart.setSkuId(skuId);
        cart.setQuantity(quantity);
        cart.setSelected(1);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartMapper.insert(cart);
        return cart;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long memberId, Long cartId, Integer quantity) {
        cartMapper.update(null, new LambdaUpdateWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId)
            .eq(CartDO::getId, cartId)
            .set(CartDO::getQuantity, quantity)
            .set(CartDO::getUpdatedAt, LocalDateTime.now()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void remove(Long memberId, Long cartId) {
        cartMapper.delete(new LambdaQueryWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId)
            .eq(CartDO::getId, cartId));
    }

    @Transactional(rollbackFor = Exception.class)
    public void toggleSelect(Long memberId, Long cartId, Integer selected) {
        cartMapper.update(null, new LambdaUpdateWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId)
            .eq(CartDO::getId, cartId)
            .set(CartDO::getSelected, selected)
            .set(CartDO::getUpdatedAt, LocalDateTime.now()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void clear(Long memberId) {
        cartMapper.delete(new LambdaQueryWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId));
    }

    @Transactional(rollbackFor = Exception.class)
    public void toggleAllSelect(Long memberId, Integer selected) {
        cartMapper.update(null, new LambdaUpdateWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId)
            .set(CartDO::getSelected, selected)
            .set(CartDO::getUpdatedAt, LocalDateTime.now()));
    }

    public Integer getCartCount(Long memberId) {
        return cartMapper.selectList(new LambdaQueryWrapper<CartDO>()
            .eq(CartDO::getTenantId, TenantContext.getTenantId())
            .eq(CartDO::getMemberId, memberId))
            .stream().mapToInt(CartDO::getQuantity).sum();
    }
}
