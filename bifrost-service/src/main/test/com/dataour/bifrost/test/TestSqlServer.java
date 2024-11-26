package com.dataour.bifrost.test;

import com.dataour.bifrost.calculate.category.job.OrderProcessingTask;
import com.dataour.bifrost.datasource.ERPMySQLDataSource;
import com.dataour.bifrost.datasource.common.param.SqlResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author vincentbai
 * @Date 29/02/2024 11:11 am
 * @description Life is a journey, learn to enjoy the ride. ❤️
 */

public class TestSqlServer extends BaseTest {

    @Autowired
    private ERPMySQLDataSource erpMySQLDataSource;
    @Autowired
    private OrderProcessingTask orderProcessingTask;

    @Test
    public void testGetPalletQtyFromHj() {
        String sql = "SELECT sku_code as sku,length,width,height FROM skyer_goods.sgoo_sku WHERE sku_code ='2XT4471'";
        for (int i = 0; i < 1000; i++) {
            SqlResult sqlResult = erpMySQLDataSource.excuteSql(sql, null);
            while (sqlResult.next()) {
                System.out.println(sqlResult.getCurrentValue().toJSONString());
                System.out.println(i + 1);
            }
        }
    }

    @Test
    public void test2() {
        orderProcessingTask.execute();
    }
}
