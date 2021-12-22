package com.dtstack.engine.domain;


import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@TableName("schedule_fill_data_job")
public class ScheduleFillDataJob implements Serializable {

    /**
     * 补数据id
     */
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 补数据名称
     */
    private String jobName;

    /**
     * 生成时间
     */
    private String runDay;

    /**
     * 补数据开始运行时间
     */
    private String fromDay;

    /**
     * 补数据结束时间
     */
    private String toDay;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 最新修改时间
     */
    private Timestamp gmtModified;

    /**
     * 发起操作的用户
     */
    private Long createUserId;

    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;

    /**
     * 最大并行数
     */
    private Integer maxParallelNum;

    /**
     * 补数据上下文
     */
    private String fillDataInfo;

    /**
     * 补数据状态
     */
    private Integer fillGenerateStatus;

    /**
     * 当前并发运行实例数
     */
    private Integer numberParallelNum;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getRunDay() {
        return runDay;
    }

    public void setRunDay(String runDay) {
        this.runDay = runDay;
    }

    public String getFromDay() {
        return fromDay;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
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

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getMaxParallelNum() {
        return maxParallelNum;
    }

    public void setMaxParallelNum(Integer maxParallelNum) {
        this.maxParallelNum = maxParallelNum;
    }

    public String getFillDataInfo() {
        return fillDataInfo;
    }

    public void setFillDataInfo(String fillDataInfo) {
        this.fillDataInfo = fillDataInfo;
    }

    public Integer getFillGenerateStatus() {
        return fillGenerateStatus;
    }

    public void setFillGenerateStatus(Integer fillGenerateStatus) {
        this.fillGenerateStatus = fillGenerateStatus;
    }

    public Integer getNumberParallelNum() {
        return numberParallelNum;
    }

    public void setNumberParallelNum(Integer numberParallelNum) {
        this.numberParallelNum = numberParallelNum;
    }
}
