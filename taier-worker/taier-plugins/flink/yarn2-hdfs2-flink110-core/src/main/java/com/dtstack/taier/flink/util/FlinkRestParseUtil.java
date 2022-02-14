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

package com.dtstack.taier.flink.util;

import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * flink restful 返回数据解析
 * Date: 2017/11/23
 * Company: www.dtstack.com
 * @author xuchao
 */

public class FlinkRestParseUtil {

    private static final Logger logger = LoggerFactory.getLogger(FlinkRestParseUtil.class);


    /**
     * 数据样例
     {
     "taskmanagers": [
     {
     "id": "ac1e2d5668eb1e908e15e3d40f8b67d6",
     "path": "akka.tcp://flink@node01:52079/user/taskmanager",
     "dataPort": 37512,
     "timeSinceLastHeartbeat": 1508393749742,
     "slotsNumber": 4,
     "freeSlots": 4,
     "cpuCores": 4,
     "physicalMemory": 8254550016,
     "freeMemory": 1073741824,
     "managedMemory": 670946944
     }]}
     */
    public final static String SLOTS_INFO = "/taskmanagers";

    /**
     * 数据样例
     {
     "root-exception": "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job. You can decrease the operator parallelism or increase the number of slots per TaskManager in the configuration. Task to schedule: < Attempt #0 (Source: mysqlreader (3/4)) @ (unassigned) - [SCHEDULED] > with groupID < bc764cd8ddf7a0cff126f51c16239658 > in sharing group < SlotSharingGroup [bc764cd8ddf7a0cff126f51c16239658, 20ba6b65f97481d5570070de90e4e791] >. Resources available to scheduler: Number of instances=1, total number of slots=10, available slots=0\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.scheduleTask(Scheduler.java:262)\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.allocateSlot(Scheduler.java:139)\n\tat org.apache.flink.runtime.executiongraph.Execution.allocateSlotForExecution(Execution.java:368)\n\tat org.apache.flink.runtime.executiongraph.ExecutionJobVertex.allocateResourcesForAll(ExecutionJobVertex.java:478)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleEager(ExecutionGraph.java:865)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleForExecution(ExecutionGraph.java:816)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply$mcV$sp(JobManager.scala:1425)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.liftedTree1$1(Future.scala:24)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.run(Future.scala:24)\n\tat akka.dispatch.TaskInvocation.run(AbstractDispatcher.scala:40)\n\tat akka.dispatch.ForkJoinExecutorConfigurator$AkkaForkJoinTask.exec(AbstractDispatcher.scala:397)\n\tat scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)\n\tat scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)\n\tat scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)\n\tat scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)\n",
     "all-exceptions": [],
     "truncated": false
     }
     */
    public final static String EXCEPTION_INFO = "/jobs/%s/exceptions";

    public static String parseEngineLog(String except) {
        try {
            if(StringUtils.isNotBlank(except)) {
                Map<String,Object> logMap = new HashMap<>();
                Map<String,Object> exceptMap = PublicUtil.jsonStrToObject(except, Map.class);
                logMap.putAll(exceptMap);
                return PublicUtil.objToString(logMap);
            }
        } catch (Exception e) {
            logger.error("parseEngineLog error", e);
        }
        throw new PluginDefineException("parseEngineLog error");
    }

}
