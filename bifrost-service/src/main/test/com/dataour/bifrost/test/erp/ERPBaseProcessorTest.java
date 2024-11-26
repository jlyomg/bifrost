package com.dataour.bifrost.test.erp;

import com.dataour.bifrost.BifrostApplication;
import com.dataour.bifrost.common.module.param.ClientUserInfo;
import com.dataour.bifrost.processor.erp.ERPUserProcessor;
import com.dataour.bifrost.test.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BifrostApplication.class)
public class ERPBaseProcessorTest extends BaseTest {
    @Autowired
    private ERPUserProcessor erpUserProcessor;

    @Test
    public void getLoginClientUser() {
        List<ClientUserInfo> allClientUserInfos = erpUserProcessor.getAllClientUserInfos();
        System.out.println(allClientUserInfos.size());
    }
}
