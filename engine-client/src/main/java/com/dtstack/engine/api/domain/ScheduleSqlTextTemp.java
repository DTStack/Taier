package com.dtstack.engine.api.domain;

import java.util.Date;

/**
 * @Author: ZYD
 * Date: 2021/4/23 10:21
 * Description: 临时运行job sql文本关联对象
 * @since 1.0.0
 */
public class ScheduleSqlTextTemp {


    private Long id;

    private Long jobId;

    private String sqlText;

    private Date gmtCreate;

    private Date gmtModified;

    private Boolean isDeleted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
