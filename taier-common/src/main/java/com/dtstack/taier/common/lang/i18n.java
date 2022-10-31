/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.common.lang;


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
