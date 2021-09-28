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

package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.dao.ClusterDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 6:02 下午 2020/11/13
 */
public class TestJobRestartDealer extends AbstractTest {

    @Autowired
    private JobRestartDealer jobRestartDealer;

    @Autowired
    private ClusterDao clusterDao;


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestartForSubmitResult() throws Exception {

        JobClient jobClient = CommonUtils.getJobClient();
        addDefaultCluster();
        jobRestartDealer.checkAndRestartForSubmitResult(jobClient);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart1() {
        addDefaultCluster();
        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        boolean flag = jobRestartDealer.checkAndRestart(2, scheduleJob, jobCache);
        Assert.assertFalse(flag);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart2() {
        addDefaultCluster();
        EngineJobCache jobCache = DataCollection.getData().getEngineJobCache();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        boolean flag2 = jobRestartDealer.checkAndRestart(8, scheduleJob,jobCache );
        Assert.assertFalse(flag2);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart3() {
        addDefaultCluster();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        EngineJobCache jobCache4 = DataCollection.getData().getEngineJobCache4();
        boolean flag3 = jobRestartDealer.checkAndRestart(8, scheduleJob, jobCache4);
    }



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckAndRestart4() {

        //engineType为kylin
        addDefaultCluster();
        ScheduleJob scheduleJob = DataCollection.getData().getScheduleJobDefiniteJobId();
        EngineJobCache jobCache4 = DataCollection.getData().getEngineJobCache5();
        boolean flag3 = jobRestartDealer.checkAndRestart(8, scheduleJob, jobCache4);
        Assert.assertFalse(flag3);

    }




    private Cluster addDefaultCluster(){

        Cluster cluster = new Cluster();
        cluster.setId(-1L);
        cluster.setClusterName("defalut");
        cluster.setHadoopVersion("");
        Integer insert = clusterDao.insertWithId(cluster);
        return cluster;
    }
}
