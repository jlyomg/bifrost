package com.dataour.bifrost.common.enums;

/**
 * @Author JASON
 */
public enum ForbidStateEnums {
    NORMAL(0, "正常"),
    FORBID(1, "禁止"),
    ;

    private int code;

    private String name;

    ForbidStateEnums(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ForbidStateEnums getByCode(int code) {
        for (ForbidStateEnums statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
