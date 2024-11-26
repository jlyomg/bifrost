package com.dataour.bifrost.common.module.request.search;

import lombok.Data;

@Data
public class TenantSearchReq extends BaseSearchReq {
    /**
     * 用户收费模版状态： Draft-草稿；Running-正式运行；Stop-已下线
     */
    private String state;
}
