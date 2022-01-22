package com.dtstack.taiga.common.lang;


import java.util.Locale;
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
        String key = String.valueOf(code);
        if (resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        }
        return null;
    }
}
