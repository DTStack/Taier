package com.dtstack.taier.develop.enums.develop;


import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;

/**
 * 实时采集任务 操作类型
 *
 * @author sanyue
 * @date 2018/9/17
 */
public enum DAoperators {

    /**
     * 插入
     */
    insert(1),

    /**
     * 更新
     */
    update(2),

    /**
     * 删除
     */
    delete(3);

    private Integer val;

    DAoperators(Integer val) {
        this.val = val;
    }

    public Integer getVal() {
        return val;
    }

    public static DAoperators getByVal(Integer val){
        DAoperators[] values = DAoperators.values();
        for (DAoperators value : values) {
            if (value.getVal().equals(val)){
                return value;
            }
        }
        throw new RdosDefineException("实时采集任务操作类型选择错误", ErrorCode.INVALID_PARAMETERS);
    }
}
