package com.dataour.bifrost.test.erp;

import com.alibaba.fastjson2.JSONObject;
import com.dataour.bifrost.BifrostApplication;
import com.dataour.bifrost.common.module.param.OrderInfo;
import com.dataour.bifrost.common.module.param.POInfo;
import com.dataour.bifrost.common.module.param.ReturnOrderInfo;
import com.dataour.bifrost.common.util.DateUtils;
import com.dataour.bifrost.processor.erp.ERPOrderProcessor;
import com.dataour.bifrost.processor.erp.ERPPOProcessor;
import com.dataour.bifrost.processor.erp.ERPReturnProcessor;
import com.dataour.bifrost.test.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BifrostApplication.class)
public class ERPProcessorTest extends BaseTest {
    @Autowired
    private ERPPOProcessor erppoProcessor;
    @Autowired
    private ERPOrderProcessor erpOrderProcessor;
    @Autowired
    private ERPReturnProcessor erpReturnProcessor;

    @Test
    public void getPOListByClientCode() {
        DateUtils.TimeRange timeRange = new DateUtils.TimeRange();
        timeRange.setStartTime("2024-04-01 00:00:00");
        timeRange.setEndTime("2024-04-05 23:59:59");
        List<POInfo> datas = erppoProcessor.getPOListByClientCode("AAA", timeRange);
        System.out.println(datas.size());
    }

    @Test
    public void getOrderListByClientCode() {
        DateUtils.TimeRange timeRange = new DateUtils.TimeRange();
        timeRange.setStartTime("2024-04-01 00:00:00");
        timeRange.setEndTime("2024-04-05 23:59:59");
        List<OrderInfo> datas = erpOrderProcessor.getOrderListByClientCode("LRI", timeRange);
        System.out.println(datas.size());
    }

    @Test
    public void getReturnListByClientCode() {
        DateUtils.TimeRange timeRange = new DateUtils.TimeRange();
        timeRange.setStartTime("2024-04-01 00:00:00");
        timeRange.setEndTime("2024-04-05 23:59:59");
        List<ReturnOrderInfo> datas = erpReturnProcessor.getReturnListByClientCode("LRI", timeRange);
        System.out.println(datas.size());
    }

    @Test
    public void getPickupOrderListByClientCode() {
        DateUtils.TimeRange timeRange = new DateUtils.TimeRange();
        timeRange.setStartTime("2024-04-01 00:00:00");
        timeRange.setEndTime("2024-04-05 23:59:59");
        List<OrderInfo> datas = erpOrderProcessor.getPickupOrderListByClientCode("LRI", timeRange);
        System.out.println(JSONObject.toJSONString(datas));
    }
}
