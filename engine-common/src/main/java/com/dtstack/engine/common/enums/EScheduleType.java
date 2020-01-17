package com.dtstack.engine.common.enums;

/**
 * Reason:
 * Date: 2017/6/2
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum EScheduleType {

    //正常调度(0), 补数据(1)
    NORMAL_SCHEDULE(0), FILL_DATA(1);

    private int type;

    EScheduleType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static String getTypeName(int type){
        if(type == 0){
            return "正常调度";
        }else if(type == 1){
            return "补数据";
        }else{
            return "未知调度类型";
        }
    }
}
