package com.dtstack.engine.sql.flink;

/**
 * @author chener
 * @Classname ETableType
 * @Description TODO
 * @Date 2020/10/20 15:03
 * @Created chener@dtstack.com
 */
public enum ETableType {

    //源表
    SOURCE(1),
    //目的表
    SINK(2);

    int type;

    ETableType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
