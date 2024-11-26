package com.dataour.bifrost.common.module.request.search;

import lombok.Data;

import java.util.Date;

@Data
public class BaseSearchReq {
    /**
     * 页码 默认为0
     */
    private int page = 0;
//    /**
//     * 页码 默认为1
//     */
//    private int currentPage = 1;
    /**
     * 每页数据大小 默认为10
     */
    private int pageSize = 10;
    /**
     * 搜索关键词 多个关键词用空格符隔开 示例："大数据 隐私保护"
     */
    private String keywords;
    /**
     * 开始时间 参数示例：2023-03-21 18:20:23
     */
    private Date startTime;
    /**
     * 结束时间 参数示例：2023-03-21 18:20:23
     */
    private Date endTime;
    /**
     * 排序字段
     */
    private String sortedBy;
    /**
     * 排序类型 asc-正序排序（默认） desc-倒序排序
     */
    private String sortedType;
    /**
     * 年
     */
    private String year;
    /**
     * 周
     */
    private String week;

    public int getPage() {
        return page + 1;
    }
}
