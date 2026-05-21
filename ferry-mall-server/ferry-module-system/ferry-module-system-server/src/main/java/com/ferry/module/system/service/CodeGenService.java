package com.ferry.module.system.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodeGenService {

    private final JdbcTemplate jdbcTemplate;

    public CodeGenService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> listTables() {
        String sql = "SELECT table_name, table_comment FROM information_schema.tables WHERE table_schema = DATABASE()";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> listColumns(String tableName) {
        String sql = "SELECT column_name, data_type, column_comment, is_nullable FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ?";
        return jdbcTemplate.queryForList(sql, tableName);
    }

    public String generateEntity(String tableName, String packageName) {
        List<Map<String, Object>> columns = listColumns(tableName);
        String className = toCamelCase(tableName, true);
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(".dal.dataobject;\n\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableName;\n");
        sb.append("import lombok.Data;\n\n");
        sb.append("@Data\n");
        sb.append("@TableName(\"").append(tableName).append("\")\n");
        sb.append("public class ").append(className).append("DO {\n");
        for (Map<String, Object> col : columns) {
            String colName = (String) col.get("column_name");
            String javaType = toJavaType((String) col.get("data_type"));
            sb.append("    private ").append(javaType).append(" ").append(toCamelCase(colName, false)).append(";\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String toJavaType(String dbType) {
        return switch (dbType.toLowerCase()) {
            case "bigint" -> "Long";
            case "int", "integer", "tinyint" -> "Integer";
            case "decimal" -> "java.math.BigDecimal";
            case "timestamp", "datetime" -> "java.time.LocalDateTime";
            case "date" -> "java.time.LocalDate";
            default -> "String";
        };
    }

    private String toCamelCase(String s, boolean firstUpper) {
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') {
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        if (firstUpper && sb.length() > 0) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }
        return sb.toString();
    }
}
