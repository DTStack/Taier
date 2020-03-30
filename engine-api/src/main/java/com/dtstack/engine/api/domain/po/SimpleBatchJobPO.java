package com.dtstack.engine.api.domain.po;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/29
 */
public class SimpleBatchJobPO {

    private Long id;
    private String jobId;
    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
