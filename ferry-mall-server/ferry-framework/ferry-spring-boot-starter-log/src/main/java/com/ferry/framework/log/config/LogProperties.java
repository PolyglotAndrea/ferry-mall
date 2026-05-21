package com.ferry.framework.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ferry.log")
public class LogProperties {
    private boolean enabled = true;
    private SlowLog slowLog = new SlowLog();

    @Data
    public static class SlowLog {
        private boolean enabled = true;
        private long thresholdMs = 1000;
        private boolean printArgs = true;
        private boolean printResult = false;
        private int resultMaxLength = 500;
    }
}
