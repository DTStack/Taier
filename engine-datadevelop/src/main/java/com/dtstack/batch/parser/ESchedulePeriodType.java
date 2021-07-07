package com.dtstack.batch.parser;

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
    MONTH(4),

    /**
     * 自定义cron表达式
     */
    CRON(5);

    private int val;

    ESchedulePeriodType(int val){
        this.val = val;
    }

    public int getVal(){
        return this.val;
    }


}
