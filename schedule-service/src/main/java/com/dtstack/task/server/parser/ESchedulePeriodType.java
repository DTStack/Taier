package com.dtstack.task.server.parser;

/**
 * Reason:
 * Date: 2017/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum ESchedulePeriodType {

    MIN(0), HOUR(1), DAY(2), WEEK(3), MONTH(4);

    private int val;

    ESchedulePeriodType(int val){
        this.val = val;
    }

    public int getVal(){
        return this.val;
    }


}
