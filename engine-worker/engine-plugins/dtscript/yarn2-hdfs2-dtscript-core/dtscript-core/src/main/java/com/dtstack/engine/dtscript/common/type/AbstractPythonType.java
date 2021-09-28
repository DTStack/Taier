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

package com.dtstack.engine.dtscript.common.type;

import com.dtstack.engine.dtscript.client.ClientArguments;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.net.URLEncoder;
import java.util.List;

public abstract class AbstractPythonType extends AbstractAppType {

    @Override
    public void env(List<String> envList) {
        super.env(envList);
    }

    @Override
    public String buildCmd(ClientArguments clientArguments, YarnConfiguration conf) {
        if (StringUtils.isNotBlank(clientArguments.getLaunchCmd())) {
            return StringUtils.replaceOnce(clientArguments.getLaunchCmd(), "python", cmdPrefix(conf));
        }

        String cmdOpts = "";
        if (StringUtils.isNotBlank(clientArguments.getCmdOpts())) {
            try {

                cmdOpts = clientArguments.getCmdOpts();
                if (clientArguments.getLocalFile()){
                    cmdOpts += " " + clientArguments.getApplicationId();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String execFile =clientArguments.getFiles()[0];
        if (!clientArguments.getLocalFile()){
            String fullPath = clientArguments.getFiles()[0];
            String[] parts = fullPath.split("/");
            execFile = parts[parts.length - 1];
        }
        return cmdPrefix(conf) + " " + execFile + " " + cmdOpts;
    }
}
