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

import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsoleClusterServiceTest {

    private ConsoleClusterService consoleClusterService;

    private ClusterMapper clusterMapper;

    @Before
    public void setUp() {
        consoleClusterService = new ConsoleClusterService();
        clusterMapper = mock(ClusterMapper.class);
        ReflectionTestUtils.setField(consoleClusterService, "clusterMapper", clusterMapper);
    }

    @Test
    public void testAddCluster() {
        when(clusterMapper.getByClusterName("cluster_a")).thenReturn(null);
        doAnswer(invocation -> {
            Cluster cluster = invocation.getArgumentAt(0, Cluster.class);
            cluster.setId(1L);
            return 1;
        }).when(clusterMapper).insert(any(Cluster.class));

        Long clusterId = consoleClusterService.addCluster("cluster_a");

        Assert.assertEquals(Long.valueOf(1L), clusterId);
        verify(clusterMapper).insert(any(Cluster.class));
    }

    @Test(expected = TaierDefineException.class)
    public void testAddClusterRejectPathTraversalName() {
        try {
            consoleClusterService.addCluster("../../../../tmp/x");
        } finally {
            verify(clusterMapper, never()).getByClusterName(any(String.class));
            verify(clusterMapper, never()).insert(any(Cluster.class));
        }
    }
}
