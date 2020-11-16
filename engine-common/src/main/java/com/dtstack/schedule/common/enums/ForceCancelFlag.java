package com.dtstack.schedule.common.enums;

/**
 *
 * @ProjectName engine-all
 * @ClassName ForceCancelFlag.java
 * @Description 强制操作标识
 * @author mowen
 * @createTime 2020年09月20日 16:01:00
 */
public enum ForceCancelFlag {
    YES(1), NO(0);

    private Integer flag;

    ForceCancelFlag(Integer flag){
        this.flag = flag;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
