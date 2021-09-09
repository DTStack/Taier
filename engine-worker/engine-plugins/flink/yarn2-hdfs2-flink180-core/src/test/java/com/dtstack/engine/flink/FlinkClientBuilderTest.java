package com.dtstack.engine.flink;

import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.util.HadoopUtils;
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

import java.lang.reflect.Field;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;

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
