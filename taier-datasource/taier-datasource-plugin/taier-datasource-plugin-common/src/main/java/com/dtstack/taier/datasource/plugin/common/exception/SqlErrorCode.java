package com.dtstack.taier.datasource.plugin.common.exception;

/**
 * sql执行时错误码和描述集合  TODO 预留，后期优化做
 *
 * @author ：wangchuan
 * date：Created in 上午10:21 2020/11/6
 * company: www.dtstack.com
 */
public enum SqlErrorCode implements IErrorCode {

    ;

    private Integer code;

    private String desc;

    SqlErrorCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
