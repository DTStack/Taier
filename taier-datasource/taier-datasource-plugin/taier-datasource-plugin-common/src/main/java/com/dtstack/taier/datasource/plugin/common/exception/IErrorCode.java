package com.dtstack.taier.datasource.plugin.common.exception;

/**
 * 错误码枚举抽象接口
 *
 * @author ：wangchuan
 * date：Created in 上午10:22 2020/11/6
 * company: www.dtstack.com
 */
public interface IErrorCode {

    /**
     * 获取错误码
     * @return 错误码
     */
    Integer getCode ();

    /**
     * 获取错误描述
     * @return 错误描述
     */
    String getDesc ();

}
