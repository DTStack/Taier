package com.dtstack.taier.develop.vo.develop.result.job;

import java.util.List;

/**
 * @author vainhope
 */
public class TaskProperties {

    private RenderCondition renderCondition;
    private String renderKind;
    private List<String> formField;
    private ActionsCondition actionsCondition;
    private List<String> actions;
    private List<Integer> dataTypeCodes;
    private List<String> barItem;
    private BarItemCondition barItemCondition;

    public List<Integer> getDataTypeCodes() {
        return dataTypeCodes;
    }

    public void setDataTypeCodes(List<Integer> dataTypeCodes) {
        this.dataTypeCodes = dataTypeCodes;
    }

    public BarItemCondition getBarItemCondition() {
        return barItemCondition;
    }

    public void setBarItemCondition(BarItemCondition barItemCondition) {
        this.barItemCondition = barItemCondition;
    }

    public void setRenderCondition(RenderCondition renderCondition) {
        this.renderCondition = renderCondition;
    }

    public RenderCondition getRenderCondition() {
        return renderCondition;
    }

    public void setRenderKind(String renderKind) {
        this.renderKind = renderKind;
    }

    public String getRenderKind() {
        return renderKind;
    }

    public void setFormField(List<String> formField) {
        this.formField = formField;
    }

    public List<String> getFormField() {
        return formField;
    }

    public void setActionsCondition(ActionsCondition actionsCondition) {
        this.actionsCondition = actionsCondition;
    }

    public ActionsCondition getActionsCondition() {
        return actionsCondition;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setBarItem(List<String> barItem) {
        this.barItem = barItem;
    }

    public List<String> getBarItem() {
        return barItem;
    }

}