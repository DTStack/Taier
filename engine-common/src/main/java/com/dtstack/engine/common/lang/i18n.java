package com.dtstack.engine.common.lang;


import com.dtstack.engine.common.lang.base.Casts;

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
        String key = Casts.castString(code);
        if (resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        }
        return null;
    }
}
