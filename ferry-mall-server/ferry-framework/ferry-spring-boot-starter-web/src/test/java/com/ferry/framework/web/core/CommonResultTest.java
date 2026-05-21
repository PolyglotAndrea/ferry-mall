package com.ferry.framework.web.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonResultTest {

    @Test
    void success_createsOkResult() {
        CommonResult<String> result = CommonResult.success("data");

        assertEquals(200, result.code());
        assertEquals("success", result.message());
        assertEquals("data", result.data());
        assertTrue(result.timestamp() > 0);
    }

    @Test
    void failed_createsErrorResult() {
        CommonResult<Object> result = CommonResult.failed(500, "error");

        assertEquals(500, result.code());
        assertEquals("error", result.message());
        assertNull(result.data());
    }
}
