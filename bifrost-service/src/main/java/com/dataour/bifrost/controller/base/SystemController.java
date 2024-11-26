package com.dataour.bifrost.controller.base;

import com.dataour.bifrost.common.controller.BaseController;
import com.dataour.bifrost.common.module.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 系统相关接口
 *
 * @Author JASON
 * @Date 2023-12-15 10:10
 */
@Validated
@Controller
@RequestMapping()
public class SystemController extends BaseController {
    /**
     * 健康检查
     *
     * @param
     * @return
     */
    @GetMapping("/ping")
    @ResponseBody
    public AjaxResult ping() {
        return AjaxResult.success("OK");
    }
}
