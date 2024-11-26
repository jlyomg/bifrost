package com.dataour.bifrost.datasource.common.util;

import com.dataour.bifrost.datasource.common.param.SqlParam;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dataour.bifrost.common.util.DataUtils.dataEmpty;

@Slf4j
public class SqlUtils {
    public static String getInSql(Set<Object> datas) {
        if (dataEmpty(datas)) {
            return " () ";
        }
        List params = new ArrayList(datas);
        Object v = params.get(0);
        boolean isStr = false;
        if (v instanceof String) {
            isStr = true;
        }
        StringBuilder sb = new StringBuilder(" (");
        int len = params.size();
        for (int i = 0; i < len; i++) {
            if (isStr) {
                sb.append("'");
                sb.append(params.get(i));
                sb.append("'");
            } else {
                sb.append(params.get(i));
            }
            if (i != len - 1) {
                sb.append(",");
            }
        }
        sb.append(") ");
        return sb.toString();
    }

    public static void closeResource(Connection conn, PreparedStatement stmt, ResultSet rs) {
        // 关闭资源
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("close ResultSet error", e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("close PreparedStatement error", e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("close Connection error", e);
            }
        }
    }

    public static void setPreparedStatement(PreparedStatement preStmt, SqlParam... params) throws SQLException {
        if (dataEmpty(params)) {
            return;
        }
        for (SqlParam param : params) {
            if (param.getDataType().equals("String")) {
                preStmt.setString(param.getIndex(), (String) param.getValue());
            } else if (param.getDataType().equals("Int")) {
                preStmt.setInt(param.getIndex(), (Integer) param.getValue());
            } else if (param.getDataType().equals("Long")) {
                preStmt.setLong(param.getIndex(), (Long) param.getValue());
            } else if (param.getDataType().equals("Double")) {
                preStmt.setDouble(param.getIndex(), (Double) param.getValue());
            } else if (param.getDataType().equals("Float")) {
                preStmt.setFloat(param.getIndex(), (Float) param.getValue());
            } else if (param.getDataType().equals("Boolean")) {
                preStmt.setBoolean(param.getIndex(), (Boolean) param.getValue());
            } else if (param.getDataType().equals("Date")) {
                preStmt.setDate(param.getIndex(), (java.sql.Date) param.getValue());
            } else if (param.getDataType().equals("Time")) {
                preStmt.setTime(param.getIndex(), (java.sql.Time) param.getValue());
            } else if (param.getDataType().equals("Timestamp")) {
                preStmt.setTimestamp(param.getIndex(), (java.sql.Timestamp) param.getValue());
            } else if (param.getDataType().equals("InputStream")) {
                preStmt.setBinaryStream(param.getIndex(), (java.io.InputStream) param.getValue());
            } else if (param.getDataType().equals("Object")) {
                preStmt.setObject(param.getIndex(), param.getValue());
            }
        }
    }

    /**
     * 验证SQL语句是否合法
     *
     * @param sqlQuery SQL查询语句
     * @return 如果SQL语句合法，则返回true；否则返回false
     */
    public static boolean validateSQL(String sqlQuery) {
        // 正则表达式模式用于匹配SELECT语句，不允许其他SQL操作
        String pattern = "(?i)\\bSELECT\\b.*\\bFROM\\b.*";
        Pattern selectPattern = Pattern.compile(pattern);

        // 使用正则表达式匹配SQL语句
        Matcher matcher = selectPattern.matcher(sqlQuery);
        if (!matcher.matches()) {
            return false; // 如果不是SELECT语句，则返回false
        }

        // 检查是否包含注入风险
        if (sqlQuery.contains(";") || sqlQuery.contains("--") || sqlQuery.contains("/*")) {
            return false; // 如果包含潜在的注入风险，则返回false
        }

        // 限制返回结果集大小
//        if (!sqlQuery.contains("LIMIT") && !sqlQuery.contains("limit")) {
//            // 如果未指定LIMIT，则添加LIMIT 1000
//            sqlQuery += " LIMIT 1000";
//        }
        return true; // SQL语句通过校验
    }
}
