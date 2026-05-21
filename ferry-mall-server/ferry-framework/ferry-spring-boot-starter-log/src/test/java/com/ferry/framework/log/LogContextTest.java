package com.ferry.framework.log;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogContextTest {

    @AfterEach
    void tearDown() {
        LogContext.clear();
    }

    @Test
    void generateTraceId_setsAndReturnsId() {
        String traceId = LogContext.generateTraceId();

        assertNotNull(traceId);
        assertFalse(traceId.isEmpty());
        assertEquals(traceId, LogContext.getTraceId());
    }

    @Test
    void setAndGetUserId() {
        LogContext.setUserId("user123");

        assertEquals("user123", org.slf4j.MDC.get("userId"));
    }

    @Test
    void clear_removesAll() {
        LogContext.generateTraceId();
        LogContext.setUserId("user123");

        LogContext.clear();

        assertNull(LogContext.getTraceId());
    }
}
