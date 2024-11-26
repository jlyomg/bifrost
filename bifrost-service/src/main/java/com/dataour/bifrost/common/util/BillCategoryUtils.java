package com.dataour.bifrost.common.util;


import com.aizuda.common.toolkit.CollectionUtils;
import com.dataour.bifrost.common.module.response.BillCategoryGroupResp;
import com.dataour.bifrost.common.module.response.BillCategorySimpleResp;
import com.dataour.bifrost.common.module.response.ProfileClientPriceResp;
import com.dataour.bifrost.domain.BillCategoryDO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.dataour.bifrost.common.util.ConvertUtils.toStringValue;

/**
 * 收费项工具类
 *
 * @Author JASON
 * @Date 2024-01-11 21:23
 */
@Slf4j
public class BillCategoryUtils {
    public static List<BillCategoryGroupResp> getBillCategoryGroupList(List<BillCategoryDO> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>();
        }
        // 按照group字段进行聚合
        Map<String, List<BillCategoryDO>> groupDataMap = dataList.stream()
                .collect(Collectors.groupingBy(BillCategoryDO::getGroup));
        List<BillCategoryGroupResp> entryList = new ArrayList<>(groupDataMap.size());
        // Map转List
        for (Map.Entry<String, List<BillCategoryDO>> entry : groupDataMap.entrySet()) {
            String key = entry.getKey();
            List<BillCategorySimpleResp> value = entry.getValue().stream().map(o -> {
                BillCategorySimpleResp billCategorySimpleResp = new BillCategorySimpleResp();
                BeanUtils.copyValue(o, billCategorySimpleResp);
                billCategorySimpleResp.setBillCategoryId(o.getId());
                billCategorySimpleResp.setBillCategoryCode(o.getCode());
                billCategorySimpleResp.setBillCategoryName(o.getName());
                billCategorySimpleResp.setBillCategoryEnName(o.getEnName());
                billCategorySimpleResp.setStandardPrice(o.getStandardPrice().toString());
                return billCategorySimpleResp;
            }).collect(Collectors.toList());
            BillCategoryGroupResp billCategoryGroupResp = new BillCategoryGroupResp();
            billCategoryGroupResp.setGroup(key);
            billCategoryGroupResp.setBillCategories(value);
            entryList.add(billCategoryGroupResp);
        }
        // 根据Group字段进行排序
        Collections.sort(entryList, new Comparator<BillCategoryGroupResp>() {
            @Override
            public int compare(BillCategoryGroupResp obj1, BillCategoryGroupResp obj2) {
                return obj1.getGroup().compareTo(obj2.getGroup());
            }
        });
        return entryList;
    }

    public static List<ProfileClientPriceResp> getProfileClientPriceList(Map<String, BigDecimal> billCategoryPriceMap, List<BillCategorySimpleResp> billCategorySimpleResps) {
        if (CollectionUtils.isEmpty(billCategorySimpleResps)) {
            return null;
        }
        return billCategorySimpleResps.stream().map(billCategorySimpleResp -> {
            ProfileClientPriceResp profileClientPriceResp = BeanUtils.copyValueWithSuper(billCategorySimpleResp, new ProfileClientPriceResp());
            profileClientPriceResp.setPrice(toStringValue(billCategoryPriceMap.get(billCategorySimpleResp.getBillCategoryCode())));
            return profileClientPriceResp;
        }).collect(Collectors.toList());
    }

}
