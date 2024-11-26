package com.dataour.bifrost.test;

import com.dataour.bifrost.BifrostApplication;
import com.dataour.bifrost.domain.BillChargeDO;
import com.dataour.bifrost.mapper.BillChargeDOMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.dataour.bifrost.common.util.DataUtils.dataNotEmpty;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BifrostApplication.class)
public class MapperTest extends BaseTest {

    @Autowired
    private BillChargeDOMapper billChargeDOMapper;

    @org.junit.Test
    public void genOldBillCategory() {
        List<BillChargeDO> billChargeReqList = new ArrayList<>();
        billChargeReqList.add(null);
        billChargeReqList.add(null);
        // 移除列表中的 null 值
        billChargeReqList.removeIf(element -> element == null || dataNotEmpty(element.getBillCategoryCode()));
        billChargeDOMapper.insertList(billChargeReqList);
        System.out.println("success");
    }


}
