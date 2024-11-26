package com.dataour.bifrost.core.processor;

import com.github.pagehelper.PageInfo;
import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.common.enums.TemplateStateEnum;
import com.dataour.bifrost.common.module.request.search.ClientProfileSearchReq;
import com.dataour.bifrost.common.module.response.BillCategoryPriceGroupResp;
import com.dataour.bifrost.common.module.response.ClientProfileResp;
import com.dataour.bifrost.common.module.response.ProfileClientPriceResp;
import com.dataour.bifrost.common.util.BeanUtils;
import com.dataour.bifrost.core.context.BillGlobalContext;
import com.dataour.bifrost.core.module.ClientbifrostCategory;
import com.dataour.bifrost.service.ClientProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: JASON
 * @Date: 2024-01-11 10:38
 * @Description:
 */
@Slf4j
@Component
public class bifrostProcessor {

    @Autowired
    private ClientProfileService clientProfileService;

    /**
     * 加载用户收费项
     */
    public void loadClientProfiles() {
        Map<String, Map<String, ClientbifrostCategory>> clientbifrostCategoryMap = new HashMap<>();
        ClientProfileSearchReq clientProfileSearchReq = new ClientProfileSearchReq();
        clientProfileSearchReq.setState(TemplateStateEnum.RUNNING.getCode());
        clientProfileSearchReq.setIfEffective(true);
        clientProfileSearchReq.setPageSize(Integer.MAX_VALUE);
        Resp<PageInfo<ClientProfileResp>> result = clientProfileService.search(clientProfileSearchReq);
        if (!result.isSuccess()) {
            log.warn("Search ClientProfile Error!");
            return;
        }
        PageInfo<ClientProfileResp> pageInfo = result.getData();
        List<ClientProfileResp> list = pageInfo.getList();
        if (pageInfo == null || CollectionUtils.isEmpty(list)) {
            log.warn("ClientProfile is empty!");
            return;
        }
        list.forEach(clientProfileResp -> {
            clientbifrostCategoryMap.put(clientProfileResp.getClientCode(), getClientbifrostCategory(clientProfileResp));
        });
        BillGlobalContext.clientbifrostCategoryMap = clientbifrostCategoryMap;
        log.info("Client Profiles and Categories load success, count:{}", clientbifrostCategoryMap.size());
    }

    private Map<String, ClientbifrostCategory> getClientbifrostCategory(ClientProfileResp clientProfileResp) {
        List<BillCategoryPriceGroupResp> profileClientPrices = clientProfileResp.getProfileClientPrices();
        if (CollectionUtils.isEmpty(profileClientPrices)) {
            log.warn("ClientProfile -> BillCategoryPriceGroupResp is empty!");
            return new HashMap<>(0);
        }
        String clientCode = clientProfileResp.getClientCode();
        Long clientProfileId = clientProfileResp.getId();
        Map<String, ClientbifrostCategory> map = new HashMap<>();
        profileClientPrices.forEach(billCategoryPriceGroupResp -> {
            List<ProfileClientPriceResp> billCategories = billCategoryPriceGroupResp.getBillCategories();
            if (!CollectionUtils.isEmpty(billCategories)) {
                billCategories.forEach(profileClientPriceResp -> {
                    ClientbifrostCategory clientbifrostCategory = BeanUtils.copyValueWithSuper(profileClientPriceResp, new ClientbifrostCategory());
                    clientbifrostCategory.setClientCode(clientCode);
                    clientbifrostCategory.setClientProfileId(clientProfileId);
                    map.put(profileClientPriceResp.getBillCategoryCode(), clientbifrostCategory);
                });
            }
        });
        return map;
    }
}
