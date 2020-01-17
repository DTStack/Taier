package com.dtstack.engine.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public enum TaskOperateType {

    RESOURCE(0),EDIT(1),CREATE(2),FROZEN(3),THAW(4),COMMIT(5);

    private int type;

    TaskOperateType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
