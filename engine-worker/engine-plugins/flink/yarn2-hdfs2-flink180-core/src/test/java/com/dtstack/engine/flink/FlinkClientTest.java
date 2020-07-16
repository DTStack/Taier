package com.dtstack.engine.flink;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.SFTPHandler;
import com.dtstack.engine.flink.enums.FlinkYarnMode;
import com.dtstack.engine.flink.factory.AbstractClientFactory;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.dtstack.engine.flink.plugininfo.SqlPluginInfo;
import com.dtstack.engine.flink.util.FileUtil;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.dtstack.engine.flink.util.HadoopConf;
import com.google.common.collect.Maps;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HistoryServerOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeReportPBImpl;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/7/6
 * Company: www.dtstack.com
 * @author xiuzhu
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({SFTPHandler.class, FlinkClientBuilder.class,
	FlinkClusterClientManager.class, PoolHttpClient.class,
	FileSystem.class, FileUtil.class, PublicUtil.class,
	FlinkConfUtil.class, FlinkUtil.class, PerJobClientFactory.class,
	AbstractClientFactory.class, FlinkClient.class})
@PowerMockIgnore("javax.net.ssl.*")
public class FlinkClientTest {

	@Mock
    FlinkClientBuilder flinkClientBuilder;

	@Mock
	YarnClient yarnClient;

	@Mock
    FlinkClusterClientManager flinkClusterClientManager;


	@InjectMocks
	private FlinkClient flinkClient;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		File file = PowerMockito.mock(File.class);
		PowerMockito.whenNew(File.class).withArguments(anyString()).thenReturn(file);
		when(file.exists()).thenReturn(true);
		when(file.isFile()).thenReturn(true);
		when(file.getParentFile()).thenReturn(file);
		when(file.getAbsolutePath()).thenReturn("hdfs://user/tmp/tmpJar.jar");

		PowerMockito.mockStatic(SFTPHandler.class);
		SFTPHandler sftpHandler = PowerMockito.mock(SFTPHandler.class);
		when(SFTPHandler.getInstance(any())).thenReturn(sftpHandler);
		when(sftpHandler.loadFromSftp(any(), any(), any())).thenReturn("test/path");
		when(sftpHandler.downloadDir(any(), any())).thenReturn(1);

		FileSystem fs = PowerMockito.mock(FileSystem.class);
		when(fs.exists(any())).thenReturn(true);
		when(fs.open(any())).thenReturn(PowerMockito.mock(FSDataInputStream.class));
		PowerMockito.mockStatic(FileSystem.class);
		when(FileSystem.get(any(), any())).thenReturn(fs);

	}

	@Test
	public void testInit() throws Exception {
		Properties prop = new Properties();
		prop.put("jarTmpDir", "test/tmp");
		prop.put("clusterMode", "test");

		String sqlPluginRootDir = temporaryFolder.newFolder("sqlPluginDir").getAbsolutePath();
		prop.put("remotePluginRootDir", sqlPluginRootDir);
		prop.put("flinkPluginRoot", sqlPluginRootDir);
		prop.put("monitorAddress", "monitorAddress");
		prop.put("hadoopConf", new HashMap<>());
		prop.put("yarnConf", new HashMap<>());

		temporaryFolder.newFolder("sqlPluginDir", "sqlplugin");

		flinkClient.init(prop);

		Class<? extends FlinkClient> flinkClientClass = flinkClient.getClass();
		Field flinkExtPropField = flinkClientClass.getDeclaredField("flinkExtProp");
		flinkExtPropField.setAccessible(true);
		Assert.assertNotNull(flinkExtPropField.get(flinkClient));

		Field flinkConfigField = flinkClientClass.getDeclaredField("flinkConfig");
		flinkConfigField.setAccessible(true);
		Assert.assertNotNull(flinkConfigField.get(flinkClient));

		Field tmpFileDirPathField = flinkClientClass.getDeclaredField("tmpFileDirPath");
		tmpFileDirPathField.setAccessible(true);
		Assert.assertNotNull(tmpFileDirPathField.get(flinkClient));

		Field syncPluginInfoField = flinkClientClass.getDeclaredField("syncPluginInfo");
		syncPluginInfoField.setAccessible(true);
		Assert.assertNotNull(syncPluginInfoField.get(flinkClient));

		Field sqlPluginInfoField = flinkClientClass.getDeclaredField("sqlPluginInfo");
		sqlPluginInfoField.setAccessible(true);
		Assert.assertNotNull(sqlPluginInfoField.get(flinkClient));

		Field flinkClusterClientManagerField = flinkClientClass.getDeclaredField("flinkClusterClientManager");
		flinkClusterClientManagerField.setAccessible(true);
		Assert.assertNotNull(flinkClusterClientManagerField.get(flinkClient));

	}

	@Test
	public void testBeforeSubmitFunc() throws Exception {

		String absolutePath = temporaryFolder.newFile("21_window_WindowJoin.jar").getAbsolutePath();
		JobClient jobClient = YarnMockUtil.mockJobClient("session", absolutePath);

		FlinkConfig flinkConfig = new FlinkConfig();
		Map<String, String> map = new HashMap<>();
		map.put("test", "test");
		flinkConfig.setSftpConf(map);
		MemberModifier.field(FlinkClient.class, "flinkConfig")
			.set(flinkClient, flinkConfig);
		MemberModifier.field(FlinkClient.class, "cacheFile")
			.set(flinkClient, Maps.newConcurrentMap());
		MemberModifier.field(FlinkClient.class, "hadoopConf")
				.set(flinkClient, new HadoopConf());

		flinkClient.beforeSubmitFunc(jobClient);
	}

	@Test
	public void testGetJobLog() throws Exception {
		String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
		String appId = "application_1594003499276_1278";
		String taskId = "taskId";
		JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any())).thenReturn("{\"app\":{\"amContainerLogs\":\"http://dtstack01:8088/ws/v1/cluster/apps/application_9527\"}}");

		ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
		when(yarnClient.getApplicationReport(any())).thenReturn(report);
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

		ClusterClient currClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient(any())).thenReturn(currClient);

		String jobLog = flinkClient.getJobLog(jobIdentifier);
		Assert.assertNotNull(jobLog);

		ApplicationReportPBImpl reportFinish = YarnMockUtil.mockApplicationReport(YarnApplicationState.FINISHED);
		when(yarnClient.getApplicationReport(any())).thenReturn(reportFinish);
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

		Configuration flinkConfig = new Configuration();
		flinkConfig.setString(HistoryServerOptions.HISTORY_SERVER_WEB_ADDRESS, "dtstack01");
		flinkConfig.setString(String.valueOf(HistoryServerOptions.HISTORY_SERVER_WEB_PORT), "9527");
		when(flinkClientBuilder.getFlinkConfiguration()).thenReturn(flinkConfig);

		String jobLogFinished = flinkClient.getJobLog(jobIdentifier);
		Assert.assertNotNull(jobLogFinished);
	}

	@Test
	public void testJudgeSlots() throws Exception {

		JobClient perJobClient = YarnMockUtil.mockJobClient("perJob", null);

		ApplicationReport report = new ApplicationReportPBImpl();
		report.setYarnApplicationState(YarnApplicationState.RUNNING);
		List<NodeReport> nodeReports = new ArrayList<>();
		NodeReport nodeReport = new NodeReportPBImpl();
		nodeReport.setUsed(Resource.newInstance(0, 0));
		nodeReport.setCapability(Resource.newInstance(10000000, 100));
		nodeReport.setNodeId(NodeId.newInstance("dtstack01", 9527));
		nodeReports.add(nodeReport);
		when(yarnClient.getNodeReports(any())).thenReturn(nodeReports);

		when(yarnClient.getApplications((EnumSet<YarnApplicationState>) any())).thenReturn(new ArrayList<>());
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

		FlinkConfig flinkConfig = new FlinkConfig();
		flinkConfig.setFlinkSessionSlotCount(10);
		MemberModifier.field(FlinkClient.class, "flinkConfig")
			.set(flinkClient, flinkConfig);

		boolean perJudgeSlot = flinkClient.judgeSlots(perJobClient);
		Assert.assertTrue(perJudgeSlot);

		JobClient jobClient = YarnMockUtil.mockJobClient("session", null);

		when(flinkClusterClientManager.getIsClientOn()).thenReturn(true);
		String webInterfaceURL = "http://dtstack01:8088";
		ClusterClient clusterClient = PowerMockito.mock(ClusterClient.class);
		when(clusterClient.getWebInterfaceURL()).thenReturn(webInterfaceURL);
		when(flinkClusterClientManager.getClusterClient()).thenReturn(clusterClient);

		String taskmanagers = "{\"taskmanagers\":[{\"freeSlots\":9, \"slotsNumber\":4}]}";
		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any(String.class), anyObject(), any(int.class))).thenReturn(taskmanagers);

		//when(flinkClient.getMessageByHttp(anyString())).thenReturn(taskmanagers);

		boolean judgeSlot = flinkClient.judgeSlots(jobClient);
		Assert.assertTrue(judgeSlot);

	}

	@Test
	public void testGetCheckpoints() throws Exception {
		JobIdentifier jobIdentifier =
			new JobIdentifier("engineId","application_1593762151957_0080", "taskId");

		ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
		when(yarnClient.getApplicationReport(any())).thenReturn(report);
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

		ClusterClient currClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient(any())).thenReturn(currClient);

		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any())).thenReturn("{\"app\":{\"amContainerLogs\":\"http://dtstack01:8088/ws/v1/cluster/apps/application_9527\"}}");

		String checkpoints = flinkClient.getCheckpoints(jobIdentifier);
		Assert.assertNotNull(checkpoints);

	}

	@Test
	public void testCancelJob() throws Exception {
		String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
		String appId = "application_1594003499276_1278";
		String taskId = "taskId";
		JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient()).thenReturn(clusterClient);

		JobResult jobResult = flinkClient.cancelJob(jobIdentifier);
		Assert.assertNotNull(jobResult);
	}

	@Test
	public void testGetJobStatus() throws Exception {
		String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
		String appId = "application_1594003499276_1278";
		String taskId = "taskId";
		JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

		ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
		when(yarnClient.getApplicationReport(any())).thenReturn(report);
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

		RdosTaskStatus jobStatus = flinkClient.getJobStatus(jobIdentifier);
		Assert.assertNotNull(jobStatus);

		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any())).thenReturn("{\"state\":\"RUNNING\"}");

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient()).thenReturn(clusterClient);
		jobIdentifier.setApplicationId(null);
		RdosTaskStatus jobStatus2 = flinkClient.getJobStatus(jobIdentifier);
		Assert.assertNotNull(jobStatus2);
	}

	@Test
	public void testGetJobMaster() throws Exception {
		String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
		String appId = "application_1594003499276_1278";
		String taskId = "taskId";
		JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId);

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient(any())).thenReturn(clusterClient);

		ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
		when(yarnClient.getApplicationReport(any())).thenReturn(report);
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

		String jobMaster = flinkClient.getJobMaster(jobIdentifier);
		Assert.assertNotNull(jobMaster);
	}

	@Test
	public void testAfterSubmitFunc() throws Exception {
		JobClient jobClient = YarnMockUtil.mockJobClient("perJob",null);

		String tmpFile = temporaryFolder.newFile("tmpFile").getAbsolutePath();
		List<String> files = new ArrayList<>();
		files.add(tmpFile);
		Map<String, List<String>> cacheFile = new HashMap<>();
		cacheFile.put(jobClient.getTaskId(), files);

		MemberModifier.field(FlinkClient.class, "cacheFile")
			.set(flinkClient, cacheFile);

		File file = PowerMockito.mock(File.class);
		PowerMockito.whenNew(File.class).withArguments(anyString()).thenReturn(file);
		when(file.exists()).thenReturn(false);

		flinkClient.afterSubmitFunc(jobClient);
	}

	@Test
	public void testSubmitJobWithJar() throws Exception {

		String absolutePath = temporaryFolder.newFile("21_window_WindowJoin.jar").getAbsolutePath();
		JobClient jobClient = YarnMockUtil.mockJobClient("session", absolutePath);

		MemberModifier.field(FlinkClient.class, "flinkConfig")
				.set(flinkClient, new FlinkConfig());
		MemberModifier.field(FlinkClient.class, "hadoopConf")
				.set(flinkClient, new HadoopConf());

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		YarnMockUtil.mockPackagedProgram();
		ClusterSpecification clusterSpecification = YarnMockUtil.mockClusterSpecification();

		when(flinkClusterClientManager.getClusterClient()).thenReturn(clusterClient);

		PowerMockito.mockStatic(FlinkConfUtil.class);
		when(FlinkConfUtil.createClusterSpecification(any(Configuration.class), any(int.class), any(Properties.class)))
				.thenReturn(clusterSpecification);

		Class<? extends FlinkClient> flinkClientClass = flinkClient.getClass();
		Method submitJobWithJarMethod = flinkClientClass.getDeclaredMethod("submitJobWithJar", JobClient.class);
		submitJobWithJarMethod.setAccessible(true);
		JobResult jobResult = (JobResult) submitJobWithJarMethod.invoke(flinkClient, jobClient);

		Assert.assertTrue(jobResult.getMsgInfo().contains("submit job is success"));
	}

	@Test
	public void testSubmitSqlJob() throws Exception {
		JobClient jobClient = YarnMockUtil.mockJobClient("perJob", null);

		MemberModifier.field(FlinkClient.class, "flinkConfig")
				.set(flinkClient, new FlinkConfig());
		MemberModifier.field(FlinkClient.class, "hadoopConf")
				.set(flinkClient, new HadoopConf());
		MemberModifier.field(FlinkClient.class, "cacheFile")
				.set(flinkClient, Maps.newConcurrentMap());

		String sqlPluginRootDir = temporaryFolder.newFolder("sqlPluginDir").getAbsolutePath();
		temporaryFolder.newFolder("sqlPluginDir", "sqlplugin");
		temporaryFolder.newFile("sqlPluginDir/sqlplugin/core-test.jar").getAbsolutePath();

		Properties prop = new Properties();
		prop.put("remotePluginRootDir", sqlPluginRootDir);
		prop.put("flinkPluginRoot", sqlPluginRootDir);
		String propStr = PublicUtil.objToString(prop);
		SqlPluginInfo sqlPluginInfo = SqlPluginInfo.create(PublicUtil.jsonStrToObject(propStr, FlinkConfig.class));
		MemberModifier.field(FlinkClient.class, "sqlPluginInfo")
				.set(flinkClient, sqlPluginInfo);

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		YarnMockUtil.mockPackagedProgram();
		ClusterSpecification clusterSpecification = YarnMockUtil.mockClusterSpecification();

		when(flinkClusterClientManager.getClusterClient()).thenReturn(clusterClient);
		when(flinkClientBuilder.getFlinkConfiguration()).thenReturn(new Configuration());

		PowerMockito.mockStatic(FlinkConfUtil.class);
		when(FlinkConfUtil.createClusterSpecification(any(Configuration.class), any(int.class), any(Properties.class)))
				.thenReturn(clusterSpecification);

		YarnClusterDescriptor yarnClusterDescriptor = YarnMockUtil.mockYarnClusterDescriptor(clusterClient);

		PerJobClientFactory perJobClientFactory = PowerMockito.mock(PerJobClientFactory.class);
		when(perJobClientFactory.createPerJobClusterDescriptor(any(JobClient.class)))
				.thenReturn(yarnClusterDescriptor);
		PowerMockito.mockStatic(PerJobClientFactory.class);
		when(PerJobClientFactory.getPerJobClientFactory()).thenReturn(perJobClientFactory);

		Class<? extends FlinkClient> flinkClientClass = flinkClient.getClass();
		Method submitSqlJobMethod = flinkClientClass.getDeclaredMethod("submitSqlJob", JobClient.class);
		submitSqlJobMethod.setAccessible(true);
		JobResult jobResult = (JobResult) submitSqlJobMethod.invoke(flinkClient, jobClient);

		Assert.assertTrue(jobResult.getMsgInfo().contains("submit job is success"));
	}


	@Test
	public void testGetReqUrl() {
		String webInterfaceURL = "http://dtstack01:8088";
		ClusterClient clusterClient = PowerMockito.mock(ClusterClient.class);
		when(clusterClient.getWebInterfaceURL()).thenReturn(webInterfaceURL);
		when(flinkClusterClientManager.getClusterClient()).thenReturn(clusterClient);
		String perReqUrl = flinkClient.getReqUrl(FlinkYarnMode.PER_JOB);
		Assert.assertEquals(perReqUrl, "${monitor}");

		String sessionReqUrl = flinkClient.getReqUrl(FlinkYarnMode.SESSION);
		Assert.assertEquals(sessionReqUrl, webInterfaceURL);
	}

	@Test
	public void testGetMessageByHttp() throws Exception {

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient()).thenReturn(clusterClient);

		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any(String.class), anyObject(), any(int.class))).thenReturn("testGetMessageByHttp");

		String res = flinkClient.getMessageByHttp("job");
		Assert.assertEquals(res, "testGetMessageByHttp");
	}

	@Test
	public void testBuildSavepointSetting() throws Exception {

		Properties props = new Properties();
		props.setProperty("allowNonRestoredState", "true");
		PowerMockito.mockStatic(PublicUtil.class);
		when(PublicUtil.stringToProperties(any())).thenReturn(props);

		JobClient jobClient = YarnMockUtil.mockJobClient("perJob", null);
		Class<? extends FlinkClient> flinkClientClass = flinkClient.getClass();
		Method savepointSettingMethod = flinkClientClass.getDeclaredMethod("buildSavepointSetting", JobClient.class);
		savepointSettingMethod.setAccessible(true);
		SavepointRestoreSettings invoke = (SavepointRestoreSettings) savepointSettingMethod.invoke(flinkClient, jobClient);
		Assert.assertNotNull(invoke);
	}

}
