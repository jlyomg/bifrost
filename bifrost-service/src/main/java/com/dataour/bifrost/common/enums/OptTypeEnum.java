package com.dataour.bifrost.common.enums;

/**
 * 操作类型
 *
 * @Author JASON
 */
public enum OptTypeEnum {
    System("System", "System"), Manual("Manual", "Manual"),
    ;

    private String code;

    private String name;

    OptTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static OptTypeEnum getByCode(String code) {
        for (OptTypeEnum statusEnum : values()) {
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
