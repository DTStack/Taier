package com.dtstack.engine.flink;

import com.dtstack.engine.common.JarFileInfo;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.flink.plugininfo.SyncPluginInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;

import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.when;


/**
 * Date: 2020/7/8
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class SyncPluginInfoTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@InjectMocks
	SyncPluginInfo syncPluginInfo;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateSyncPluginArgs() throws Exception {
		JobClient jobClient = PowerMockito.mock(JobClient.class);
		when(jobClient.getClassArgs()).thenReturn("args");
		when(jobClient.getComputeType()).thenReturn(ComputeType.BATCH);
		when(jobClient.getConfProperties()).thenReturn(new Properties());

		MemberModifier.field(SyncPluginInfo.class, "monitorAddress")
			.set(syncPluginInfo, "address");

		List<String> args = syncPluginInfo.createSyncPluginArgs(jobClient, null);
		Assert.assertNotNull(args);
	}

	@Test
	public void testCreateAddJarInfo() throws Exception {
		String syncPluginDir = temporaryFolder.newFolder("syncPluginDir").getAbsolutePath();
		temporaryFolder.newFile("syncPluginDir/flinkx-test.jar");

		MemberModifier.field(SyncPluginInfo.class, "localSyncFileDir")
			.set(syncPluginInfo, syncPluginDir);

		JarFileInfo jarFileInfo = syncPluginInfo.createAddJarInfo();
		Assert.assertNotNull(jarFileInfo);
	}
}
