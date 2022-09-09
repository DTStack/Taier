package com.dtstack.taier.develop.vo.develop.result.job;

import java.util.List;

/**
 * @author vainhope
 */
public class ActionsCondition {

    private String key;
    private int value;
    private List<String> actions;

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

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<String> getActions() {
        return actions;
    }

}