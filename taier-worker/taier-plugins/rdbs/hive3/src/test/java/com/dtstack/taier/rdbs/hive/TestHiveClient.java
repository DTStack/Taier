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

package com.dtstack.taier.rdbs.hive;

import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.pojo.ClusterResource;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.MD5Util;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.rdbs.common.AbstractRdbsClient;
import com.dtstack.taier.rdbs.common.executor.AbstractConnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/** @author dtstack tiezhu 2021/4/20 星期二 */
public class TestHiveClient {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRdbsClient.class);

    public static void main(String[] args) throws IOException {

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

            HiveClient client = new HiveClient();
            client.init(properties);

            ClusterResource clusterResource = client.getClusterResource();

            LOG.info("submit success!");
            LOG.info(clusterResource.toString());
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
