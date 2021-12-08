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

package com.dtstack.engine.dtscript.util;

import com.dtstack.engine.dtscript.api.DtYarnConstants;

import java.util.Map;

/**
 * @program: engine-all
 * @author: wuren
 * @create: 2021/02/25
 **/
public class KrbUtils {

    private static final String PYTHON_TYPE = "python";

    public static boolean hasKrb(Map<String, String> env) {
        String principal = env.get(DtYarnConstants.ENV_PRINCIPAL);
        return principal != null;
    }

    public static boolean isPythonType(String appType) {
        if (null == appType) {
            return false;
        } else {
            return appType.toLowerCase().startsWith(PYTHON_TYPE);
        }
    }
}
