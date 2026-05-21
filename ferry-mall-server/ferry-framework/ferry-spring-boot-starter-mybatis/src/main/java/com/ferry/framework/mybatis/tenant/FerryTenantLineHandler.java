package com.ferry.framework.mybatis.tenant;

import com.ferry.framework.web.tenant.TenantContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FerryTenantLineHandler implements TenantLineHandler {

    private static final List<String> IGNORE_TABLES = List.of(
        "sys_tenant", "sys_dict", "sys_dict_item"
    );

    @Override
    public Expression getTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            tenantId = 0L;
        }
        return new LongValue(tenantId);
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return IGNORE_TABLES.stream().anyMatch(tableName::contains);
    }
}
