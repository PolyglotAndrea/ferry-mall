package com.ferry.module.statistics.service;

import com.ferry.module.statistics.api.dto.OverviewResp;
import com.ferry.module.statistics.api.dto.PendingCountResp;
import com.ferry.module.statistics.api.dto.ProductRankResp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsOverviewService {
    private final JdbcTemplate jdbcTemplate;

    public StatisticsOverviewService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public OverviewResp overview() {
        Integer orderCount = queryInt("select count(1) from order_info");
        Integer salesAmountCent = queryInt("select coalesce(sum(pay_amount_cent), 0) from order_info where status >= 20");
        Integer memberCount = queryInt("select count(1) from member_user where status = 1");
        Integer productCount = queryInt("select count(1) from product_spu where status = 1");
        Integer merchantCount = queryInt("select count(1) from merchant_info where status = 20");
        return new OverviewResp(orderCount, salesAmountCent, memberCount, productCount, merchantCount);
    }

    public List<ProductRankResp> topProducts(int limit) {
        String sql = """
            select oi.product_name as name, sum(oi.quantity) as total_quantity, sum(oi.total_cent) as total_amount_cent
            from order_item oi
            join order_info o on oi.order_id = o.id
            where o.status >= 20
            group by oi.product_name
            order by total_quantity desc
            limit ?
            """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new ProductRankResp(
            rs.getString("name"),
            rs.getInt("total_quantity"),
            rs.getInt("total_amount_cent")
        ), limit);
    }

    public List<Map<String, Object>> dailySales(int days) {
        String sql = """
            select date(created_at) as day, count(1) as order_count, coalesce(sum(pay_amount_cent), 0) as amount_cent
            from order_info
            where status >= 20 and created_at >= date_sub(current_date, interval ? day)
            group by date(created_at)
            order by day asc
            """;
        return jdbcTemplate.queryForList(sql, days);
    }

    public PendingCountResp pendingCount() {
        Integer pendingShip = queryInt("select count(1) from order_info where status = 20");
        Integer pendingAftermarket = queryInt("select count(1) from aftermarket_record where status = 10");
        Integer pendingPayment = queryInt("select count(1) from order_info where status = 10");
        Integer pendingSettlement = queryInt("select count(1) from settlement_bill where status = 10");
        return new PendingCountResp(pendingShip, pendingAftermarket, pendingPayment, pendingSettlement);
    }

    private Integer queryInt(String sql) {
        Integer value = jdbcTemplate.queryForObject(sql, Integer.class);
        return value == null ? 0 : value;
    }
}
