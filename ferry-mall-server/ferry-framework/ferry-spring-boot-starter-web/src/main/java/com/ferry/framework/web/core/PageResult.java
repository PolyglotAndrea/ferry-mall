package com.ferry.framework.web.core;

import java.util.List;

public record PageResult<T>(List<T> list, long total, int pages) {
    public static <T> PageResult<T> of(List<T> list, long total, int pageSize) {
        int pages = pageSize <= 0 ? 0 : (int) Math.ceil(total * 1.0 / pageSize);
        return new PageResult<>(list, total, pages);
    }
}
