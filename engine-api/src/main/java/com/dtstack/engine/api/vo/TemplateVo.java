package com.dtstack.engine.api.vo;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-11
 */
public class TemplateVo {
    /**
     * 前端界面展示 名称
     */
    private String key;
    /**
     * 前端界面展示 多选值
     */
    private List<TemplateVo> values;
    /**
     * 前端界面展示类型  0: 输入框 1:单选:
     */
    private String type;
    /**
     * 默认值
     */
    private String value;
    /**
     * 是否必填 默认必须
     */
    private Boolean required = true;

    private String dependencyKey;

    private String dependencyValue;

    public String getDependencyValue() {
        return dependencyValue;
    }

    public void setDependencyValue(String dependencyValue) {
        this.dependencyValue = dependencyValue;
    }

    public String getDependencyKey() {
        return dependencyKey;
    }

    public void setDependencyKey(String dependencyKey) {
        this.dependencyKey = dependencyKey;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<TemplateVo> getValues() {
        return values;
    }

    public void setValues(List<TemplateVo> values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
