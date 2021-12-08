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

package com.dtstack.engine.flink;

import com.dtstack.engine.pluginapi.JarFileInfo;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.enums.ComputeType;
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

import java.util.ArrayList;
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
	public void testCreate() {
		FlinkConfig flinkConfig = new FlinkConfig();
		flinkConfig.setFlinkPluginRoot("root");
		flinkConfig.setRemotePluginRootDir("remote");
		SyncPluginInfo.create(flinkConfig);
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

	@Test
	public void testGetClassPaths() throws Exception {
		List<String> programArgList = new ArrayList<>();
		programArgList.add("-job");
		programArgList.add("test");

		MemberModifier.field(SyncPluginInfo.class, "flinkRemoteSyncPluginRoot").set(syncPluginInfo, "pluginPath");
		syncPluginInfo.getClassPaths(programArgList);
	}
}
