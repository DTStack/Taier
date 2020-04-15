package com.dtstack.engine.api.vo;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
public class ChartMetaDataVO {
    private String name;
    private List<Object> data;

    public ChartMetaDataVO() {
    }

    public ChartMetaDataVO(String name, List<Object> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
