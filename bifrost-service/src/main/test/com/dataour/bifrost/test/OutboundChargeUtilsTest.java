package com.dataour.bifrost.test;

import com.dataour.bifrost.calculate.common.util.OutboundChargeUtils;
import com.dataour.bifrost.common.module.param.OrderInfo;
import com.dataour.bifrost.common.module.param.ProductInfo;
import com.dataour.bifrost.domain.BillChargeDO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class OutboundChargeUtilsTest {

    private OrderInfo orderInfo;
    private List<BillChargeDO> billChargeReqList;
    private BillChargeDO billChargeDO;

    @BeforeEach
    void setUp() {
        orderInfo = Mockito.mock(OrderInfo.class);
        billChargeReqList = new ArrayList<>();
        billChargeDO = Mockito.mock(BillChargeDO.class);
    }

    @Test
    void testOrderProcessingCharge() {
        // 设置orderInfo的返回值
        when(orderInfo.getLabourTime()).thenReturn(3); // 假设工作时间为3天
        when(orderInfo.getProductInfoList()).thenReturn(getProductInfoListWithWeight(10.5f, 20.5f)); // 假设产品总重量为31公斤

        // 设置billChargeDO的返回值
        when(billChargeDO.getAttributes()).thenReturn("{}"); // 假设初始属性为空
        when(billChargeDO.getAttributes()).thenReturn("{}"); // 假设更新后的属性为空
        when(billChargeDO.getAttributes()).thenReturn("{\"sku\": \"SKU123\"}"); // 假设更新后的属性包含SKU信息

        // 调用待测试的方法
        OutboundChargeUtils.itemPickCharge(orderInfo, billChargeReqList);

        // 验证billChargeDO是否被正确添加到列表中
        verify(billChargeReqList, times(1)).add(any(BillChargeDO.class));

        // 验证交互行为
        verify(orderInfo, times(1)).getLabourTime();
        verify(orderInfo, times(1)).getProductInfoList();
        verify(billChargeDO, atLeastOnce()).getAttributes();
    }

    private List<ProductInfo> getProductInfoListWithWeight(float... weights) {
        List<ProductInfo> productList = new ArrayList<>();
        for (float weight : weights) {
            ProductInfo productInfo = Mockito.mock(ProductInfo.class);
            productInfo.setProductHeight(weight);
            productInfo.setSku("SKU" + (productList.size() + 1));
            productList.add(productInfo);
        }
        return productList;
    }
}

