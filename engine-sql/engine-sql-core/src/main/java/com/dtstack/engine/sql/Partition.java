package com.dtstack.engine.sql;

import org.apache.commons.math3.util.Pair;

import java.util.List;

/**
 * 分区描述类
 *
 * @author jaingbo
 */
public class Partition {

    /**
     * 分区字段
     */
    private List<Pair<String,String>> partKeyValues;

    /**
     * 分区路径
     */
    private String partLocalion;

    public List<Pair<String, String>> getPartKeyValues() {
        return partKeyValues;
    }

    public void setPartKeyValues(List<Pair<String, String>> partKeyValues) {
        this.partKeyValues = partKeyValues;
    }

    public String getPartLocalion() {
        return partLocalion;
    }

    public void setPartLocalion(String partLocalion) {
        this.partLocalion = partLocalion;
    }

    @Override
    public String toString() {
        return "Partition{" +
                "partKeyValues=" + partKeyValues +
                ", partLocalion='" + partLocalion + '\'' +
                '}';
    }
}
