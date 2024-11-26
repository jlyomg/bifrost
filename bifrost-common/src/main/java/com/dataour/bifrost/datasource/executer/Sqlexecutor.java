package com.dataour.bifrost.datasource.executer;

import com.alibaba.fastjson2.JSONObject;
import com.dataour.bifrost.datasource.common.param.SqlParam;
import com.dataour.bifrost.datasource.common.param.SqlResult;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.dataour.bifrost.datasource.common.util.SqlUtils.closeResource;
import static com.dataour.bifrost.datasource.common.util.SqlUtils.setPreparedStatement;

@Slf4j
public abstract class Sqlexecutor {
    public abstract SqlResult excuteSql(String sql, SqlParam... params);

    public SqlResult excute(DataSource dataSource, String sql, SqlParam... params) {
        SqlResult result = new SqlResult();
        Connection connection = null;
        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            preStmt = connection.prepareStatement(sql);
            setPreparedStatement(preStmt, params);
            log.info("Excute sql:{}", sql);
            log.info("Excute params:{}", JSONObject.toJSONString(params));
            resultSet = preStmt.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    // 从结果集中提取数据放入Map中
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    obj.put(columnName, resultSet.getObject(i));
                }
                result.addResult(obj);
            }
        } catch (SQLException e) {
            log.error("Excute sql error, SQL:{}, params:{}", e, sql, JSONObject.toJSONString(params));
        } finally {
            // Close the resources Important！非常重要！数据库资源一定要关闭！！！
            closeResource(connection, preStmt, resultSet);
        }
        return result;
    }
}
