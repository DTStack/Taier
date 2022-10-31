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
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.List;
import java.util.Map;

public abstract class AbstractAppType {

    public static AbstractAppType fromString(String type) {
        if (StringUtils.isBlank(type)) {
            return new DummyType();
        } else if (type.equalsIgnoreCase(AppTypeEnum.SHELL.name())) {
            return new ShellType();
        } else if (type.equalsIgnoreCase(AppTypeEnum.PYTHON.name()) || type.equalsIgnoreCase(AppTypeEnum.PYTHON2.name())) {
            return new Python2Type();
        } else if (type.equalsIgnoreCase(AppTypeEnum.PYTHON3.name())) {
          return new Python3Type();
        }
        throw new IllegalArgumentException("Unsupported appType: " + type);
    }

    public String cmdPrefix(ScriptConfiguration config) {
        return "";
    }

    public String buildCmd(ScriptConfiguration dtconf) {
        if (StringUtils.isNotBlank(dtconf.get(ScriptConfiguration.SCRIPT_LAUNCH_CMD))) {
            return dtconf.get(ScriptConfiguration.SCRIPT_LAUNCH_CMD);
        } else {
            String fullPath = StringUtils.split(dtconf.get(ScriptConfiguration.SCRIPT_FILES),",")[0];
            String[] parts = fullPath.split("/");
            String encodedOpts = "";
            return cmdPrefix(dtconf) + " " + parts[parts.length - 1] + " " + encodedOpts;
        }
    }

    /**
     * 每个类型，可能对执行时的命令有额外的参数处理
     */
    public String cmdContainerExtra(String cmd, YarnConfiguration conf, Map<String, Object> containerInfo) {
        return cmd;
    }

    abstract public String name();

    public void env(List<String> envList) {
        envList.add("LANG=zh_CN.UTF-8");
        envList.add("CLASSPATH=" + "./:" + System.getenv("CLASSPATH"));
        envList.add("PATH=" + "./:" + System.getenv("PATH"));
        envList.add("PYTHONPATH=" + System.getenv("PYTHONPATH"));
    }

}