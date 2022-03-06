package com.dtstack.taier.develop.enums.develop;

/**
 * @author huoyun
 * @date 2021/4/13 3:00 下午
 * @company: www.dtstack.com
 */
public enum CollectType {
    /**
     * 从任务运行时开始
     */
    ALL(0),

    /**
     * 按时间选择
     */
    TIME(1),

    /**
     * 按文件选择
     */
    FILE(2),

    /**
     * 用户手动输入
     */
    SCN(3),

    /**
     * 从begin点开始
     */
    BEGIN(4),

    /**
     * 从lsn开始
     */
    LSN(5);

    private Integer collectType;

    CollectType(Integer collectType){
        this.collectType = collectType;
    }

    public Integer getCollectType() {
        return collectType;
    }
}
