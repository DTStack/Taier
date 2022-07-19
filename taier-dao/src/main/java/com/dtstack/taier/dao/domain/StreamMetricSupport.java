package com.dtstack.taier.dao.domain;

/**
 * metric 指标及含义
 *
 * @author ：wangchuan
 * date：Created in 上午11:35 2021/4/16
 * company: www.dtstack.com
 */
public class StreamMetricSupport extends BaseEntity {

    // 指标中文名称
    private String name;

    // 指标支持的任务类型
    private String taskType;

    // 指标 key
    private String value;

    // 指标过滤的字段
    private Integer metricTag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getMetricTag() {
        return metricTag;
    }

    public void setMetricTag(Integer metricTag) {
        this.metricTag = metricTag;
    }
}
