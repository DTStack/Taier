package com.dtstack.engine.common.login.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @program: DAGScheduleX
 * @description:
 * @create: 2021-12-16 00:02
 **/
public class LicenseProductComponentWidget implements Serializable {
    private String field;

    private String fieldType;

    private Object defaultValue;

    private Object value;

    private Integer widgetType;

    private String proofImpl;

    private String effectApi;

    private List<LicenseProductComponentWidget> children;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(Integer widgetType) {
        this.widgetType = widgetType;
    }

    public String getProofImpl() {
        return proofImpl;
    }

    public void setProofImpl(String proofImpl) {
        this.proofImpl = proofImpl;
    }

    public String getEffectApi() {
        return effectApi;
    }

    public void setEffectApi(String effectApi) {
        this.effectApi = effectApi;
    }

    public List<LicenseProductComponentWidget> getChildren() {
        return children;
    }

    public void setChildren(List<LicenseProductComponentWidget> children) {
        this.children = children;
    }
}
