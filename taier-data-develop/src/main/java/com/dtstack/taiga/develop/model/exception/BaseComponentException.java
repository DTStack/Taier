package com.dtstack.taiga.develop.model.exception;

import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.exception.RdosDefineException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class BaseComponentException extends RdosDefineException {

    private static final String MESSAGE_TEMPLATE = "Error on component of type %s. %s";
    private static final String MESSAGE_TEMPLATE_ZH = "组件 %s 错误 %s";

    private final EComponentType type;
    private final String msg;

    public BaseComponentException(EComponentType type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return String.format(MESSAGE_TEMPLATE_ZH, this.type, this.msg);
        } else {
            return String.format(MESSAGE_TEMPLATE, this.type, this.msg);
        }
    }

}
