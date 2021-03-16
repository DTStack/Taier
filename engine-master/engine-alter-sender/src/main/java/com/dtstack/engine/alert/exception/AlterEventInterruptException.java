package com.dtstack.engine.alert.exception;

import com.dtstack.lang.exception.BizException;

/**
 * @Auther: dazhi
 * @Date: 2021/1/26 3:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterEventInterruptException extends BizException {
    private static final long serialVersionUID = -8457636506880929633L;

    public AlterEventInterruptException(String message) {
        super(message);
    }
}
