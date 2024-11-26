package com.dataour.bifrost.controller.base;

import com.alibaba.fastjson2.JSONObject;
import com.dataour.bifrost.common.controller.BaseController;
import com.dataour.bifrost.common.module.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.dataour.bifrost.core.context.BillGlobalContext.clientbifrostCategoryMap;

/**
 * 系统监控接口
 *
 * @Author JASON
 * @Date 2023-12-15 10:10
 */
@Validated
@Controller
@RequestMapping("/monitor")
public class MonitorController extends BaseController {
    /**
     * 查看用户收费模版
     *
     * @param
     * @return
     */
    @GetMapping("/getClientbifrostCategory")
    @ResponseBody
    public AjaxResult<String> getClientbifrostCategory() {
        return AjaxResult.success(JSONObject.toJSONString(clientbifrostCategoryMap));
    }
}
