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

package com.dtstack.taier.datasource.plugin.saphana;

import com.dtstack.taier.datasource.plugin.common.exception.AbsErrorPattern;
import com.dtstack.taier.datasource.plugin.common.exception.ConnErrorCode;

import java.util.regex.Pattern;

/**
 * sap hana 错误适配器
 *
 * @author ：wangchuan
 * date：Created in 上午10:13 2021/12/30
 * company: www.dtstack.com
 */
public class SapHanaErrorPattern extends AbsErrorPattern {

    private static final Pattern USERNAME_PASSWORD_ERROR = Pattern.compile("(?i)Access\\s*denied\\s*for\\s*user.*using\\s*password");
    private static final Pattern DB_NOT_EXISTS = Pattern.compile("(?i)Access\\s*denied\\s*for\\s*user.*to\\s*database");
    private static final Pattern IP_PORT_FORMAT_ERROR = Pattern.compile("(?i)not\\s*received\\s*any\\s*packets");
    static {
        PATTERN_MAP.put(ConnErrorCode.USERNAME_PASSWORD_ERROR.getCode(), USERNAME_PASSWORD_ERROR);
        PATTERN_MAP.put(ConnErrorCode.DB_NOT_EXISTS.getCode(), DB_NOT_EXISTS);
        PATTERN_MAP.put(ConnErrorCode.IP_PORT_FORMAT_ERROR.getCode(), IP_PORT_FORMAT_ERROR);
    }
}
