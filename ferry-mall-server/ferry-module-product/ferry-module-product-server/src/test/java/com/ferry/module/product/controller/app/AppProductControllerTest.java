package com.ferry.module.product.controller.app;

import com.ferry.framework.web.core.PageParam;
import com.ferry.framework.web.core.PageResult;
import com.ferry.module.product.api.dto.ProductCategoryResp;
import com.ferry.module.product.api.dto.ProductSpuSnapshot;
import com.ferry.module.product.service.ProductCatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppProductControllerTest {

    private MockMvc mockMvc;
    private ProductCatalogService productCatalogService;

    @BeforeEach
    void setup() {
        productCatalogService = mock(ProductCatalogService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AppProductController(productCatalogService)).build();
    }

    @Test
    void categoryTree_returnsList() throws Exception {
        when(productCatalogService.categoryTree())
            .thenReturn(List.of(new ProductCategoryResp(1L, 0L, "电子产品", List.of())));

        mockMvc.perform(get("/app-api/product/category/tree"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data[0].name").value("电子产品"));
    }

    @Test
    void page_returnsProducts() throws Exception {
        var pageResult = PageResult.of(
            List.of(new ProductSpuSnapshot(1L, 1L, 1L, "iPhone 15", null, null, 599900, 699900, 100, 0, 1)),
            1L, 10);

        when(productCatalogService.page(any(PageParam.class))).thenReturn(pageResult);

        mockMvc.perform(get("/app-api/product/spu/page?pageNo=1&pageSize=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.list[0].name").value("iPhone 15"));
    }
}
