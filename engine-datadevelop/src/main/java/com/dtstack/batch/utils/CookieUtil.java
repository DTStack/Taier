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

package com.dtstack.batch.utils;


import javax.servlet.http.Cookie;

public class CookieUtil {


    public static final String DT_TOKEN = "dt_token";
    public static final String DT_USERID = "dt_user_id";
    public static final String DT_TENANT_ID = "dt_tenant_id";
    public static final String USER_FILED = "userId";
    public static final String TENANT_FILED = "tenantId";
    public static final String TOKEN = "dtToken";

    public static String getDtUicToken(Cookie[] cookies) {
        Object value = getCookieValue(cookies, DT_TOKEN);
        return value == null ? "" : value.toString();
    }


    public static long getUserId(Cookie[] cookies) {
        Object value = getCookieValue(cookies, DT_USERID);
        return value == null ? -1 : Long.parseLong(value.toString());
    }


    private static Object getCookieValue(Cookie[] cookies, String key) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
