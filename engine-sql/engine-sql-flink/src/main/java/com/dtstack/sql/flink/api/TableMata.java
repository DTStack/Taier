package com.dtstack.sql.flink.api;

import java.util.Map;

/**
 * @author chener
 * @Classname TableMata
 * @Description
 * @Date 2020/10/21 17:15
 * @Created chener@dtstack.com
 */
public class TableMata {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getProp() {
        return prop;
    }

    public void setProp(Map prop) {
        this.prop = prop;
    }

    private String name;
    private Map prop;

    public TableMata(String name, Map prop){
        this.name = name;
        this.prop = prop;
    }
}
