package com.dtstack.engine.api.domain;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:43 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskParamTemplate {

    private Long id;

    private String params;

    /**
     * '计算类型 0实时，1 离线'
     */
    private Integer computeType;

    private Integer engineType;

    private Timestamp gmtModified;

    private Timestamp gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }
}
