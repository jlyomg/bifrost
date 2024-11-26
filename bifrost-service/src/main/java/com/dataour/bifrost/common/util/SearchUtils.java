package com.dataour.bifrost.common.util;

import com.github.pagehelper.PageInfo;
import com.dataour.bifrost.common.module.Page;
import com.dataour.bifrost.common.module.request.search.BaseSearchReq;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.entity.Example;

import static com.dataour.bifrost.common.util.ConvertUtils.trans2TargetList;
import static com.dataour.bifrost.common.util.ParamUtils.concatLikeParam;

/**
 * 搜索工具类
 */
@Slf4j
@Data
public class SearchUtils {
    public static final String FIELD_GMTCREATE = "gmtCreate";

    public static Example.Criteria handleCommonFields(Example example, BaseSearchReq searchParams, String KeywordFieldName) {
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(searchParams.getKeywords())) {
            criteria.andLike(KeywordFieldName, concatLikeParam(searchParams.getKeywords()));
        }
        if (searchParams.getStartTime() != null) {
            criteria.andGreaterThanOrEqualTo(FIELD_GMTCREATE, searchParams.getStartTime());
        }
        if (searchParams.getEndTime() != null) {
            criteria.andLessThanOrEqualTo(FIELD_GMTCREATE, searchParams.getEndTime());
        }
        String year = searchParams.getYear();
        if (year != null) {
            if (year.contains("-")) {
                String[] strings = year.split("-");
                criteria.andEqualTo("year", Integer.valueOf(strings[0]));
                criteria.andEqualTo("week", Integer.valueOf(strings[1]));
            } else {
                criteria.andEqualTo("year", Integer.valueOf(year));
            }
        }
        String week = searchParams.getWeek();
        if (week != null) {
            criteria.andEqualTo("week", Integer.valueOf(week));
        }
        // 处理排序逻辑
        if (StringUtils.isNoneBlank(searchParams.getSortedBy())) {
            example.setOrderByClause(searchParams.getSortedBy() + " " + searchParams.getSortedType());
        }
        // 解决mybatis带了criteria参数is_delete拼接多一个and的问题
        criteria.andCondition("1=1");
        return criteria;
    }

    /**
     * @param pageInfo
     * @return
     */
    public static Page<Object> trans2Page(PageInfo<Object> pageInfo) {
        Page<Object> resp = new Page<>();
        resp.setTotalPages(pageInfo.getPages());
        resp.setTotalElements(pageInfo.getTotal());
        resp.setNumberOfElements(pageInfo.getPageSize());
        resp.setNumber(pageInfo.getPageNum() - 1);
        resp.setSize(pageInfo.getPageSize());
        resp.setContent(pageInfo.getList());
        return resp;
    }

    public static PageInfo transDatas(PageInfo pageInfo, Class<?> targetClazz) {
        pageInfo.setList(trans2TargetList(pageInfo.getList(), targetClazz));
        return pageInfo;
    }
}
