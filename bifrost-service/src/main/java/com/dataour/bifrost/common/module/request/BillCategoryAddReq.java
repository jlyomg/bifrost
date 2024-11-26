package com.dataour.bifrost.common.module.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

import static com.dataour.bifrost.common.constant.DateConstants.DATE_TIME_FORMATTER;

/**
 * 应用新增或修改参数
 */
@Data
public class BillCategoryAddReq {
    /**
     * 收费项编码
     */
    @NotNull
    private String code;

    /**
     * 收费项中文名称
     */
    private String name;

    /**
     * 收费项英文名称
     */
    private String enName;

    /**
     * 收费项类型：Normal，Section
     */
    @NotNull
    private String type;

    /**
     * 收费项分组
     */
    @NotNull
    private String group;

    /**
     * SAP分组
     */
    private String sapGroup;

    /**
     * 报表分组
     */
    private String reportGroup;

    /**
     * 生效日期
     */
    @NotNull
    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    private Date effectiveDate;

    /**
     * 标签
     */
    private String[] tag;

    /**
     * 单位：KG-重量千克，PCS-件数，GP，HQ
     */
    private String unit;

    /**
     * section_min
     */
    private String sectionMin;

    /**
     * section_max
     */
    private String sectionMax;

    /**
     * 标准价格
     */
    @NotNull
    private String standardPrice;

    /**
     * 成本
     */
    private String cost;

    /**
     * 描述
     */
    private String description;
}
