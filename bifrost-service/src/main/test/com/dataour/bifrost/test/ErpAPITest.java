package com.dataour.bifrost.test;

import com.alibaba.fastjson2.JSONObject;
import com.dataour.bifrost.common.module.param.ClientUserInfo;
import com.dataour.bifrost.common.module.param.ClientWareHouseInfo;
import com.dataour.bifrost.common.util.DateUtils;
import com.dataour.bifrost.processor.erp.ERPUserProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class ErpAPITest extends BaseTest {
    @Autowired
    private ERPUserProcessor erpUserProcessor;

    @Test
    public void testGetPalletQtyFromHj() {
        List<ClientUserInfo> allClientUserInfos = erpUserProcessor.getAllClientUserInfos();
        System.out.println(allClientUserInfos.size());
    }

    @Test
    public void getClientUser() {
        ClientUserInfo allClientUserInfos = erpUserProcessor.getClientUser("LRI");
        System.out.println(allClientUserInfos.getUserId());
    }

    @Test
    public void getClientWareHouseInfo() {
        DateUtils.TimeRange lastDayTimeRange = DateUtils.getLastDayTimeRange(new Date());
        ClientWareHouseInfo data = erpUserProcessor.getClientWareHouseInfo("LRI", lastDayTimeRange);
        System.out.println(JSONObject.toJSONString(data));
    }
}
