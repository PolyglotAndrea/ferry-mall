package com.ferry.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.framework.web.exception.FerryBusinessException;
import com.ferry.module.product.api.ProductCatalogApi;
import com.ferry.module.product.api.dto.ProductCategoryResp;
import com.ferry.module.product.api.dto.ProductCreateReq;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import com.ferry.module.product.dal.dataobject.ProductCategoryDO;
import com.ferry.module.product.dal.dataobject.ProductSpuDO;
import com.ferry.module.product.dal.mapper.ProductCategoryMapper;
import com.ferry.module.product.dal.mapper.ProductSpuMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCatalogService implements ProductCatalogApi {
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductSpuMapper productSpuMapper;

    public ProductCatalogService(ProductCategoryMapper productCategoryMapper, ProductSpuMapper productSpuMapper) {
        this.productCategoryMapper = productCategoryMapper;
        this.productSpuMapper = productSpuMapper;
    }

    @Override
    public List<ProductCategoryResp> categoryTree() {
        List<ProductCategoryDO> categories = productCategoryMapper.selectList(new LambdaQueryWrapper<ProductCategoryDO>()
            .eq(ProductCategoryDO::getVisible, 1)
            .orderByAsc(ProductCategoryDO::getSort)
            .orderByAsc(ProductCategoryDO::getId));
        return categories.stream()
            .filter(item -> item.getParentId() == null || item.getParentId() == 0L)
            .map(item -> toCategoryResp(item, categories))
            .toList();
    }

    @Override
    public PageResult<ProductSpuSnapshot> page(PageParam pageParam) {
        Page<ProductSpuDO> page = productSpuMapper.selectPage(new Page<>(pageParam.pageNo(), pageParam.pageSize()), new LambdaQueryWrapper<ProductSpuDO>()
            .eq(ProductSpuDO::getStatus, 1)
            .orderByDesc(ProductSpuDO::getId));
        return PageResult.of(page.getRecords().stream().map(this::toProductResp).toList(), page.getTotal(), pageParam.pageSize());
    }

    @Override
    public ProductSpuSnapshot detail(Long id) {
        ProductSpuDO product = productSpuMapper.selectById(id);
        if (product == null || !Integer.valueOf(1).equals(product.getStatus())) {
            throw new FerryBusinessException(404, "商品不存在");
        }
        return toProductResp(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductSpuSnapshot create(ProductCreateReq req) {
        ProductSpuDO product = new ProductSpuDO();
        product.setCategoryId(req.categoryId());
        product.setStoreId(1L);
        product.setName(req.name());
        product.setSubtitle(req.subtitle());
        product.setCoverUrl(req.coverUrl());
        product.setPriceCent(req.priceCent());
        product.setMarketPriceCent(req.marketPriceCent());
        product.setStock(req.stock());
        product.setSales(0);
        product.setStatus(1);
        productSpuMapper.insert(product);
        return toProductResp(product);
    }

    private ProductCategoryResp toCategoryResp(ProductCategoryDO item, List<ProductCategoryDO> all) {
        List<ProductCategoryResp> children = all.stream()
            .filter(child -> item.getId().equals(child.getParentId()))
            .map(child -> toCategoryResp(child, all))
            .toList();
        return new ProductCategoryResp(item.getId(), item.getParentId(), item.getName(), children);
    }

    private ProductSpuSnapshot toProductResp(ProductSpuDO product) {
        return new ProductSpuSnapshot(product.getId(), product.getCategoryId(), product.getStoreId(), product.getName(), product.getSubtitle(), product.getCoverUrl(), product.getPriceCent(), product.getMarketPriceCent(), product.getStock(), product.getSales(), product.getStatus());
    }
}
