package com.dataour.bifrost.service;

import com.dataour.bifrost.common.module.request.BillCategoryAddReq;
import com.dataour.bifrost.common.module.request.BillCategoryUpdateReq;
import com.dataour.bifrost.common.module.request.search.bifrostCategorySearchReq;
import com.dataour.bifrost.common.module.response.BillCategoryResp;
import com.dataour.bifrost.common.module.response.Resp;
import com.github.pagehelper.PageInfo;

/**
 * 收费项接口
 *
 * @Author JASON
 * @Date 2024-01-03 14:46
 */
public interface TenantService {
    Resp<PageInfo<BillCategoryResp>> search(bifrostCategorySearchReq searchParams);

    Resp<BillCategoryResp> getDetail(Long id);

    Resp<Boolean> add(BillCategoryAddReq params);

    Resp<Boolean> update(BillCategoryUpdateReq params);

    Resp<Boolean> delete(Long id);
}
