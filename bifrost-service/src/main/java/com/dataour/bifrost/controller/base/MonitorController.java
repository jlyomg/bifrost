package com.dataour.bifrost.controller.base;

import com.dataour.bifrost.common.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
