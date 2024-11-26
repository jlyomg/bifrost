package com.dataour.bifrost.common.module.auth;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckPermissionParam implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 需要校验的权限码
     */
    String[] permissionCodes;
    private String token;
}
