package com.dtstack.taier.develop.service.template;


import java.util.List;

/**
 * Date: 2020/1/9
 * Company: www.dtstack.com
 * 前端传入的基本参数(sourceMap) 用于构建插件对象
 * @author xiaochen
 */
public class DaPluginParam {
    private Integer type;
    private Integer sourceId;
    private String extralConfig;
    protected List<Long> sourceIds;

    /**
     * 数据源展示名称
     */
    private String name;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getExtralConfig() {
        return extralConfig;
    }

    public void setExtralConfig(String extralConfig) {
        this.extralConfig = extralConfig;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }
}
