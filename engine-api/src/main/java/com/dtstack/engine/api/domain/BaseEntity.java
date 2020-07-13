package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.sql.Timestamp;

@ApiModel
public class BaseEntity implements Serializable {

    private Long id = 0L;

    /**
     * 创建时间
     */
    @ApiModelProperty(notes = "创建时间")
    private Timestamp gmtCreate;

    /**
     * 修改时间
     */
    @ApiModelProperty(notes = "修改时间")
    private Timestamp gmtModified;

    /**
     * 是否删除
     */
    @ApiModelProperty(notes = "是否删除")
    private Integer isDeleted = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}
