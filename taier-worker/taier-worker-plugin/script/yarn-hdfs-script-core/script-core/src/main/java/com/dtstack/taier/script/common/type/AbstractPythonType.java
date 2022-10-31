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
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;

import java.util.List;

public abstract class AbstractPythonType extends AbstractAppType {

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }

    @Override
    public String buildCmd(ScriptConfiguration dtconf) {
        if (StringUtils.isNotBlank(dtconf.get(ScriptConfiguration.SCRIPT_LAUNCH_CMD))) {
            return StringUtils.replaceOnce(dtconf.get(ScriptConfiguration.SCRIPT_LAUNCH_CMD), "python", cmdPrefix(dtconf));
        }
        String cmdOpts = "";
        if (StringUtils.isNotBlank(dtconf.get(ScriptConfiguration.SCRIPT_CMD_OPTS))) {
            try {
                cmdOpts = dtconf.get(ScriptConfiguration.SCRIPT_CMD_OPTS);
                if (dtconf.getBoolean(ScriptConfiguration.SCRIPT_LOCALFILE,false)){
                    cmdOpts += " " + dtconf.get(ScriptConfiguration.SCRIPT_RUNNING_APPLICATIONID);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        String execFile = new Path(dtconf.get(ScriptConfiguration.SCRIPT_FILES)).getName();
        return cmdPrefix(dtconf) + " " + execFile + " " + cmdOpts;
    }
}
