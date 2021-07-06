package com.dtstack.batch.common.enums;

/**
 * <p>分区文件备份状态
 *
 * @author ：wangchuan
 * date：Created in 2:24 下午 2020/12/14
 * company: www.dtstack.com
 */
public enum EFileBakStatus {

    /**
     * 未删除
     */
    NORMAL(0),

    /**
     * 已经删除
     */
    DELETE(1);

    private final Integer val;

    public Integer getVal() {
        return val;
    }

    EFileBakStatus(Integer val) {
        this.val = val;
    }
}
