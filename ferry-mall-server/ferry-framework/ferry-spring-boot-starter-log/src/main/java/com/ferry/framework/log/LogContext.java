package com.ferry.framework.log;

import org.slf4j.MDC;

import java.util.UUID;

public class LogContext {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String USER_ID_KEY = "userId";

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static String generateTraceId() {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        MDC.put(TRACE_ID_KEY, traceId);
        return traceId;
    }

    public static void clearTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    public static void setUserId(String userId) {
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId);
        }
    }

    public static void clearUserId() {
        MDC.remove(USER_ID_KEY);
    }

    public static void clear() {
        MDC.clear();
    }
}
