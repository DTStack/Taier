package com.dtstack.engine.flink;

import com.dtstack.engine.common.JobClient;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.MiniClusterClient;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.HighAvailabilityOptions;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.jobmanager.HighAvailabilityMode;
import org.apache.flink.runtime.minicluster.MiniCluster;
import org.apache.flink.runtime.minicluster.MiniClusterConfiguration;
import org.apache.flink.runtime.util.HadoopUtils;
import org.apache.flink.runtime.util.LeaderConnectionInfo;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationResourceUsageReportPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
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

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/7/6
 * Company: www.dtstack.com
 * @author xiuzhu
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({YarnClient.class, HadoopUtils.class, FlinkClientBuilder.class,
	StandaloneClusterDescriptor.class, AkkaUtils.class})
@PowerMockIgnore("javax.net.ssl.*")
public class FlinkClientBuilderTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Mock
	YarnClient yarnClient;

	@InjectMocks
    FlinkClientBuilder flinkClientBuilder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		PowerMockito.mockStatic(HadoopUtils.class);
	}

	@Test
	public void testInitFlinkGlobalConfiguration() throws Exception {
		Properties extProp = new Properties();
		flinkClientBuilder.initFlinkGlobalConfiguration(extProp);

		Field flinkConfigurationField = flinkClientBuilder.getClass().getDeclaredField("flinkConfiguration");
		flinkConfigurationField.setAccessible(true);
		Assert.assertNotNull(flinkConfigurationField.get(flinkClientBuilder));
	}

	@Test
	public void testGetFlinkConfiguration() throws Exception {
		Configuration configuration = new Configuration();
		MemberModifier.field(FlinkClientBuilder.class, "flinkConfiguration")
			.set(flinkClientBuilder, configuration);
		Configuration flinkConfiguration = flinkClientBuilder.getFlinkConfiguration();
		Assert.assertEquals(configuration, flinkConfiguration);
	}

	@Test
	public void testGetYarnClient() throws Exception {
		MemberModifier.field(FlinkClientBuilder.class, "yarnClient")
			.set(flinkClientBuilder, yarnClient);
		YarnClient yarnClientRes = flinkClientBuilder.getYarnClient();
		Assert.assertEquals(yarnClientRes, yarnClient);
	}



}
