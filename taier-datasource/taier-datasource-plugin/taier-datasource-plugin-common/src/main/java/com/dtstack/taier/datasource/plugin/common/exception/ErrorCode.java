package com.dtstack.taier.datasource.plugin.common.exception;

/**
 * common-loader 错误码
 *
 * @author ：wangchuan
 * date：Created in 上午11:09 2021/3/23
 * company: www.dtstack.com
 */
public enum ErrorCode implements IErrorCode {

    /**
     * The method is not supported
     */
    NOT_SUPPORT(0, "This method is not supported by this data source...");

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    ErrorCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误描述
     */
    private final String desc;
}
