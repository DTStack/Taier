package com.dtstack.engine.common.enums;

/**
 * Reason:
 * Date: 2017/6/2
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public enum EScheduleType {

    //正常调度(0), 补数据(1),临时运行一次
    NORMAL_SCHEDULE(0, "正常调度"), FILL_DATA(1, "补数据"), TEMP_JOB(2, "临时运行");

    private int type;

    private String desc;


    EScheduleType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static String getTypeName(int type) {
        if (NORMAL_SCHEDULE.type == type) {
            return NORMAL_SCHEDULE.desc;
        } else if (FILL_DATA.type == type) {
            return FILL_DATA.desc;
        } else if (TEMP_JOB.type == type) {
            return TEMP_JOB.desc;
        } else {
            throw new UnsupportedOperationException("未知调度类型");
        }
    }
}
