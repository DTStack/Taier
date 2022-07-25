package com.dtstack.taier.develop.enums.develop;


import com.dtstack.taier.common.exception.RdosDefineException;

/**
 * Reason:
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum ESchedulePeriodType {

    /**
     * 分钟
     */
    MIN(0),

    /**
     * 小时
     */
    HOUR(1),

    /**
     * 天
     */
    DAY(2),

    /**
     * 周
     */
    WEEK(3),

    /**
     * 月
     */
    MONTH(4);

    private int val;

    ESchedulePeriodType(int val){
        this.val = val;
    }

    public int getVal(){
        return this.val;
    }


    public static ESchedulePeriodType getEnumByVal(Integer val) {
        for (ESchedulePeriodType periodType : ESchedulePeriodType.values()) {
            if (periodType.getVal() == val) {
                return periodType;
            }
        }
        throw new RdosDefineException(String.format("val：%s 没有匹配到对应的周期调度类型", val));
    }

}
