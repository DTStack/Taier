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

package com.dtstack.taier.hadoop;


import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.MD5Util;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

public class TestHadoopClient {

    private static final Logger LOG = LoggerFactory.getLogger(TestHadoopClient.class);

    public static void main(String[] args) throws Exception {

        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try {
            System.setProperty("HADOOP_USER_NAME", "admin");

            // input params json file path
            String filePath = args[0];
            File paramsFile = new File(filePath);
            fileInputStream = new FileInputStream(paramsFile);
            inputStreamReader = new InputStreamReader(fileInputStream);
            reader = new BufferedReader(inputStreamReader);
            String request = reader.readLine();
            Map params = PublicUtil.jsonStrToObject(request, Map.class);
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            JobClient jobClient = new JobClient(paramAction);

            String pluginInfo = jobClient.getPluginInfo();
            Properties properties = PublicUtil.jsonStrToObject(pluginInfo, Properties.class);
            String md5plugin = MD5Util.getMd5String(pluginInfo);
            properties.setProperty("md5sum", md5plugin);

            HadoopClient client = new HadoopClient();
            client.init(properties);

            System.exit(0);
        } catch (Exception e) {
            LOG.error("submit error!", e);
        } finally {
            if (reader != null) {
                reader.close();
                inputStreamReader.close();
                fileInputStream.close();
            }
        }
    }


}
