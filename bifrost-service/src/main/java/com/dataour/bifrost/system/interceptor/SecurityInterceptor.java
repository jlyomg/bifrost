package com.dataour.bifrost.system.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一鉴权拦截器 校验token和权限
 *
 * @Author JLY
 */
@Slf4j
public class SecurityInterceptor implements HandlerInterceptor {

//    @Autowired
//    private ERPUserProcessor erpUserProcessor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        String token = CookieUtils.getToken(request);
//        if (StringUtils.isEmpty(token)) {
//            print(response, JWT_ERRCODE_NULL.getName(), HttpStatus.UNAUTHORIZED.value());
//            return false;
//        }
//        try {
//            Resp<Boolean> resp = erpUserProcessor.checkPermission(getCheckPermissionParam(token, handler));
//            if (!resp.isSuccess()) {
//                print(response, resp.getMessage(), resp.getCode());
//                return false;
//            }
//        } catch (Exception e) {
//            log.error("Rpc check permission error,token:{}", e, token);
//            print(response, REQ_UNAUTHORIZED.getName(), HttpStatus.UNAUTHORIZED.value());
//            return false;
//        }
        return true;
    }
}
