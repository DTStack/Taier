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

package com.dtstack.engine.master.utils;

import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.pluginapi.pojo.ParamAction;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.dtstack.engine.master.dataCollection.DataCollection;

import java.io.File;

public class CommonUtils {
    private final static String SEP = File.separator;
    private final static String TEST_CONF = "test_conf";

    public static void sleep(Integer millons) {
        try {
            Thread.sleep(millons);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setUserDirToTest() {
        String parentDir = System.getProperty("user.dir");
        while (parentDir != null && parentDir.contains(SEP)) {
            String testConfDir = parentDir + SEP + TEST_CONF;
            if (new File(testConfDir).exists()) {
                System.setProperty("user.dir.conf", testConfDir);
                return;
            } else {
                parentDir = parentDir.substring(0, parentDir.lastIndexOf(SEP));
            }
        }
        throw new RuntimeException("Not found dir named test_conf");
    }

    public static JobClient getJobClient() throws Exception {

        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
        JobClient jobClient = new JobClient(paramAction);
        jobClient.setTaskId(jobCache.getJobId());
        return jobClient;

    }
}
