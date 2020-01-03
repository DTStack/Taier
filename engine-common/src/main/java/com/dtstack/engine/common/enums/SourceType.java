package com.dtstack.engine.common.enums;

/**
 * 数据来源类型
 * Date: 2018/8/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum SourceType {
    //周期调度
    CRON(0),
    //补数据
    FILL(1),
    //临时查询
    TEMP_QUERY(2);

    Integer type;
    SourceType(int type){
        this.type = type;
    }

    public Integer getType(){
        return type;
    }
}
