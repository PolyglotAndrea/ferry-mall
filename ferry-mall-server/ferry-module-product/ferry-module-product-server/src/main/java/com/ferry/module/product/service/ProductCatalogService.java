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
import com.ferry.module.product.api.dto.ProductUpdateReq;
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
        return page(pageParam, null, null, null);
    }

    public PageResult<ProductSpuSnapshot> page(PageParam pageParam, String sort) {
        return page(pageParam, sort, null, null);
    }

    public PageResult<ProductSpuSnapshot> page(PageParam pageParam, String keyword, Long categoryId) {
        return page(pageParam, null, keyword, categoryId);
    }

    public PageResult<ProductSpuSnapshot> page(PageParam pageParam, String sort, String keyword, Long categoryId) {
        LambdaQueryWrapper<ProductSpuDO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(ProductSpuDO::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(ProductSpuDO::getCategoryId, categoryId);
        }
        if ("sales_desc".equals(sort)) {
            wrapper.orderByDesc(ProductSpuDO::getSales);
        } else if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(ProductSpuDO::getPriceCent);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(ProductSpuDO::getPriceCent);
        } else {
            wrapper.orderByDesc(ProductSpuDO::getId);
        }
        Page<ProductSpuDO> page = productSpuMapper.selectPage(new Page<>(pageParam.pageNo(), pageParam.pageSize()), wrapper);
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

    @Transactional(rollbackFor = Exception.class)
    public ProductSpuSnapshot update(Long id, ProductUpdateReq req) {
        ProductSpuDO product = productSpuMapper.selectById(id);
        if (product == null) {
            throw new FerryBusinessException(404, "商品不存在");
        }
        product.setCategoryId(req.categoryId());
        product.setName(req.name());
        product.setSubtitle(req.subtitle());
        product.setCoverUrl(req.coverUrl());
        product.setPriceCent(req.priceCent());
        product.setMarketPriceCent(req.marketPriceCent());
        product.setStock(req.stock());
        product.setStatus(req.status());
        productSpuMapper.updateById(product);
        return toProductResp(product);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ProductSpuDO product = productSpuMapper.selectById(id);
        if (product == null) {
            throw new FerryBusinessException(404, "商品不存在");
        }
        productSpuMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductSpuSnapshot toggleStatus(Long id) {
        ProductSpuDO product = productSpuMapper.selectById(id);
        if (product == null) {
            throw new FerryBusinessException(404, "商品不存在");
        }
        product.setStatus(Integer.valueOf(1).equals(product.getStatus()) ? 0 : 1);
        productSpuMapper.updateById(product);
        return toProductResp(product);
    }

    private ProductCategoryResp toCategoryResp(ProductCategoryDO item, List<ProductCategoryDO> all) {
        List<ProductCategoryResp> children = all.stream()
            .filter(child -> item.getId().equals(child.getParentId()))
            .map(child -> toCategoryResp(child, all))
            .toList();
        return new ProductCategoryResp(item.getId(), item.getParentId(), item.getName(), children);
    }

    public List<ProductSpuDO> recommend(int limit) {
        return productSpuMapper.selectList(new LambdaQueryWrapper<ProductSpuDO>()
            .eq(ProductSpuDO::getStatus, 1)
            .orderByDesc(ProductSpuDO::getSales)
            .last("LIMIT " + limit));
    }

    private ProductSpuSnapshot toProductResp(ProductSpuDO product) {
        return new ProductSpuSnapshot(product.getId(), product.getCategoryId(), product.getStoreId(), product.getName(), product.getSubtitle(), product.getCoverUrl(), product.getPriceCent(), product.getMarketPriceCent(), product.getStock(), product.getSales(), product.getStatus());
    }
}
