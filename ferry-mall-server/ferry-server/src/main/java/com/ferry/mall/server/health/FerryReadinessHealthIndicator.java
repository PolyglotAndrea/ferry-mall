package com.ferry.mall.server.health;

import javax.sql.DataSource;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("ferryReadiness")
public class FerryReadinessHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbcTemplate;

    public FerryReadinessHealthIndicator(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Health health() {
        try {
            Integer result = jdbcTemplate.queryForObject("select 1", Integer.class);
            return Health.up().withDetail("database", result).build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
