package com.dtstack.taier.develop.vo.develop.result.job;

import java.util.List;

/**
 * @author yuebai
 * @date 2022/9/7
 */
public class BarItemCondition {

    private String key;
    private int value;
    private List<String> barItem;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<String> getBarItem() {
        return barItem;
    }

    public void setBarItem(List<String> barItem) {
        this.barItem = barItem;
    }
}
