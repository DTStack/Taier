package com.dtstack.taier.develop.vo.develop.result.job;


/**
 * @author vainhope
 */
public class RenderCondition {

    private String key;
    private int value;
    private String renderKind;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setRenderKind(String renderKind) {
        this.renderKind = renderKind;
    }

    public String getRenderKind() {
        return renderKind;
    }

}