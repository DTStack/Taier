package com.dtstack.batch.common.enums;

/**
 * <p>小文件合并状态
 *
 * @author ：wangchuan
 * date：Created in 2:24 下午 2020/12/14
 * company: www.dtstack.com
 */
public enum EFileMergeStatus {

    /**
     * 未开始 - 等待开始的状态
     */
    NOT_START(0, "未开始"),

    /**
     * 正在合并
     */
    MERGING(1, "正在合并"),

    /**
     * 合并成功
     */
    SUCCESS(2, "合并成功"),

    /**
     * 合并失败
     */
    FAIL(3, "合并失败"),

    /**
     * 合并取消
     */
    CANCEL(4, "合并取消"),

    /**
     * 合并取消中
     */
    CANCELING(5, "正在停止"),

    /**
     * 等待资源
     */
    WAIT_RESOURCE(8, "等待资源");

    /**
     * 对应的key
     */
    private final Integer val;

    /**
     * 描述
     */
    private String desc;

    public Integer getVal() {
        return val;
    }

    EFileMergeStatus(Integer val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
