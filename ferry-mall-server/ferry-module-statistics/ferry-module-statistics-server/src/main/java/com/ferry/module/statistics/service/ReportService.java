package com.ferry.module.statistics.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final JdbcTemplate jdbcTemplate;

    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> tradeOverview() {
        String sql = """
            select
                count(1) as total_orders,
                coalesce(sum(pay_amount_cent), 0) as total_amount_cent,
                count(distinct member_id) as total_buyers,
                avg(pay_amount_cent) as avg_order_amount_cent
            from order_info
            where status >= 20
            """;
        return jdbcTemplate.queryForMap(sql);
    }

    public List<Map<String, Object>> productSalesRank(int limit) {
        String sql = """
            select oi.product_name, sum(oi.quantity) as total_qty, sum(oi.total_cent) as total_amount_cent
            from order_item oi
            join order_info o on oi.order_id = o.id
            where o.status >= 20
            group by oi.product_name
            order by total_qty desc
            limit ?
            """;
        return jdbcTemplate.queryForList(sql, limit);
    }

    public List<Map<String, Object>> memberGrowth(int days) {
        String sql = """
            select date(created_at) as day, count(1) as new_members
            from member_user
            where created_at >= date_sub(current_date, interval ? day)
            group by date(created_at)
            order by day desc
            """;
        return jdbcTemplate.queryForList(sql, days);
    }

    public List<Map<String, Object>> hourlyOrderDistribution() {
        String sql = """
            select hour(created_at) as hour, count(1) as order_count
            from order_info
            where date(created_at) = current_date
            group by hour(created_at)
            order by hour
            """;
        return jdbcTemplate.queryForList(sql);
    }
}
