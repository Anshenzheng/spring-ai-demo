package org.an.springai.service;

import org.an.springai.exception.UnsafeSqlException;
import org.an.springai.util.SqlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SafeSqlExecutor {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public SafeSqlExecutor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> executeSafeQuery(String sql) throws UnsafeSqlException {
        System.out.println("validation is in progress ...");
        if (!SqlValidator.isValidQuery(sql)) {
            System.out.println("sql does not pass the validation: " + sql);
            throw new UnsafeSqlException("Query contains potentially dangerous operations");
        }
        System.out.println("sql passed the validation: " + sql);

        // 限制查询返回行数
        String limitedSql = applyQueryLimits(sql);

        return jdbcTemplate.queryForList(limitedSql);
    }

    private String applyQueryLimits(String sql) {
        // 如果SQL没有LIMIT子句，自动添加限制
        if (!sql.matches("(?i).*\\bLIMIT\\b.*")) {
            if (sql.contains(";")) {
                sql = sql.replace(";", " LIMIT 1000;");
            } else {
                sql += " LIMIT 1000";
            }
        }
        return sql;
    }
}
