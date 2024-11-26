package com.dataour.bifrost.controller;

import com.dataour.bifrost.annotation.CheckPermission;
import com.dataour.bifrost.common.controller.BaseController;
import com.dataour.bifrost.common.module.AjaxResult;
import com.dataour.bifrost.common.module.request.BillCategoryAddReq;
import com.dataour.bifrost.common.module.request.BillCategoryUpdateReq;
import com.dataour.bifrost.common.module.request.search.bifrostCategorySearchReq;
import com.dataour.bifrost.common.module.response.BillCategoryResp;
import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.service.TenantService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Tenant
 *
 * @Author JASON
 * @Date 2023-12-14 10:10
 */
@Validated
@Controller
@RequestMapping("/tenant")
public class TenantController extends BaseController {

    @Autowired
    private TenantService tenantService;

    /**
     * 获取租户列表(搜索)
     *
     * @param
     * @return
     */
    @GetMapping("/list")
    @ResponseBody
    @CheckPermission("bifrost:system:tenant:list")
    public AjaxResult<PageInfo<BillCategoryResp>> list(bifrostCategorySearchReq searchParams) {
        Resp<PageInfo<BillCategoryResp>> resp = tenantService.search(searchParams);
        return AjaxResult.resp(resp);
    }

    /**
     * 查看租户信息
     *
     * @param id 业务id
     * @return
     */
    @GetMapping("/detail")
    @CheckPermission("bifrost:system:tenant:detail")
    @ResponseBody
    public AjaxResult<BillCategoryResp> detail(@NotNull Long id) {
        return AjaxResult.resp(tenantService.getDetail(id));
    }

    /**
     * 新建租户
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @CheckPermission("bifrost:system:tenant:add")
    @ResponseBody
    public AjaxResult<Boolean> add(@RequestBody @Valid BillCategoryAddReq param) {
        return AjaxResult.resp(tenantService.add(param));
    }

    /**
     * 修改租户
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @CheckPermission("bifrost:system:tenant:update")
    @ResponseBody
    public AjaxResult<Boolean> update(@RequestBody @Valid BillCategoryUpdateReq param) {
        return AjaxResult.resp(tenantService.update(param));
    }

//    /**
//     * 删除租户
//     *
//     * @param
//     * @return
//     */
//    @PostMapping("/delete")
//    @CheckPermission("Bill:Admin:bifrostCategories:Delete")
//    @ResponseBody
//    public AjaxResult<Boolean> delete(@RequestBody @Valid IdParam param) {
//        return AjaxResult.resp(tenantService.delete(param.getId()));
//    }
}
