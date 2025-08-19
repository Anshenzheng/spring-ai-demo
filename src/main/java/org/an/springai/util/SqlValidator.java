package org.an.springai.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用于对AI生成的SQL进行验证，最好使用一个专门只有读权限的FID来执行
 */
public class SqlValidator {
    // 允许的查询关键字（不区分大小写）
    private static final List<String> ALLOWED_QUERY_KEYWORDS = Arrays.asList(
            "SELECT", "WITH", "FROM", "WHERE", "GROUP", "BY", "HAVING", "ORDER", "LIMIT",
            "OFFSET", "JOIN", "INNER", "LEFT", "RIGHT", "OUTER", "AS", "AND", "OR", "NOT",
            "IN", "LIKE", "BETWEEN", "IS", "NULL", "TRUE", "FALSE", "DISTINCT", "COUNT",
            "SUM", "AVG", "MIN", "MAX", "CASE", "WHEN", "THEN", "ELSE", "END", "UNION",
            "ALL", "EXCEPT", "INTERSECT"
    );

    // 禁止的危险操作关键字
    private static final List<String> FORBIDDEN_OPERATIONS = Arrays.asList(
            // 数据操纵
            "INSERT", "UPDATE", "DELETE", "MERGE", "REPLACE",
            // 数据定义
            "CREATE", "ALTER", "DROP", "TRUNCATE", "COMMENT",
            // 事务控制
            "COMMIT", "ROLLBACK", "SAVEPOINT", "SET",
            // 其他危险操作
            "EXEC", "CALL", "PREPARE", "DEALLOCATE", "LOAD",
            "COPY", "HANDLER", "LOCK", "UNLOCK", "RENAME"
    );

    // 禁止的危险功能
    private static final List<String> FORBIDDEN_FUNCTIONS = Arrays.asList(
            "EXECUTE", "SYSTEM", "SHELL", "LOAD_FILE", "INTO_DUMPFILE", "INTO_OUTFILE"
    );

    // 用于检测注释的正则表达式
    private static final Pattern COMMENT_PATTERN = Pattern.compile(
            "--.*?$|/\\*.*?\\*/|#.*?$",
            Pattern.DOTALL | Pattern.MULTILINE
    );

    // 用于提取SQL关键字的正则表达式
    private static final Pattern KEYWORD_PATTERN = Pattern.compile("\\b\\w+\\b");

    /**
     * 验证SQL是否为安全的查询操作
     * @param sql 需要验证的SQL语句
     * @return 安全返回true，否则返回false
     */
    public static boolean isValidQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        // 1. 移除所有注释，防止通过注释绕过检测
        String cleanSql = removeComments(sql);

        // 2. 检查是否包含分号（防止多语句注入）
        if (containsMultipleStatements(cleanSql)) {
            System.out.println(1);
            return false;
        }

        // 3. 检查是否包含危险操作关键字
        if (containsForbiddenOperations(cleanSql)) {
            System.out.println(2);

            return false;
        }

        // 4. 检查是否包含危险函数
        if (containsForbiddenFunctions(cleanSql)) {
            System.out.println(3);

            return false;
        }

        // 5. 检查是否以合法的查询关键字开头
        if (!startsWithAllowedKeyword(cleanSql)) {
            System.out.println(4);

            return false;
        }

        // 6. 检查是否包含合法查询之外的关键字
        if (containsInvalidKeywords(cleanSql)) {
            System.out.println(5);

            return false;
        }

        return true;
    }

    /**
     * 移除SQL中的所有注释
     */
    private static String removeComments(String sql) {
        return COMMENT_PATTERN.matcher(sql).replaceAll(" ");
    }

    /**
     * 检查是否包含多个语句（分号）
     */
    private static boolean containsMultipleStatements(String sql) {
        // 允许字符串中的分号（如'abc;def'），但禁止语句分隔的分号
        // 这里使用简单判断，复杂场景需要更完善的解析
        String trimmed = sql.trim();
        if (trimmed.endsWith(";")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }
        return trimmed.contains(";");
    }

    /**
     * 检查是否包含禁止的操作关键字
     */
    private static boolean containsForbiddenOperations(String sql) {
        String upperSql = sql.toUpperCase();
        for (String op : FORBIDDEN_OPERATIONS) {
            // 使用单词边界确保精确匹配关键字
            if (Pattern.compile("\\b" + op + "\\b").matcher(upperSql).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否包含禁止的函数
     */
    private static boolean containsForbiddenFunctions(String sql) {
        String upperSql = sql.toUpperCase();
        for (String func : FORBIDDEN_FUNCTIONS) {
            // 函数通常以(结尾，如LOAD_FILE(
            if (upperSql.contains(func + "(")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否以允许的关键字开头
     */
    private static boolean startsWithAllowedKeyword(String sql) {
        String upperSql = sql.trim().toUpperCase();
        // 使用同一个Matcher实例进行查找和获取分组
        java.util.regex.Matcher matcher = KEYWORD_PATTERN.matcher(upperSql);

        // 先执行find()，如果找到匹配项再获取第一个分组
        if (matcher.find()) {
            String firstKeyword = matcher.group();
            return ALLOWED_QUERY_KEYWORDS.contains(firstKeyword);
        }

        // 没有找到任何关键字
        return false;
    }

    /**
     * 检查是否包含不允许的关键字
     */
    private static boolean containsInvalidKeywords(String sql) {
        String upperSql = sql.toUpperCase();
        java.util.regex.Matcher matcher = KEYWORD_PATTERN.matcher(upperSql);

        while (matcher.find()) {
            String token = matcher.group();
            // 忽略数字和空字符串
            if (token.matches("\\d+") || token.isEmpty()) {
                continue;
            }
            // 检查是否包含禁止的操作关键字（整词匹配）
            if (FORBIDDEN_OPERATIONS.stream().anyMatch(op ->
                    Pattern.compile("\\b" + op + "\\b").matcher(token).matches()
            )) {
                return true;
            }
            // 检查是否包含禁止的危险函数（函数通常以(结尾，如LOAD_FILE(）
            if (FORBIDDEN_FUNCTIONS.stream().anyMatch(func ->
                    token.startsWith(func + "(")
            )) {
                return true;
            }
        }
        // 没有检测到无效关键字
        return false;
    }
}
