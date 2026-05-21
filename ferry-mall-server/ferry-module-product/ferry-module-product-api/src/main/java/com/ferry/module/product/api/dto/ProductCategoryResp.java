package com.ferry.module.product.api.dto;

import java.util.List;

public record ProductCategoryResp(Long id, Long parentId, String name, List<ProductCategoryResp> children) {}
