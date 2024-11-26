package com.dataour.bifrost.common.cache;

public class UserCache {
    /**
     * key 为用户clientCode，value 为租户Id
     */
    public static EvictingMap<String, String> clientCodeTenantCache = new EvictingMap<>(2000);
    /**
     * key 为用户clientCode，value 为租户Id
     */
    public static EvictingMap<String, String> tokenTenantCache = new EvictingMap<>(2000);
}
