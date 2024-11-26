package com.dataour.bifrost.common.enums;

/**
 * 应用枚举
 */
public enum AppEnum {
    Bifrost("Bifrost", "Bifrost"),
    ;

    private String code;

    private String name;

    AppEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AppEnum getByCode(String code) {
        for (AppEnum statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
