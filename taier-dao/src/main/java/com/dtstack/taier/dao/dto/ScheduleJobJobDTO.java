package com.dtstack.taier.dao.dto;

import java.util.List;

/**
 * @author jiangbo
 * @date 2018/7/19 9:44
 */
public class ScheduleJobJobDTO {

    private String jobKey;

    private Integer relyType;

    private Integer level;

    private List<ScheduleJobJobDTO> children;

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

    public List<ScheduleJobJobDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ScheduleJobJobDTO> children) {
        this.children = children;
    }

    public Integer getRelyType() {
        return relyType;
    }

    public void setRelyType(Integer relyType) {
        this.relyType = relyType;
    }
}
