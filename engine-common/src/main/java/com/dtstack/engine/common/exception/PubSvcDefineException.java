package com.dtstack.engine.common.exception;


/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/15
 * @desc 公共服务模块定义的业务异常
 */
public class PubSvcDefineException extends BizException {

    public PubSvcDefineException(String message) {
        super(message);
    }

    public PubSvcDefineException(String message, Throwable cause) {
        super(message, cause);
    }

    public PubSvcDefineException(ExceptionEnums errorCode) {
        super(errorCode.getDescription());
    }

    public PubSvcDefineException(String message, ExceptionEnums errorCode) {
        super(message, errorCode);
    }

    public PubSvcDefineException(ExceptionEnums errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
    }

    public PubSvcDefineException(String message, ExceptionEnums errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
