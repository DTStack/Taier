package com.dtstack.engine.flink;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.flink.factory.PerJobClientFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.curator.org.apache.curator.RetryPolicy;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFramework;
import org.apache.flink.shaded.curator.org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/7/6
 * Company: www.dtstack.com
 * @author xiuzhu
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({FlinkClientBuilder.class, FlinkConfig.class,
	FlinkClusterClientManager.class, Cache.class, CuratorFrameworkFactory.class})
@PowerMockIgnore("javax.net.ssl.*")
public class FlinkClusterClientManagerTest {

	@Mock
    FlinkClientBuilder flinkClientBuilder;

	@Mock
	YarnClient yarnClient;

	@Mock
	ClusterClient clusterClient;

	@InjectMocks
    FlinkClusterClientManager flinkClusterClientManager;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetClusterClient() throws Exception {
		String engineId = "engineId";
		String appId = "application_1593762151957_0080";
		String taskId = "taskId";
		JobIdentifier jobIdentifier = new JobIdentifier(engineId,appId, taskId);

		Cache<String, ClusterClient> perJobClientCache = CacheBuilder
			.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
		MemberModifier.field(FlinkClusterClientManager.class, "perJobClientCache")
			.set(flinkClusterClientManager, perJobClientCache);

		ClusterClient clusterClientMock = YarnMockUtil.mockClusterClient();
		YarnClusterDescriptor yarnClusterDescriptor = YarnMockUtil.mockYarnClusterDescriptor(clusterClientMock);
		PerJobClientFactory perJobClientFactory = PowerMockito.mock(PerJobClientFactory.class);
		when(perJobClientFactory.createPerJobClusterDescriptor(any(JobClient.class)))
				.thenReturn(yarnClusterDescriptor);
		MemberModifier.field(FlinkClusterClientManager.class, "perJobClientFactory")
				.set(flinkClusterClientManager, perJobClientFactory);

		AbstractYarnClusterDescriptor descriptor = PowerMockito.mock(AbstractYarnClusterDescriptor.class);
		when(descriptor.retrieve(any())).thenReturn(PowerMockito.mock(ClusterClient.class));

		ClusterClient clusterClient = flinkClusterClientManager.getClusterClient(jobIdentifier);
		Assert.assertNotNull(clusterClient);
	}

	@Test
	public void testAddClient() {
		String applicationId = "application_1593762151957_0080";

		flinkClusterClientManager.addClient(applicationId, clusterClient);
	}

	@Test
	public void testCreateWithInit() throws Exception {
		FlinkConfig flinkConfig = new FlinkConfig();
		flinkConfig.setClusterMode("yarn");
		when(flinkClientBuilder.getFlinkConfig()).thenReturn(flinkConfig);
		AbstractYarnClusterDescriptor descriptor = PowerMockito.mock(AbstractYarnClusterDescriptor.class);

		when(flinkClientBuilder.getFlinkConfiguration()).thenReturn(new Configuration());

		CuratorFrameworkFactory.Builder builder = PowerMockito.mock(CuratorFrameworkFactory.Builder.class);
		when(builder.connectionTimeoutMs(any(int.class))).thenReturn(builder);
		when(builder.connectString(any(String.class))).thenReturn(builder);
		when(builder.retryPolicy(any(RetryPolicy.class))).thenReturn(builder);
		when(builder.sessionTimeoutMs(any(int.class))).thenReturn(builder);
		when(builder.build()).thenReturn(PowerMockito.mock(CuratorFramework.class));

		PowerMockito.mockStatic(CuratorFrameworkFactory.class);
		when(CuratorFrameworkFactory.builder()).thenReturn(builder);

		FlinkClusterClientManager clusterClientManager = FlinkClusterClientManager.createWithInit(flinkClientBuilder);
		Assert.assertNotNull(clusterClientManager);
	}

}
