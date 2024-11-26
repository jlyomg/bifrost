package com.dataour.bifrost.common.enums;

public enum SortedTypeEnum {
    ASC("asc", "asc"),
    DESC("desc", "desc"),
    ;

    private String code;

    private String name;

    SortedTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SortedTypeEnum getByCode(String code) {
        for (SortedTypeEnum statusEnum : values()) {
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
