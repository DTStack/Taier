package com.dtstack.taier.common.enums;


import com.dtstack.taier.common.exception.RdosDefineException;

/**
 * metric 指标过滤 key
 *
 * @author ：wangchuan
 * date：Created in 下午5:49 2021/4/20
 * company: www.dtstack.com
 */
public enum EMetricTag {

    /**
     * job_id 全匹配
     */
    JOB_ID(1, "job_id", "=", "%s"),

    /**
     * exported_job 前缀匹配
     */
    EXPORTED_JOB_PREFIX(2, "exported_job", "=~", "%s.*");

    // 枚举唯一值
    private final Integer tagVal;

    // tag 名称
    private final String tagName;

    // 类型：=、=~、!=、!~
    private final String type;

    // value
    private final String filter;

    EMetricTag(Integer tagVal, String tagName, String type, String filter) {
        this.tagVal = tagVal;
        this.tagName = tagName;
        this.type = type;
        this.filter = filter;
    }

    public Integer getTagVal() {
        return tagVal;
    }

    public String getTagName() {
        return tagName;
    }

    public String getType() {
        return type;
    }

    public String getFilter() {
        return filter;
    }

    /**
     * 根据 tagVal 获取对应的枚举
     *
     * @param tagVal 枚举唯一值
     * @return EMetricTag
     */
    public static EMetricTag getByTagVal(Integer tagVal) {
        for (EMetricTag metricTag : values()) {
            if (metricTag.getTagVal().equals(tagVal)) {
                return metricTag;
            }
        }
        throw new RdosDefineException(String.format("no matching metricTag was found based on '%s'", tagVal));
    }
}
