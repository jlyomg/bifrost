package com.dataour.bifrost.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "bifrost_tenant")
public class TenantDO {
    /**
     * 主键id
     */
    @Id
    private Long id;

    /**
     * 租户ID
     */
    @Column(name = "tenant_id")
    private Integer tenantId;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 租户英文名称
     */
    @Column(name = "en_name")
    private String enName;

    /**
     * 扩展字段
     */
    private String attributes;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_update")
    private Date gmtUpdate;

    /**
     * 创建用户
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * 更新用户
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * 是否删除： 1是,0否
     */
    @Column(name = "is_deleted")
    private Byte isDeleted;

    /**
     * 描述
     */
    private String description;

    /**
     * 获取主键id
     *
     * @return id - 主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取租户ID
     *
     * @return tenant_id - 租户ID
     */
    public Integer getTenantId() {
        return tenantId;
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 获取租户名称
     *
     * @return name - 租户名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置租户名称
     *
     * @param name 租户名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取租户英文名称
     *
     * @return en_name - 租户英文名称
     */
    public String getEnName() {
        return enName;
    }

    /**
     * 设置租户英文名称
     *
     * @param enName 租户英文名称
     */
    public void setEnName(String enName) {
        this.enName = enName;
    }

    /**
     * 获取扩展字段
     *
     * @return attributes - 扩展字段
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * 设置扩展字段
     *
     * @param attributes 扩展字段
     */
    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    /**
     * 获取创建时间
     *
     * @return gmt_create - 创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置创建时间
     *
     * @param gmtCreate 创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 获取修改时间
     *
     * @return gmt_update - 修改时间
     */
    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    /**
     * 设置修改时间
     *
     * @param gmtUpdate 修改时间
     */
    public void setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
    }

    /**
     * 获取创建用户
     *
     * @return created_by - 创建用户
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置创建用户
     *
     * @param createdBy 创建用户
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取更新用户
     *
     * @return updated_by - 更新用户
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 设置更新用户
     *
     * @param updatedBy 更新用户
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * 获取是否删除： 1是,0否
     *
     * @return is_deleted - 是否删除： 1是,0否
     */
    public Byte getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置是否删除： 1是,0否
     *
     * @param isDeleted 是否删除： 1是,0否
     */
    public void setIsDeleted(Byte isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 获取描述
     *
     * @return description - 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }
}