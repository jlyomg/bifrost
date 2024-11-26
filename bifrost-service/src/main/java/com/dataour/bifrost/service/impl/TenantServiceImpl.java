package com.dataour.bifrost.service.impl;

import com.aizuda.common.toolkit.CollectionUtils;
import com.dataour.bifrost.common.enums.RespCodeEnums;
import com.dataour.bifrost.common.module.request.BillCategoryAddReq;
import com.dataour.bifrost.common.module.request.BillCategoryUpdateReq;
import com.dataour.bifrost.common.module.request.search.bifrostCategorySearchReq;
import com.dataour.bifrost.common.module.response.BillCategoryResp;
import com.dataour.bifrost.common.module.response.Resp;
import com.dataour.bifrost.common.util.BeanUtils;
import com.dataour.bifrost.common.util.SearchUtils;
import com.dataour.bifrost.common.util.StringUtils;
import com.dataour.bifrost.domain.TenantDO;
import com.dataour.bifrost.mapper.TenantDOMapper;
import com.dataour.bifrost.service.TenantService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static com.dataour.bifrost.common.util.SearchUtils.transDatas;

/**
 * 应用服务接口
 *
 * @Author JASON
 * @Date 2024-01-03 14:46
 */
@Slf4j
@Service
public class TenantServiceImpl implements TenantService {
    @Autowired
    private TenantDOMapper tenantDOMapper;

    @Override
    public Resp<PageInfo<BillCategoryResp>> search(bifrostCategorySearchReq params) {
        Example example = new Example(TenantDO.class);
        Example.Criteria criteria = SearchUtils.handleCommonFields(example, params, "name");
        String state = params.getState();
        if (StringUtils.isNotEmpty(state)) {
            criteria.andEqualTo("state", state);
        }
        PageHelper.startPage(params.getPage(), params.getPageSize());
        PageInfo<BillCategoryResp> pageInfo = transDatas(new PageInfo<>(tenantDOMapper.selectByExample(example)), BillCategoryResp.class);
        List<BillCategoryResp> billCategoryResps = pageInfo.getList();
        if (CollectionUtils.isEmpty(billCategoryResps)) {
            return Resp.success(pageInfo);
        }
        return Resp.success(pageInfo);
    }

    @Override
    public Resp<BillCategoryResp> getDetail(Long id) {
        return Resp.success(BeanUtils.copyValueWithSuper(tenantDOMapper.selectByPrimaryKey(id), new BillCategoryResp()));
    }

    @Override
    public Resp<Boolean> add(BillCategoryAddReq param) {
        try {
            return Resp.success(true);
        } catch (Exception e) {
            log.error("Add billCategory error", e);
        }
        return Resp.error(RespCodeEnums.BIZ_ERROR);
    }

    @Override
    public Resp<Boolean> update(BillCategoryUpdateReq param) {
        try {
            return Resp.success(true);
        } catch (Exception e) {
            log.error("Update billCategory error", e);
        }
        return Resp.error(RespCodeEnums.BIZ_ERROR);
    }

    @Override
    public Resp<Boolean> delete(Long id) {
        return Resp.success(true);
    }

}
