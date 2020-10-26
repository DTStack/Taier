package com.dtstack.engine.sql.enums;

/**
 * @author chener
 * @Classname ETableType
 * @Description TODO
 * @Date 2020/7/27 14:12
 * @Created chener@dtstack.com
 */
public enum ETableType {
    HIVE(1, "Hive"),
    LIBRA(2, "LibrA"),
    TIDB(3, "TiDB"),
    ORACLE(4, "Oracle"),
    GREENPLUM(5, "GreenPlum"),
    IMPALA(6, "IMPALA");

    int type;
    String content;


    ETableType(int type, String content) {
        this.content = content;
        this.type = type;
    }

    public int getType() {
        return this.type;
    }


    public String getContent() {
        return content;
    }


    public static ETableType getTableType(int type) {
        for (ETableType etype : ETableType.values()) {
            if (etype.getType() == type) {
                return etype;
            }
        }

        throw new IllegalStateException(String.format("not support ETableType:%d", type));
    }
}
