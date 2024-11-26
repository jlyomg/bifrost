package com.dataour.bifrost.common.enums;

/**
 * 账单项状态
 *
 * @Author JASON
 */
public enum DataTypeEnum {
    String("String", "String"),
    Float("Float", "Float"),
    Integer("Integer", "Integer"),
    Long("Long", "Long"),
    Double("Double", "Double"),
    Boolean("Boolean", "Boolean"),
    BigDecimal("BigDecimal", "BigDecimal"),
    ;

    private String code;

    private String name;

    DataTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DataTypeEnum getByCode(String code) {
        for (DataTypeEnum statusEnum : values()) {
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
