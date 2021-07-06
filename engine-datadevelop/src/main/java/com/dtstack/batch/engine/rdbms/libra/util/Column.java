package com.dtstack.batch.engine.rdbms.libra.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class Column {

    private String name;
    private String type;
    private String comment;
    private Integer index;
    private String alias;
    private String table;


    public Column(String name) {
        this.name = name;
    }

    public Column(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    public Column() {
    }

    public String getAlias() {
        return alias;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public static List<Column> getColumns(List<String> cols){
        List<Column> columns = Lists.newArrayList();
        int index = 0;
        for (String col : cols) {
            if(StringUtils.isNotEmpty(col)){
                columns.add(new Column(col,index++));
            }
        }
        return columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", comment='" + comment + '\'' +
                ", index=" + index +
                ", alias='" + alias + '\'' +
                ", table='" + table + '\'' +
                '}';
    }
}
