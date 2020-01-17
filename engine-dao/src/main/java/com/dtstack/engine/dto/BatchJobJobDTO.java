package com.dtstack.engine.dto;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/19 9:44
 */
public class BatchJobJobDTO {

    private String jobKey;

    private Integer level;

    private List<BatchJobJobDTO> children;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public List<BatchJobJobDTO> getChildren() {
        return children;
    }

    public void setChildren(List<BatchJobJobDTO> children) {
        this.children = children;
    }
}
