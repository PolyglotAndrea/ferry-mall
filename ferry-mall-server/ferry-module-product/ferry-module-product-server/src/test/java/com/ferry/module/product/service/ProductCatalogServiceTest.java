package com.ferry.module.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ferry.framework.web.core.PageParam;
import com.ferry.module.product.dal.dataobject.ProductCategoryDO;
import com.ferry.module.product.dal.dataobject.ProductSpuDO;
import com.ferry.module.product.dal.mapper.ProductCategoryMapper;
import com.ferry.module.product.dal.mapper.ProductSpuMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCatalogServiceTest {

    @Mock
    private ProductCategoryMapper productCategoryMapper;

    @Mock
    private ProductSpuMapper productSpuMapper;

    @InjectMocks
    private ProductCatalogService productCatalogService;

    @Test
    void categoryTree_returnsHierarchy() {
        ProductCategoryDO root = new ProductCategoryDO();
        root.setId(1L);
        root.setParentId(0L);
        root.setName("电子产品");

        ProductCategoryDO child = new ProductCategoryDO();
        child.setId(2L);
        child.setParentId(1L);
        child.setName("手机");

        when(productCategoryMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(List.of(root, child));

        var tree = productCatalogService.categoryTree();

        assertEquals(1, tree.size());
        assertEquals(1L, tree.get(0).id());
        assertEquals(1, tree.get(0).children().size());
        assertEquals(2L, tree.get(0).children().get(0).id());
    }

    @Test
    void page_returnsProducts() {
        ProductSpuDO spu = new ProductSpuDO();
        spu.setId(1L);
        spu.setName("iPhone 15");
        spu.setStatus(1);

        Page<ProductSpuDO> page = new Page<>(1, 10);
        page.setRecords(List.of(spu));
        page.setTotal(1);

        when(productSpuMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        var result = productCatalogService.page(new PageParam(1, 10));

        assertNotNull(result);
        assertEquals(1, result.list().size());
        assertEquals("iPhone 15", result.list().get(0).name());
    }
}
