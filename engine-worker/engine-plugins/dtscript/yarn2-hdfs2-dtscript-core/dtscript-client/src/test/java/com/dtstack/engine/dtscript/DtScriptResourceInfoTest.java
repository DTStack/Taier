package com.dtstack.engine.dtscript;

import com.dtstack.engine.base.resource.AbstractYarnResourceInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dtscript.api.DtYarnConstants;
import com.dtstack.engine.dtscript.client.Client;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/29 14:59
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JobConf.class, Client.class, DtScriptUtil.class, ExceptionUtil.class,
        ConverterUtils.class, FileSystem.class, YarnClientApplication.class,
        Gson.class, StringUtils.class, YarnClient.class, JobResult.class, DtYarnConstants.class,
        ApplicationConstants.class,DtScriptResourceInfo.class})
@PowerMockIgnore("javax.net.ssl.*")
public class DtScriptResourceInfoTest {

    @Test
    public void testJudgeSlots() throws Exception{
        JobClient jobClient = DataUtil.getJobClient();


        String queueName = "a";
        Integer yarnAccepterTaskNumber = 3;
        YarnClient yarnClient = PowerMockito.mock(YarnClient.class);

        List<ApplicationReport> acceptedApps = PowerMockito.mock(List.class);
        PowerMockito.when(yarnClient.getApplications()).thenReturn(acceptedApps);
        PowerMockito.when(acceptedApps.size()).thenReturn(2);
        List<NodeReport> nodeReports = PowerMockito.mock(List.class);
        NodeReport nodeReport = PowerMockito.mock(NodeReport.class);
        PowerMockito.when(yarnClient.getNodeReports(Mockito.any(NodeState.class))).thenReturn(Arrays.asList(nodeReport));

        Resource capability = PowerMockito.mock(Resource.class);
        Resource used = PowerMockito.mock(Resource.class);
        PowerMockito.when(nodeReport.getCapability()).thenReturn(capability);
        PowerMockito.when(nodeReport.getUsed()).thenReturn(used);

        PowerMockito.when(capability.getMemory()).thenReturn(3000);
        PowerMockito.when(capability.getVirtualCores()).thenReturn(10);

        NodeId nodeId = PowerMockito.mock(NodeId.class);
        PowerMockito.when(nodeReport.getNodeId()).thenReturn(nodeId);
        PowerMockito.when(nodeId.toString()).thenReturn("node01");



        PowerMockito.when(used.getMemory()).thenReturn(512);
        PowerMockito.when(used.getVirtualCores()).thenReturn(2);



        DtScriptResourceInfo dtScriptResourceInfo = new DtScriptResourceInfo(yarnClient, queueName, yarnAccepterTaskNumber);
        dtScriptResourceInfo.judgeSlots(jobClient);


    }


}
