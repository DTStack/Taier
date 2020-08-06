package com.dtstack.schedule.common.enums;

import com.dtstack.engine.common.exception.RdosDefineException;

/**
 * 表类型
 * Date: 2019/5/30
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public enum ETableType {

    HIVE(1, "Hive"),
    LIBRA(2, "LibrA"),
    IMPALA(3, "IMPALA"),
    TIDB(4,"TiDB"),
    PRESTO(5, "Presto");

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

        throw new RdosDefineException(String.format("not support ETableType:%d", type));
    }
}
