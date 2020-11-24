package com.dtstack.engine.dtscript;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dtscript.client.Client;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;


/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/19 09:52
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Client.class, StringUtils.class, YarnClient.class})
@PowerMockIgnore("javax.net.ssl.*")
public class DtScriptClientTest {

    @Mock
    Client client;

    @Mock
    YarnClient yarnClient;


    @Test
    public void testInit() throws Exception {
        DtScriptClient dtScriptClient = PowerMockito.mock(DtScriptClient.class);
        whenNew(DtScriptClient.class).withNoArguments().thenReturn(dtScriptClient);

//        PowerMockito.doNothing().when(dtScriptClient.init());
    }

    @Test
    public void testProcessSubmitJobWithType() throws Exception {

        DtScriptClient dtScriptClient = PowerMockito.mock(DtScriptClient.class);
        JobClient jobClient = PowerMockito.mock(JobClient.class);
        JobResult jobResult = PowerMockito.mock(JobResult.class);

        whenNew(DtScriptClient.class).withNoArguments().thenReturn(dtScriptClient);
        when(jobClient.getJobType()).thenReturn(EJobType.KYLIN);
        when(dtScriptClient.processSubmitJobWithType(jobClient)).thenReturn(jobResult);

        Assert.assertNotNull(dtScriptClient.processSubmitJobWithType(jobClient));
    }

    @Test
    public void testCancelJob() throws Exception {

        String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        String appId = "application_1594003499276_1278";
        String taskId = "taskId";

        DtScriptClient dtScriptClient = PowerMockito.mock(DtScriptClient.class);
        JobIdentifier jobIdentifier = PowerMockito.mock(JobIdentifier.class);
        JobResult jobResult = PowerMockito.mock(JobResult.class);

        whenNew(DtScriptClient.class).withNoArguments().thenReturn(dtScriptClient);
        whenNew(JobIdentifier.class).withArguments(jobId,appId,taskId).thenReturn(jobIdentifier);


        when(jobIdentifier.getEngineJobId()).thenReturn(jobId);
        client.kill(jobId);
        when(dtScriptClient.cancelJob(jobIdentifier)).thenReturn(jobResult);

        Assert.assertNotNull(dtScriptClient.cancelJob(jobIdentifier));

    }

    @Test
    public void testGetJobStatus() throws Exception {

        String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        String appId = "application_1594003499276_1278";
        String taskId = "taskId";

        DtScriptClient dtScriptClient = PowerMockito.mock(DtScriptClient.class);
        JobIdentifier jobIdentifier = PowerMockito.mock(JobIdentifier.class);

        whenNew(DtScriptClient.class).withNoArguments().thenReturn(dtScriptClient);
        whenNew(JobIdentifier.class).withArguments(jobId,appId,taskId).thenReturn(jobIdentifier);
        mockStatic(StringUtils.class);

        when(StringUtils.isEmpty(jobIdentifier.getEngineJobId())).thenReturn(false);
        when(jobIdentifier.getEngineJobId()).thenReturn(jobId);

        ApplicationReportPBImpl report = PowerMockito.mock(ApplicationReportPBImpl.class);
        when(client.getApplicationReport(jobIdentifier.getEngineJobId())).thenReturn(report);
        when(report.getFinalApplicationStatus()).thenReturn(FinalApplicationStatus.SUCCEEDED);
        when(report.getYarnApplicationState()).thenReturn(YarnApplicationState.RUNNING);
        when(report.getTrackingUrl()).thenReturn("http://dtstack01:8088");

        when(dtScriptClient.getJobStatus(jobIdentifier)).thenReturn(RdosTaskStatus.RUNNING);

    }

    @Test
    public void testGetJobMaster() throws Exception {

        String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
        String appId = "application_1594003499276_1278";
        String taskId = "taskId";
        String url = "";

        YarnClient yarnClient = PowerMockito.mock(YarnClient.class);
        when(client.getYarnClient()).thenReturn(yarnClient);
        Assert.assertNotNull(yarnClient);

        yarnClient.getNodeReports();

    }

}
