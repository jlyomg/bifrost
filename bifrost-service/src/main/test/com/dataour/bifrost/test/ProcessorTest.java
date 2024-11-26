package com.dataour.bifrost.test;

import com.alibaba.fastjson2.JSONObject;
import com.dataour.bifrost.common.module.param.StorageInfo;
import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.processor.erp.ERPUserProcessor;
import com.dataour.bifrost.processor.wms.WMSStorageProcessor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author vincentbai
 * @Date 29/02/2024 11:11 am
 * @description Life is a journey, learn to enjoy the ride. ❤️
 */

public class ProcessorTest extends BaseTest {

    @Autowired
    private WMSStorageProcessor wmsStorageProcessor;
    @Autowired
    private ERPUserProcessor erpUserProcessor;

    @Test
    public void getStorageInfoByClientCode() {
        StorageInfo storageInfo = wmsStorageProcessor.getStorageInfoByClientCode("LRI");
        System.out.println(JSONObject.toJSONString(storageInfo));
    }

    @Test
    public void checekData() {
        Resp<Boolean> result = erpUserProcessor.checekData("LRI", "", "244999", "");
        System.out.println(JSONObject.toJSONString(result));
    }

}
