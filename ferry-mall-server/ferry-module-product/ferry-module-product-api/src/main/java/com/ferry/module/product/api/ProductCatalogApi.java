package com.ferry.module.product.api;

import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.product.api.dto.ProductCategoryResp;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import java.util.List;

public interface ProductCatalogApi {
    List<ProductCategoryResp> categoryTree();
    PageResult<ProductSpuSnapshot> page(PageParam pageParam);
    ProductSpuSnapshot detail(Long id);
}
