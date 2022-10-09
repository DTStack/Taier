package com.dtstack.taier.datasource.api.dto.tsdb;

/**
 * suggest 类型
 *
 * @author ：wangchuan
 * date：Created in 上午10:20 2021/6/24
 * company: www.dtstack.com
 */
public enum Suggest {

    Metrics("metrics"),

    Field("field"),

    Tagk("tagk"),

    Tagv("tagv");

    private final String name;

    Suggest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
