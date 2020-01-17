package com.dtstack.engine.common.enums;

/**
 * 任务节点层级显示
 * 0 展开上下游, 1:展开上游 2:展开下游
 * Date: 2018/3/21
 * Company: www.dtstack.com
 * @author xuchao
 */
public enum DisplayDirect {

    FATHER_CHILD(0), FATHER(1), CHILD(2);

    Integer type = 0;

    DisplayDirect(Integer type){
        this.type = type;
    }

    public Integer getType(){
        return type;
    }
}
