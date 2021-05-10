package com.dtstack.engine.api.vo.lineage;

/**
 * 查询中的字段
 *
 * @author jiangbo
 * @date 2019/5/22
 */
public class SelectColumn {

    public static final String CONSTANT = "_CONSTANT_";

    /**
     * 字段名称，格式：tb.name
     */
    private String name;

    /**
     * 字段别名 as alias
     */
    private String alias;

    public SelectColumn() {
    }

    public SelectColumn(String name, String alias) {
        this.name = name;
        this.alias = alias == null ? name : alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public SelectColumn clone() {
        return new SelectColumn(name, alias);
    }
}
