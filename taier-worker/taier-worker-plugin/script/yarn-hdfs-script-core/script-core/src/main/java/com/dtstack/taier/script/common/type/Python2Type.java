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

package com.dtstack.taier.script.common.type;

import com.dtstack.taier.script.ScriptConfiguration;
import org.apache.commons.lang.StringUtils;


public class Python2Type extends AbstractPythonType {

    @Override
    public String cmdPrefix(ScriptConfiguration dtconf) {
        // 获取控制台配置的 python 路径
        String python = dtconf.get(ScriptConfiguration.SCRIPT_PYTHON2_PATH);
        return StringUtils.isNotBlank(python) ? python : "python";
    }

    @Override
    public String name() {
        return "PYTHON2";
    }
}