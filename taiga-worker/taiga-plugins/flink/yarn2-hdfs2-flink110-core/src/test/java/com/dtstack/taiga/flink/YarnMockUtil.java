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

package com.dtstack.taiga.flink;

import com.dtstack.taiga.pluginapi.pojo.ParamAction;
import com.dtstack.taiga.base.enums.ClassLoaderType;
import com.dtstack.taiga.pluginapi.JarFileInfo;
import com.dtstack.taiga.pluginapi.JobClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class YarnMockUtil {

    public static ClusterClient mockClusterClient() throws Exception {

        CompletableFuture<JobID> result = PowerMockito.mock(CompletableFuture.class);
        CompletableFuture<JobID> savePoint = PowerMockito.mock(CompletableFuture.class);
        when(result.complete(result.get())).thenReturn(false);
        when(result.get()).thenReturn(new JobID());

        ClusterClient clusterClient = PowerMockito.mock(ClusterClient.class);
        when(clusterClient.submitJob(any(JobGraph.class))).thenReturn(result);
        when(clusterClient.getClusterId()).thenReturn(PowerMockito.mock(ApplicationIdPBImpl.class));
        when(clusterClient.getWebInterfaceURL()).thenReturn("http://HiLany.github.io/proxy/");
        when(clusterClient.cancelWithSavepoint(any(JobID.class), any(String.class))).thenReturn(savePoint);

        return clusterClient;
    }

    public static ClusterSpecification mockClusterSpecification() {
        JobGraph jobGraph = PowerMockito.mock(JobGraph.class);
        when(jobGraph.getJobID()).thenReturn(new JobID());

        ClusterSpecification clusterSpecification =
                new ClusterSpecification.ClusterSpecificationBuilder()
                        .createClusterSpecification();
        clusterSpecification.setJobGraph(jobGraph);
        return clusterSpecification;
    }

    public static PackagedProgram mockPackagedProgram() throws Exception {
        PackagedProgram packagedProgram = PowerMockito.mock(PackagedProgram.class);
        PowerMockito.whenNew(PackagedProgram.class)
                .withArguments(any(File.class), any(List.class), any(ClassLoaderType.class), any(String.class))
                .thenReturn(packagedProgram);
        return packagedProgram;
    }

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

    public static YarnClusterDescriptor mockYarnClusterDescriptor(ClusterClientProvider clusterClientProvider) throws Exception {
        YarnClusterDescriptor yarnClusterDescriptor = PowerMockito.mock(YarnClusterDescriptor.class);
        when(yarnClusterDescriptor.deployJobCluster(any(ClusterSpecification.class), any(JobGraph.class), any(boolean.class)))
                .thenReturn(clusterClientProvider);
        PowerMockito.whenNew(YarnClusterDescriptor.class)
                .withArguments(any(Configuration.class), any(YarnConfiguration.class),
                        any(String.class), any(YarnClient.class), any(boolean.class))
                .thenReturn(yarnClusterDescriptor);
        when(yarnClusterDescriptor.retrieve(any(ApplicationId.class))).thenReturn(clusterClientProvider);
        return yarnClusterDescriptor;
    }

    public static JobClient mockJobClient(String jobType, String jarPath) throws Exception {
        return mockJobClient(jobType, null, jarPath);
    }

    public static JobClient mockJobClient(String jobType, String sqlNewText, String jarPath) throws Exception {
        String taskId = "9999";
        String sqlText = "ADD JAR WITH /data/sftp/21_window_WindowJoin.jar AS dtstack.WindowJoin";
        if (StringUtils.isNotEmpty(sqlNewText)) {
            sqlText = sqlNewText;
        }
        ParamAction paramAction = new ParamAction();
        if ("perJob".equalsIgnoreCase(jobType)) {
            paramAction.setTaskType(0);
            paramAction.setComputeType(0);
        } else {
            paramAction.setTaskType(1);
            paramAction.setComputeType(1);
        }

        paramAction.setJobId(taskId);
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
