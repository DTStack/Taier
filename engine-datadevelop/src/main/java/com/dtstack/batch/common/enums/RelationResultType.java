package com.dtstack.batch.common.enums;

/**
 * @author yuebai
 * @date 2019-11-16
 */
public enum RelationResultType {
    /**
     * 是否只是查询
     */
    IS_QUERY(0),
    /**
     * 是否是结果
     */
    IS_RESULT(1);
    private int val;
    RelationResultType(int val){
        this.val = val;
    }
    public int getVal() {
        return val;
    }
}
