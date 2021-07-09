package com.dtstack.engine.common.web.i18n;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author 猫爸
 */
public class i18n {
    private static ResourceBundle resourceBundle;

    static {
        Locale.setDefault(Locale.CHINESE);
        resourceBundle = ResourceBundle.getBundle("i18n/ErrorMessages");
    }

    public static String error(int code) {
        String key = castString(code, StringUtils.EMPTY);
        if (resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        }
        return null;
    }

    public static final String castString(Object object, String defaultValue) {
        return Objects.isNull(object) ? defaultValue : String.valueOf(object);
    }
}
