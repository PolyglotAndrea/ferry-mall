package com.ferry.framework.web.core;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageParam(@Min(1) Integer pageNo, @Min(1) @Max(200) Integer pageSize) {
    public PageParam {
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 10 : pageSize;
    }
}
