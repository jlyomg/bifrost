package com.dataour.bifrost.api.common.util;

import com.alibaba.fastjson.JSON;
import com.dataour.bifrost.annotation.CheckPermission;
import com.dataour.bifrost.common.module.auth.CheckPermissionParam;
import com.dataour.bifrost.common.module.response.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录、授权工具类
 *
 * @Author JASON
 * @Date 2023-04-03 10:10
 */
@Slf4j
public class AuthUtils {
    public static void print(HttpServletResponse response, String message, int httpCode) {
        PrintWriter writer = null;
        try {
            response.setStatus(httpCode);
            response.setHeader("Content-Type", "text/html;charset=UTF-8");
            writer = response.getWriter();
            writer.write(JSON.toJSONString(Resp.error(httpCode, message)));
            writer.flush();
        } catch (IOException e) {
            log.error("error", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static CheckPermissionParam getCheckPermissionParam(String token, Object handler) {
        CheckPermissionParam checkPermissionParam = new CheckPermissionParam();
        checkPermissionParam.setToken(token);
        // 仅校验方法权限
        if (handler instanceof HandlerMethod) {
            CheckPermission checkPermission = ((HandlerMethod) handler).getMethod().getAnnotation(CheckPermission.class);
            checkPermissionParam.setPermissionCodes(checkPermission.value());
        }
        return checkPermissionParam;
    }
}
