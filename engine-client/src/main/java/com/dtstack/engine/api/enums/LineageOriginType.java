package com.dtstack.engine.api.enums;

/**
 * @author chener
 * @Classname LineageOriginType
 * @Description 血缘来源
 * @Date 2020/11/4 15:30
 * @Created chener@dtstack.com
 */
public enum LineageOriginType {
    /**
     * sql解析
     */
    SQL_PARSE(0),

    /**
     * 手动维护
     */
    MANUAL_ADD(1),

    /**
     * json解析
     */
    JSON_PARSE(2),
    ;

    public int getType() {
        return type;
    }

    private int type;

    LineageOriginType(int type) {
        this.type = type;
    }

}
