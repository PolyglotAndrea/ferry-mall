package com.ferry.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.tenant.TenantContext;
import com.ferry.module.product.dal.dataobject.ProductCommentDO;
import com.ferry.module.product.dal.mapper.ProductCommentMapper;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCommentService {
    private final ProductCommentMapper productCommentMapper;

    public ProductCommentService(ProductCommentMapper productCommentMapper) {
        this.productCommentMapper = productCommentMapper;
    }

    public PageResult<ProductCommentDO> pageBySpu(Long spuId, PageParam pageParam) {
        Page<ProductCommentDO> page = productCommentMapper.selectPage(
            new Page<>(pageParam.pageNo(), pageParam.pageSize()),
            new LambdaQueryWrapper<ProductCommentDO>()
                .eq(ProductCommentDO::getTenantId, TenantContext.getTenantId())
                .eq(ProductCommentDO::getSpuId, spuId)
                .eq(ProductCommentDO::getStatus, 1)
                .orderByDesc(ProductCommentDO::getCreatedAt));
        return PageResult.of(page.getRecords(), page.getTotal(), pageParam.pageSize());
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductCommentDO create(Long spuId, Long memberId, String memberNickname,
                                   String memberAvatar, String content, Integer rating, String images) {
        ProductCommentDO comment = new ProductCommentDO();
        comment.setTenantId(TenantContext.getTenantId());
        comment.setSpuId(spuId);
        comment.setMemberId(memberId);
        comment.setMemberNickname(memberNickname);
        comment.setMemberAvatar(memberAvatar);
        comment.setContent(content);
        comment.setRating(rating);
        comment.setImages(images);
        comment.setStatus(1);
        comment.setCreatedAt(LocalDateTime.now());
        productCommentMapper.insert(comment);
        return comment;
    }
}
