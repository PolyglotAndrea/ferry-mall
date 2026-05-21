package com.ferry.framework.log.config;

import com.ferry.framework.log.aspect.SlowLogAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnProperty(prefix = "ferry.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = "ferry.log.slow-log", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SlowLogAspect slowLogAspect(LogProperties logProperties) {
        return new SlowLogAspect(logProperties);
    }
}
