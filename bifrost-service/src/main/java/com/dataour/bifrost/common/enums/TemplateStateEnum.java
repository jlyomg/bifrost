package com.dataour.bifrost.common.enums;

/**
 * @Author JASON
 */
public enum TemplateStateEnum {
    DRAFT("Draft", "草稿"),
    RUNNING("Running", "正式运行"),
    STOP("Stop", "禁止"),
    ;

    private String code;

    private String name;

    TemplateStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static TemplateStateEnum getByCode(String code) {
        for (TemplateStateEnum statusEnum : values()) {
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
