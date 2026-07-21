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

package com.dtstack.taier.develop.service.console;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsoleComponentServiceTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGetLocalKerberosPathUseClusterIdInsteadOfClusterName() throws Exception {
        File tempDir = temporaryFolder.newFolder("temp");
        Long clusterId = 12L;
        Cluster cluster = new Cluster();
        cluster.setId(clusterId);
        cluster.setClusterName("../../../../tmp/x");

        ClusterMapper clusterMapper = mock(ClusterMapper.class);
        when(clusterMapper.getOne(clusterId)).thenReturn(cluster);
        EnvironmentContext environmentContext = mock(EnvironmentContext.class);
        when(environmentContext.getTempDir()).thenReturn(tempDir.getAbsolutePath());

        ConsoleComponentService consoleComponentService = new ConsoleComponentService();
        ReflectionTestUtils.setField(consoleComponentService, "clusterMapper", clusterMapper);
        ReflectionTestUtils.setField(consoleComponentService, "env", environmentContext);

        String localKerberosPath = consoleComponentService.getLocalKerberosPath(clusterId, EComponentType.HDFS.getTypeCode());
        File localKerberosDir = new File(localKerberosPath);

        Assert.assertEquals(new File(tempDir, "CLUSTER_12" + File.separator + "HDFS" + File.separator + "kerberos").getPath(),
                localKerberosPath);
        Assert.assertTrue(localKerberosDir.getCanonicalPath().startsWith(tempDir.getCanonicalPath() + File.separator));
        Assert.assertFalse(localKerberosPath.contains(".."));
        Assert.assertFalse(localKerberosPath.contains("tmp/x"));
    }
}
