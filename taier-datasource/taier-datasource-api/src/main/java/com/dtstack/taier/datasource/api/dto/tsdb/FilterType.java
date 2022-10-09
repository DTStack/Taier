package com.dtstack.taier.datasource.api.dto.tsdb;

/**
 * 过滤器类型
 *
 * @author ：wangchuan
 * date：Created in 上午10:20 2021/6/24
 * company: www.dtstack.com
 */
public enum FilterType {
    LiteralOr("literal_or"),

    NotLiteralOr("not_literal_or"),

    Wildcard("wildcard"),

    Regexp("regexp"),

    GeoIntersects("intersects");

    private final String name;

    FilterType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}

