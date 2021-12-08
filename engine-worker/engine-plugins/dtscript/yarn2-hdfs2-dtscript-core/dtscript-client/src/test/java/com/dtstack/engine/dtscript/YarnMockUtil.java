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

package com.dtstack.engine.dtscript;

import com.dtstack.engine.pluginapi.pojo.ParamAction;
import com.dtstack.engine.pluginapi.JarFileInfo;
import com.dtstack.engine.pluginapi.JobClient;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.powermock.api.mockito.PowerMockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public class YarnMockUtil {

    public static ApplicationReportPBImpl mockApplicationReport(YarnApplicationState state) {
        ApplicationReportPBImpl report = PowerMockito.mock(ApplicationReportPBImpl.class);
        when(report.getFinalApplicationStatus()).thenReturn(FinalApplicationStatus.SUCCEEDED);
        if (state == null) {
            when(report.getYarnApplicationState()).thenReturn(YarnApplicationState.RUNNING);
        } else {
            when(report.getYarnApplicationState()).thenReturn(state);
        }
        when(report.getTrackingUrl()).thenReturn("http://dtstack01:8088");
        return report;
    }

    public static JobClient mockJobClient(String jobType, String jarPath) throws Exception {
        String taskId = "9999";
        String sqlText = "ADD JAR WITH /data/sftp/21_window_WindowJoin.jar AS dtstack.WindowJoin";
        ParamAction paramAction = new ParamAction();
        if ("perJob".equalsIgnoreCase(jobType)) {
            paramAction.setTaskType(0);
            paramAction.setComputeType(0);
        } else {
            paramAction.setTaskType(1);
            paramAction.setComputeType(1);
        }

        paramAction.setTaskId(taskId);
        paramAction.setSqlText(sqlText);
        paramAction.setTenantId(0L);
        paramAction.setTaskParams("{\"test\":\"test\"}");
        paramAction.setExternalPath("/tmp/savepoint");
        Map<String, Object> map = new HashMap();
        map.put("yarnConf", new HashMap());
        paramAction.setPluginInfo(map);

        JobClient jobClient = new JobClient(paramAction);

        JarFileInfo jarFileInfo = new JarFileInfo();
        jarFileInfo.setJarPath(jarPath);
        jarFileInfo.setMainClass("dtstack.WindowJoin");
        jobClient.setCoreJarInfo(jarFileInfo);
        return jobClient;
    }

}
