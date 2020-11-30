package com.dtstack.engine.flink;

import com.dtstack.engine.base.filesystem.FilesystemManager;
import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.flink.enums.ClusterMode;
import com.dtstack.engine.flink.enums.FlinkYarnMode;
import com.dtstack.engine.flink.factory.AbstractClientFactory;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.dtstack.engine.flink.plugininfo.SqlPluginInfo;
import com.dtstack.engine.flink.plugininfo.SyncPluginInfo;
import com.dtstack.engine.flink.util.FileUtil;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.dtstack.engine.flink.util.HadoopConf;
import com.dtstack.engine.worker.enums.ClassLoaderType;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.math3.analysis.function.Pow;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
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
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/7/6
 * Company: www.dtstack.com
 * @author xiuzhu
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FlinkClientBuilder.class,
	FlinkClusterClientManager.class, PoolHttpClient.class,
	FileSystem.class, FileUtil.class, PublicUtil.class,
	FlinkConfUtil.class, FlinkUtil.class, PerJobClientFactory.class,
	AbstractClientFactory.class, FlinkClient.class, PackagedProgram.class,
	Jsoup.class})
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
		when(file.getAbsoluteFile()).thenReturn(file);
		URI uri = new URI("file", null, "/tmp", null);
		when(file.toURI()).thenReturn(uri);

		FileSystem fs = PowerMockito.mock(FileSystem.class);
		when(fs.exists(any())).thenReturn(true);
		when(fs.open(any())).thenReturn(PowerMockito.mock(FSDataInputStream.class));
		PowerMockito.mockStatic(FileSystem.class);
		when(FileSystem.get(any(), any())).thenReturn(fs);

		FlinkConfig flinkConfig = new FlinkConfig();
		flinkConfig.setSftpConf(new SftpConfig());
		MemberModifier.field(FlinkClient.class, "flinkConfig").set(flinkClient, flinkConfig);
		MemberModifier.field(FlinkClient.class, "cacheFile").set(flinkClient, Maps.newConcurrentMap());
		HadoopConf hadoopConf = new HadoopConf();
		hadoopConf.initHadoopConf(new HashMap<>());
		hadoopConf.initYarnConf(new HashMap<>());
		MemberModifier.field(FlinkClient.class, "hadoopConf").set(flinkClient, hadoopConf);
		FilesystemManager filesystemManager = PowerMockito.mock(FilesystemManager.class);
		File fileTmp = new File("/tmp");
		when(filesystemManager.downloadFile(any(), any())).thenReturn(fileTmp);
		MemberModifier.field(FlinkClient.class, "filesystemManager").set(flinkClient, filesystemManager);
	}

	/**
	 * flink client init test
	 */
	@Test
	public void testInit() throws Exception{
		MemberModifier.field(FlinkClient.class, "cacheFile")
				.set(flinkClient, Maps.newConcurrentMap());

		String sqlPluginRootDir = temporaryFolder.newFolder("sqlPluginDir").getAbsolutePath();
		temporaryFolder.newFolder("sqlPluginDir", "sqlplugin");
		temporaryFolder.newFile("sqlPluginDir/sqlplugin/core-test.jar").getAbsolutePath();

		Properties prop = new Properties();
		prop.put("remotePluginRootDir", sqlPluginRootDir);
		prop.put("flinkPluginRoot", sqlPluginRootDir);
		prop.put("yarnConf", new HashMap<>());
		prop.put("hadoopConf", new HashMap<>());
		prop.put("clusterMode", ClusterMode.STANDALONE.name());

		flinkClient.init(prop);
	}

	@Test
	public void testProcessSubmitJobWithType() throws Exception {

		String absolutePath = temporaryFolder.newFile("21_window_WindowJoin.jar").getAbsolutePath();
		JobClient jobClient = YarnMockUtil.mockJobClient("perJob", absolutePath);

		// test flink mr
		jobClient.setJobType(EJobType.MR);

		PerJobClientFactory perJobClientFactory = PowerMockito.mock(PerJobClientFactory.class);
		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		YarnClusterDescriptor yarnClusterDescriptor = YarnMockUtil.mockYarnClusterDescriptor(clusterClient);
		when(perJobClientFactory.createPerJobClusterDescriptor(any(JobClient.class))).thenReturn(yarnClusterDescriptor);
		when(flinkClusterClientManager.getClientFactory()).thenReturn(perJobClientFactory);

		Configuration configuration = new Configuration();
		when(flinkClientBuilder.getFlinkConfiguration()).thenReturn(configuration);
		ClusterSpecification clusterSpecification = YarnMockUtil.mockClusterSpecification();
		PowerMockito.mockStatic(FlinkConfUtil.class);
		when(FlinkConfUtil.createClusterSpecification(any(Configuration.class), any(int.class), any(Properties.class)))
				.thenReturn(clusterSpecification);

		JobResult jobResult = flinkClient.processSubmitJobWithType(jobClient);
		Assert.assertTrue(jobResult.getMsgInfo().contains("submit job is success"));

		// test flink sql
		jobClient.setJobType(EJobType.SQL);

		String sqlPluginRootDir = temporaryFolder.newFolder("sqlPluginDir").getAbsolutePath();
		temporaryFolder.newFolder("sqlPluginDir", "sqlplugin");
		temporaryFolder.newFile("sqlPluginDir/sqlplugin/core-test.jar").getAbsolutePath();
		FlinkConfig flinkConfig = new FlinkConfig();
		flinkConfig.setFlinkPluginRoot(sqlPluginRootDir);
		SqlPluginInfo sqlPluginInfo = SqlPluginInfo.create(flinkConfig);

		MemberModifier.field(FlinkClient.class, "sqlPluginInfo").set(flinkClient, sqlPluginInfo);
		JobResult jobResultSql = flinkClient.processSubmitJobWithType(jobClient);
		Assert.assertTrue(jobResultSql.getMsgInfo().contains("submit job is success"));

		// test flink sync
		jobClient = YarnMockUtil.mockJobClient("session", absolutePath);
		jobClient.setJobType(EJobType.SYNC);
		JarFileInfo jarFileInfo = new JarFileInfo();
		jarFileInfo.setMainClass("flinkx.main");
		jobClient.setCoreJarInfo(jarFileInfo);

		String syncPluginRootDir = temporaryFolder.newFolder("syncPluginDir").getAbsolutePath();
		temporaryFolder.newFolder("syncPluginDir", "syncplugin");
		temporaryFolder.newFile("syncPluginDir/syncplugin/flinkx-core-test.jar").getAbsolutePath();

		FlinkConfig flinkConfigSync = new FlinkConfig();
		flinkConfigSync.setFlinkPluginRoot(syncPluginRootDir);
		flinkConfigSync.setMonitorAddress("http://dtstack:8081");
		SyncPluginInfo syncPluginInfo = SyncPluginInfo.create(flinkConfigSync);
		MemberModifier.field(FlinkClient.class, "syncPluginInfo").set(flinkClient, syncPluginInfo);

		PackagedProgram packagedProgram = PowerMockito.mock(PackagedProgram.class);
		PowerMockito.mockStatic(FlinkUtil.class);
		when(FlinkUtil.buildProgram(any(String.class), any(String.class), any(List.class), any(EJobType.class),
				any(), any(String[].class), any(SavepointRestoreSettings.class), any(FilesystemManager.class)))
				.thenReturn(packagedProgram);

		MemberModifier.field(FlinkClient.class, "tmpFileDirPath").set(flinkClient, "./tmp180");
		when(flinkClusterClientManager.getClusterClient(any())).thenReturn(clusterClient);

		JobResult jobResultSync = flinkClient.processSubmitJobWithType(jobClient);
		Assert.assertTrue(jobResultSync.getMsgInfo().contains("submit job is success"));

	}

	@Test
	public void testBeforeSubmitFunc() throws Exception {

		String absolutePath = temporaryFolder.newFile("21_window_WindowJoin.jar").getAbsolutePath();
		JobClient jobClient = YarnMockUtil.mockJobClient("session", absolutePath);

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

		PowerMockito.mockStatic(FileUtil.class);
		String jsonString = "{\"archive\":[{\"path\":\"/jobs/exceptions\",\"json\":\"{}\"}]}";
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);
		when(FileUtil.readJsonFromHdfs(any(String.class), any(org.apache.hadoop.conf.Configuration.class)))
				.thenReturn(jsonObject);

		when(PoolHttpClient.get(any())).thenReturn(null);

		String jobLogFinished = flinkClient.getJobLog(jobIdentifier);
		Assert.assertNotNull(jobLogFinished);
	}

	@Test
	public void testJudgeSlots() throws Exception {

		// perjob judgeSlot
		JobClient perJobClient = YarnMockUtil.mockJobClient("perJob", null);

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

		JudgeResult perJudgeSlot = flinkClient.judgeSlots(perJobClient);
		Assert.assertTrue(perJudgeSlot.available());

		// session judgeSlot
		JobClient jobClient = YarnMockUtil.mockJobClient("session", null);
		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient(null)).thenReturn(clusterClient);

		String taskmanagers = "{\"taskmanagers\":[{\"freeSlots\":9, \"slotsNumber\":4}]}";
		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any(String.class), anyObject(), any(int.class))).thenReturn(taskmanagers);

		JudgeResult judgeSlot = flinkClient.judgeSlots(jobClient);
		Assert.assertTrue(judgeSlot.available());

	}

	@Test
	public void testGetCheckpoints() throws Exception {
		JobIdentifier jobIdentifier =
			 JobIdentifier.createInstance("engineId","application_1593762151957_0080", "taskId");

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
		JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId, false);
		jobIdentifier.setForceCancel(false);

		MemberModifier.field(FlinkClient.class, "jobHistory").set(flinkClient, "http://dtstack:8081");

		ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
		when(yarnClient.getApplicationReport(any())).thenReturn(report);
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);


		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient(any())).thenReturn(clusterClient);

		JobResult jobResult = flinkClient.cancelJob(jobIdentifier);
		Assert.assertNotNull(jobResult);
	}

	@Test
	public void testGetRollingLogBaseInfo() throws Exception {
		String jobId = "40c01cd0c53928fff6a55e8d8b8b022c";
		String appId = "application_1594003499276_1278";
		String taskId = "taskId";
		JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobId, appId, taskId, false);

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient(any())).thenReturn(clusterClient);

		ApplicationReportPBImpl report = YarnMockUtil.mockApplicationReport(null);
		when(yarnClient.getApplicationReport(any())).thenReturn(report);
		when(flinkClientBuilder.getYarnClient()).thenReturn(yarnClient);

		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any(String.class), any(int.class), any(Header[].class))).thenReturn("{\"app\":{\"id\":\"application_1606700198705_0193\",\"user\":\"admin\",\"name\":\"flinTest\",\"queue\":\"a\",\"state\":\"RUNNING\",\"finalStatus\":\"UNDEFINED\",\"trackingUrl\":\"http://dtstack01:8088/proxy/application_1606700198705_0193/\",\"applicationType\":\"Apache Flink\",\"amContainerLogs\":\"http://dtstack03:8042/node/containerlogs/container_e07_1606700198705_0193_01_000001/admin\"}}");

		PowerMockito.mockStatic(Jsoup.class);
		org.jsoup.nodes.Document document = PowerMockito.mock(org.jsoup.nodes.Document.class);
		Elements elements = PowerMockito.mock(Elements.class);
		when(elements.size()).thenReturn(1);
		Element element = PowerMockito.mock(Element.class);
		when(element.select(any(String.class))).thenReturn(elements);
		when(element.attr(any(String.class))).thenReturn("http://dtstack01:8054/app/log?start=568");
		when(element.text()).thenReturn("http://dtstack01:8054/app/log?start=568");
		when(elements.get(any(int.class))).thenReturn(element);
		when(document.getElementsByClass(any(String.class))).thenReturn(elements);
		when(Jsoup.parse(any(String.class))).thenReturn(document);

		List<String> logInfo = flinkClient.getRollingLogBaseInfo(jobIdentifier);
		Assert.assertNotNull(logInfo);
		Assert.assertTrue(logInfo.size() > 0);
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
	public void testGetReqUrl() {
		String webInterfaceURL = "http://dtstack01:8088";
		ClusterClient clusterClient = PowerMockito.mock(ClusterClient.class);
		when(clusterClient.getWebInterfaceURL()).thenReturn(webInterfaceURL);
		when(flinkClusterClientManager.getClusterClient(null)).thenReturn(clusterClient);
		String perReqUrl = flinkClient.getReqUrl(FlinkYarnMode.PER_JOB);
		Assert.assertEquals(perReqUrl, "${monitor}");

		String sessionReqUrl = flinkClient.getReqUrl(FlinkYarnMode.SESSION);
		Assert.assertEquals(sessionReqUrl, webInterfaceURL);
	}

	@Test
	public void testGetMessageByHttp() throws Exception {

		ClusterClient clusterClient = YarnMockUtil.mockClusterClient();
		when(flinkClusterClientManager.getClusterClient(null)).thenReturn(clusterClient);

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
