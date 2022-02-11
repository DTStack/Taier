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

package com.dtstack.taiga.local.test;

import com.dtstack.taiga.pluginapi.pojo.ParamAction;
import com.dtstack.taiga.pluginapi.JobClient;
import com.dtstack.taiga.pluginapi.JobIdentifier;
import com.dtstack.taiga.pluginapi.callback.CallBack;
import com.dtstack.taiga.pluginapi.callback.ClassLoaderCallBackMethod;
import com.dtstack.taiga.pluginapi.client.IClient;
import com.dtstack.taiga.pluginapi.pojo.JobResult;
import com.dtstack.taiga.pluginapi.util.MD5Util;
import com.dtstack.taiga.pluginapi.util.PublicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String SP = File.separator;

    public static void main(String[] args) throws Exception {

        System.setProperty("HADOOP_USER_NAME", "admin");

        // job json path
        String jobJsonPath = USER_DIR + SP + "local-test/src/main/json/dtscript-agent.json";

        // create jobClient
        String content = getJobContent(jobJsonPath);
        Map params =  PublicUtil.jsonStrToObject(content, Map.class);
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        JobClient jobClient = new JobClient(paramAction);

        // create jobIdentifier
        String jobId = "jobId";
        String appId = "appId";
        String taskId = "taskId";
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

        // get pluginInfo
        String pluginInfo = jobClient.getPluginInfo();
        Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
        String md5plugin = MD5Util.getMd5String(pluginInfo);
        properties.setProperty("md5sum", md5plugin);

        // create client
        String pluginParentPath = USER_DIR + SP + "pluginLibs";
        IClient client = ClientFactory.buildPluginClient(pluginInfo, pluginParentPath);

        // client init
        ClassLoaderCallBackMethod.callbackAndReset(new CallBack<String>() {
            @Override
            public String execute() throws Exception {
                client.init(properties);
                return null;
            }
        }, client.getClass().getClassLoader(), true);

        // test target method
        ClassLoaderCallBackMethod.callbackAndReset(new CallBack<Object>() {
            @Override
            public Object execute() throws Exception {
                JobResult jobResult = client.submitJob(jobClient);
                return jobResult;
            }
        }, client.getClass().getClassLoader(), true);

        LOG.info("Launcher Success!");
        System.exit(0);
    }

    public static String getJobContent(String jobJsonPath) {
        File jobFile = new File(jobJsonPath);
        try (
                InputStreamReader isr = new InputStreamReader(new FileInputStream(jobFile));
                BufferedReader reader = new BufferedReader(isr);
        ) {
            String content = "";
            String line = reader.readLine();
            while (line != null) {
                content = content + line;
                line = reader.readLine();
            }
            return content;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
