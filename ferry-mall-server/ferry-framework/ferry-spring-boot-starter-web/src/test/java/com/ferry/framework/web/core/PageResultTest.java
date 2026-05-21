package com.ferry.framework.web.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResultTest {

    @Test
    void of_createsPageResult() {
        PageResult<String> result = PageResult.of(List.of("a", "b"), 10L, 10);

        assertEquals(2, result.list().size());
        assertEquals(10L, result.total());
        assertEquals(1, result.pages());
    }

    @Test
    void of_calculatesPages() {
        PageResult<String> result = PageResult.of(List.of("a"), 25L, 10);

        assertEquals(3, result.pages());
    }
}
