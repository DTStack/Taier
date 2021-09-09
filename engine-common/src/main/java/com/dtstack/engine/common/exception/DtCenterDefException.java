package com.dtstack.engine.common.exception;

import com.dtstack.engine.common.lang.base.Strings;
import com.dtstack.engine.pluginapi.exception.ExceptionEnums;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 猫爸
 */
public class DtCenterDefException extends RuntimeException {
    private int code;


    public DtCenterDefException(ExceptionEnums exEnum) {
        super(exEnum.getDescription());
        code = exEnum.getCode();
    }

    public DtCenterDefException(ExceptionEnums exEnum, String message) {
        super(StringUtils.isNotBlank(message) ? message : exEnum.getDescription());
        code = exEnum.getCode();
    }

    public DtCenterDefException(ExceptionEnums exEnum, String message, Object... args) {
        super(StringUtils.isNotBlank(message) ? Strings.format(message, args) : exEnum.getDescription());
        code = exEnum.getCode();
    }

    public DtCenterDefException(Throwable throwable, ExceptionEnums exEnum) {
        super(exEnum.getDescription(), throwable);
        code = exEnum.getCode();
    }

    public DtCenterDefException(Throwable throwable, ExceptionEnums exEnum, String message) {
        super(StringUtils.isNotBlank(message) ? message : exEnum.getDescription(), throwable);
        code = exEnum.getCode();
    }

    public DtCenterDefException(Throwable throwable, ExceptionEnums exEnum, String message, Object... args) {
        super(StringUtils.isNotBlank(message) ? Strings.format(message, args) : exEnum.getDescription(), throwable);
        code = exEnum.getCode();
    }

    public int getCode() {
        return code;
    }
}
