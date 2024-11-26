package com.dataour.bifrost.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: JASON
 * @Date: 2023-3-20 11:30
 */
@Slf4j
public class CookieUtils {
    public static final String TOKEN_PARAM = "Authorization";
    public static final String SSO_AUTH_CODE_PARAM = "_auth_code_";
    /**
     * 单位:秒 cookie有效期1天
     */
    public static final int COOKIE_TOKEN_EXPIRE_TIME = 24 * 3600;
    /**
     * 单点登录AUTH_CODE过期时间30分钟
     */
    private static final Integer SSO_AUTH_CODE_EXPIRETIME = 1800;

    /**
     * 设置token
     */
    public static void setToken(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(TOKEN_PARAM, accessToken);
        cookie.setMaxAge(COOKIE_TOKEN_EXPIRE_TIME);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 返回name为"_td_token_"的cookie值
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
//        return getCookieValByName(request, TOKEN_PARAM);
//        return getCookieValByName(request, TOKEN_PARAM);
        if (request == null) {
            return null;
        }
        try {
            return request.getHeader(TOKEN_PARAM);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据cookie的name返回cookie
     *
     * @param request
     * @param name    cookiename
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Cookie c = null;
        if (request == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isBlank(name)) {
            return null;
        }
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName();
            if (name.equals(cookieName)) {
                c = cookie;
                break;
            }
        }
        return c;
    }

    /**
     * 根据cookiename返回cookieval
     *
     * @param request
     * @param name
     * @return
     */
    public static String getCookieValByName(HttpServletRequest request, String name) {
        String result = null;
        Cookie cookie = getCookieByName(request, name);
        if (cookie != null) {
            result = cookie.getValue();
        }
        return result;
    }

    /**
     * 删除name为"_td_token_"的cookie值
     *
     * @param response
     * @return
     */
    public static void deleteToken(HttpServletResponse response) {
        Cookie newCookie = new Cookie(TOKEN_PARAM, null);
        // 立即删除型
        newCookie.setMaxAge(0);
        // 项目所有目录均有效，这很关键，否则不敢保证删除
        newCookie.setPath("/");
        // 重新写入，将覆盖之前的
        response.addCookie(newCookie);
    }

    /**
     * 校验CSRF Token
     *
     * @param request
     * @return
     */
    public static boolean checkCSRFToken(HttpServletRequest request) {
        String csrfToken = request.getHeader("X-Cf-Random");
        String token = getToken(request);
        if (StringUtils.isBlank(token)) {
            return false;
        }
        if (StringUtils.isBlank(csrfToken) || csrfToken.length() != 36) {
            return false;
        }
        int index1 = Integer.valueOf(csrfToken.substring(0, 1));
        int index2 = Integer.valueOf(csrfToken.substring(1, 2));
        int index3 = Integer.valueOf(csrfToken.substring(34, 35));
        int index4 = Integer.valueOf(csrfToken.substring(35, 36));
        String md5 = MD5Utils.MD5(token.substring(index1, index1 + 1) + token.substring(index2, index2 + 1) + token.substring(index3, index3 + 1) + token.substring(index4, index4 + 1));
        if (md5.equalsIgnoreCase(csrfToken.substring(2, 34))) {
            return true;
        }
        return false;
    }

    /**
     * 新增cookie
     */
    public static void addAuthCode(HttpServletResponse response, String authCode) {
        Cookie newCookie = new Cookie(SSO_AUTH_CODE_PARAM, authCode);
        newCookie.setMaxAge(SSO_AUTH_CODE_EXPIRETIME);
        newCookie.setPath("/");
        response.addCookie(newCookie);
    }
}
