package com.dataour.bifrost.common.util;

import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.common.module.param.ClientUserInfo;
import com.dataour.bifrost.common.module.response.BillCategoryResp;
import com.dataour.bifrost.common.module.response.SelectResp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dataour.bifrost.common.util.DataUtils.dataEmpty;

public class SelectUtils {
    public static List<SelectResp> clientInfo2Select(List<ClientUserInfo> datas) {
        if (dataEmpty(datas)) {
            return new ArrayList<>();
        }
        return datas.stream().map(clientUserInfo -> {
            SelectResp selectResp = new SelectResp();
            selectResp.setLabel(clientUserInfo.getCompanyName());
            selectResp.setValue(clientUserInfo.getClientCode());
            return selectResp;
        }).collect(Collectors.toList());
    }

    public static List<SelectResp> billCategory2Select(Resp<List<BillCategoryResp>> dataResp) {
        if (dataResp == null || !dataResp.isSuccess()) {
            return new ArrayList<>();
        }
        List<BillCategoryResp> datas = dataResp.getData();
        if (dataEmpty(datas)) {
            return new ArrayList<>();
        }
        return datas.stream().map(data -> {
            SelectResp selectResp = new SelectResp();
            selectResp.setLabel(data.getName());
            selectResp.setValue(data.getCode());
            return selectResp;
        }).collect(Collectors.toList());
    }
}
