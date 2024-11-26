package com.dataour.bifrost.common.enums;

/**
 * 注解鉴权的验证模式
 *
 * @Author JASON
 */
public enum SaMode {
    /**
     * 必须具有所有的元素
     */
    AND,
    /**
     * 只需具有其中一个元素
     */
    OR
}
