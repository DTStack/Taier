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

import com.dtstack.taier.base.enums.ClassLoaderType;
import com.dtstack.taier.base.filesystem.FilesystemManager;
import com.dtstack.taier.flink.config.PluginConfig;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.pluginapi.JarFileInfo;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.dtstack.taier.pluginapi.loader.DtClassLoader;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.JarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.URLClassPath;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkUtil {

    static final Logger logger = LoggerFactory.getLogger(FlinkUtil.class);

    /**
     * 数据样例
     * {
     * "taskmanagers": [
     * {
     * "id": "ac1e2d5668eb1e908e15e3d40f8b67d6",
     * "path": "akka.tcp://flink@node01:52079/user/taskmanager",
     * "dataPort": 37512,
     * "timeSinceLastHeartbeat": 1508393749742,
     * "slotsNumber": 4,
     * "freeSlots": 4,
     * "cpuCores": 4,
     * "physicalMemory": 8254550016,
     * "freeMemory": 1073741824,
     * "managedMemory": 670946944
     * }]}
     */
    public final static String SLOTS_INFO = "/taskmanagers";

    /**
     * 数据样例
     * {
     * "root-exception": "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job. You can decrease the operator parallelism or increase the number of slots per TaskManager in the configuration. Task to schedule: < Attempt #0 (Source: mysqlreader (3/4)) @ (unassigned) - [SCHEDULED] > with groupID < bc764cd8ddf7a0cff126f51c16239658 > in sharing group < SlotSharingGroup [bc764cd8ddf7a0cff126f51c16239658, 20ba6b65f97481d5570070de90e4e791] >. Resources available to scheduler: Number of instances=1, total number of slots=10, available slots=0\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.scheduleTask(Scheduler.java:262)\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.allocateSlot(Scheduler.java:139)\n\tat org.apache.flink.runtime.executiongraph.Execution.allocateSlotForExecution(Execution.java:368)\n\tat org.apache.flink.runtime.executiongraph.ExecutionJobVertex.allocateResourcesForAll(ExecutionJobVertex.java:478)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleEager(ExecutionGraph.java:865)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleForExecution(ExecutionGraph.java:816)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply$mcV$sp(JobManager.scala:1425)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.liftedTree1$1(Future.scala:24)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.run(Future.scala:24)\n\tat akka.dispatch.TaskInvocation.run(AbstractDispatcher.scala:40)\n\tat akka.dispatch.ForkJoinExecutorConfigurator$AkkaForkJoinTask.exec(AbstractDispatcher.scala:397)\n\tat scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)\n\tat scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)\n\tat scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)\n\tat scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)\n",
     * "all-exceptions": [],
     * "truncated": false
     * }
     */
    public final static String EXCEPTION_INFO = "/jobs/%s/exceptions";

    /**
     * set jobmanager.memory.process.size、taskmanager.memory.process.size from job properties.
     * if job properties is not set, use environment properties.
     */
    public static ClusterSpecification createClusterSpecification(Configuration configuration, int priority, Properties confProperties, Properties envProperties) {
        MemorySize jobManagerMemorySize;
        MemorySize taskManagerMemorySize;
        int numberOfTaskSlots;
        int parallelism;

        if (confProperties != null) {
            if (confProperties.containsKey(JobManagerOptions.TOTAL_PROCESS_MEMORY.key())) {
                jobManagerMemorySize = MemorySize.parse(confProperties.getProperty(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), ConfigConstant.DEFAULT_JOBMANAGER_MEMORY));
            } else {
                jobManagerMemorySize = MemorySize.parse(envProperties.getProperty(JobManagerOptions.TOTAL_PROCESS_MEMORY.key(), ConfigConstant.DEFAULT_JOBMANAGER_MEMORY));
            }
            if (confProperties.containsKey(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key())) {
                taskManagerMemorySize = MemorySize.parse(confProperties.getProperty(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), ConfigConstant.DEFAULT_TASKMANAGER_MEMORY));
            } else {
                taskManagerMemorySize = MemorySize.parse(envProperties.getProperty(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key(), ConfigConstant.DEFAULT_TASKMANAGER_MEMORY));
            }
            numberOfTaskSlots = confProperties.containsKey(TaskManagerOptions.NUM_TASK_SLOTS.key()) ?
                    MathUtil.getIntegerVal(confProperties.get(TaskManagerOptions.NUM_TASK_SLOTS.key())) :
                    MathUtil.getIntegerVal(envProperties.get(TaskManagerOptions.NUM_TASK_SLOTS.key()), TaskManagerOptions.NUM_TASK_SLOTS.defaultValue());
            parallelism = Math.max(
                    FlinkUtil.getEnvParallelism(confProperties),
                    FlinkUtil.getJobParallelism(confProperties));
        } else {
            jobManagerMemorySize = configuration.get(JobManagerOptions.TOTAL_PROCESS_MEMORY);
            taskManagerMemorySize = configuration.get(JobManagerOptions.TOTAL_PROCESS_MEMORY);
            numberOfTaskSlots = configuration.getInteger(TaskManagerOptions.NUM_TASK_SLOTS);
            parallelism = configuration.getInteger(CoreOptions.DEFAULT_PARALLELISM);
        }

        configuration.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, jobManagerMemorySize);
        configuration.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, taskManagerMemorySize);
        configuration.set(TaskManagerOptions.NUM_TASK_SLOTS, numberOfTaskSlots);

        return ClusterSpecification.newInstance(
                jobManagerMemorySize.getMebiBytes(),
                jobManagerMemorySize.getMebiBytes(),
                numberOfTaskSlots,
                parallelism,
                priority);
    }

    public static PackagedProgram buildProgram(String jarPath, List<URL> classPaths, EJobType jobType,
                                               String entryPointClass, String[] programArgs,
                                               SavepointRestoreSettings spSetting,
                                               org.apache.flink.configuration.Configuration flinkConfiguration,
                                               FilesystemManager filesystemManager)
            throws IOException, ProgramInvocationException {
        if (jarPath == null) {
            throw new IllegalArgumentException("The program JAR file was not specified.");
        }
        File jarFile = new File(jarPath);

        org.apache.flink.configuration.Configuration flinkConfig = new org.apache.flink.configuration.Configuration(flinkConfiguration);
        String classloaderCache = flinkConfig.getString(
                ClassLoaderType.CLASSLOADER_DTSTACK_CACHE,
                ClassLoaderType.CLASSLOADER_DTSTACK_CACHE_TRUE);
        flinkConfig.setString(ClassLoaderType.CLASSLOADER_DTSTACK_CACHE, classloaderCache);

        // 指定采用parent ClassLoader优先加载的类
        String append = flinkConfig.getString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL);
        if (jobType == EJobType.SQL || jobType == EJobType.SYNC) {
            //String dtstackAppend = "com.fasterxml.jackson.";
            String dtstackAppend = ConfigConstant.PARENT_FIRST_LOADER_PATTERNS_DEFAULT;
            if (StringUtils.isNotEmpty(append)) {
                dtstackAppend = dtstackAppend + ";" + append;
            }
            flinkConfig.setString(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL, dtstackAppend);
        }

        PackagedProgram program = PackagedProgram.newBuilder()
                .setJarFile(jarFile)
                .setUserClassPaths(classPaths)
                .setEntryPointClassName(entryPointClass)
                .setConfiguration(flinkConfig)
                .setArguments(programArgs)
                .setSavepointRestoreSettings(spSetting)
                .build();

        return program;
    }

    /**
     * 将远程文件下载到本地
     */
    public static File downloadJar(String remotePath, String localDir, FilesystemManager filesystemManager, boolean localPriority) throws IOException {
        if (localPriority) {
            //如果不是http 或者 hdfs协议的从本地读取
            File localFile = new File(remotePath);
            if (localFile.exists()) {
                return localFile;
            }
        }

        String localJarPath = FlinkUtil.getTmpFileName(remotePath, localDir);
        File downloadFile = filesystemManager.downloadFile(remotePath, localJarPath);
        logger.info("downloadFile remotePath:{} localJarPath:{}", remotePath, localJarPath);

        URL jarFileUrl;

        try {
            jarFileUrl = downloadFile.getAbsoluteFile().toURI().toURL();
        } catch (MalformedURLException e1) {
            throw new IllegalArgumentException("The jar file path is invalid.");
        }

        JarUtils.checkJarFile(jarFileUrl);

        return downloadFile;
    }

    private static String getTmpFileName(String fileUrl, String toPath) {
        String fileName = StringUtils.substringAfterLast(fileUrl, File.separator);
        String tmpFileName = toPath + File.separator + fileName;
        return tmpFileName;
    }

    /**
     * FIXME 仅针对sql执行方式,暂时未找到区分设置source,transform,sink 并行度的方式
     * 设置job运行的并行度
     */
    public static int getEnvParallelism(Properties properties) {
        String parallelismStr = properties.getProperty(ConfigConstant.SQL_ENV_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr) ? Integer.parseInt(parallelismStr) : 1;
    }


    /**
     * 针对MR类型整个job的并发度设置
     */
    public static int getJobParallelism(Properties properties) {
        String parallelismStr = properties.getProperty(ConfigConstant.MR_JOB_PARALLELISM);
        return StringUtils.isNotBlank(parallelismStr) ? Integer.parseInt(parallelismStr) : 1;
    }

    /**
     * todo: make sure the usage of this method
     */
    public static String getTaskWorkspace(String home) {
        return String.format("%s/%s_%s",
                ConfigConstant.TMP_DIR, home, Thread.currentThread().getId());
    }

    public static SavepointRestoreSettings buildSavepointSetting(JobClient jobClient) {

        if (jobClient.getExternalPath() == null) {
            return SavepointRestoreSettings.none();
        }

        String externalPath = jobClient.getExternalPath();
        boolean allowNonRestoredState = false;
        if (jobClient.getConfProperties().containsKey(ConfigConstant.FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY)) {
            String allowNonRestored = (String) jobClient.getConfProperties().get(ConfigConstant.FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY);
            allowNonRestoredState = BooleanUtils.toBoolean(allowNonRestored);
        }

        return SavepointRestoreSettings.forPath(externalPath, allowNonRestoredState);
    }

    /**
     * add flinkX core jar to dtClassLoader classPath
     */
    public static void fillFlinkxToClassLoader(PluginConfig pluginConfig) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        DtClassLoader ucl;
        if (cl instanceof URLClassLoader) {
            ucl = (DtClassLoader) cl;
            JarFileInfo coreJarInfo = pluginConfig.getCoreJarInfo();
            File flinkxCorefile = new File(coreJarInfo.getJarPath());

            URL[] oldUrls = ucl.getURLs();
            URL[] coreUrl = {flinkxCorefile.toURI().toURL()};
            URL[] newUrls = Arrays.copyOf(coreUrl, oldUrls.length + coreUrl.length);
            System.arraycopy(oldUrls, 0, newUrls, coreUrl.length, oldUrls.length);

            Class<URLClassLoader> uclz = URLClassLoader.class;
            Field ucpField = uclz.getDeclaredField("ucp");
            ucpField.setAccessible(true);

            URLClassPath urlClassPath = new URLClassPath(newUrls);
            ucpField.set(ucl, urlClassPath);
        }
    }

    /**
     * parse exception log, log structure like：
     * {
     * "root-exception": "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job. You can decrease the operator parallelism or increase the number of slots per TaskManager in the configuration. Task to schedule: < Attempt #0 (Source: mysqlreader (3/4)) @ (unassigned) - [SCHEDULED] > with groupID < bc764cd8ddf7a0cff126f51c16239658 > in sharing group < SlotSharingGroup [bc764cd8ddf7a0cff126f51c16239658, 20ba6b65f97481d5570070de90e4e791] >. Resources available to scheduler: Number of instances=1, total number of slots=10, available slots=0\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.scheduleTask(Scheduler.java:262)\n\tat org.apache.flink.runtime.jobmanager.scheduler.Scheduler.allocateSlot(Scheduler.java:139)\n\tat org.apache.flink.runtime.executiongraph.Execution.allocateSlotForExecution(Execution.java:368)\n\tat org.apache.flink.runtime.executiongraph.ExecutionJobVertex.allocateResourcesForAll(ExecutionJobVertex.java:478)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleEager(ExecutionGraph.java:865)\n\tat org.apache.flink.runtime.executiongraph.ExecutionGraph.scheduleForExecution(ExecutionGraph.java:816)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply$mcV$sp(JobManager.scala:1425)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat org.apache.flink.runtime.jobmanager.JobManager$$anonfun$org$apache$flink$runtime$jobmanager$JobManager$$submitJob$1.apply(JobManager.scala:1372)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.liftedTree1$1(Future.scala:24)\n\tat scala.concurrent.impl.Future$PromiseCompletingRunnable.run(Future.scala:24)\n\tat akka.dispatch.TaskInvocation.run(AbstractDispatcher.scala:40)\n\tat akka.dispatch.ForkJoinExecutorConfigurator$AkkaForkJoinTask.exec(AbstractDispatcher.scala:397)\n\tat scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)\n\tat scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)\n\tat scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)\n\tat scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)\n",
     * "all-exceptions": [],
     * "truncated": false
     * }
     */
    public static String parseEngineLog(String exception) {
        try {
            if (StringUtils.isNotBlank(exception)) {
                // todo: 为什么转成Map以后又转成String
                Map<String, Object> logMap = PublicUtil.jsonStrToObject(exception, Map.class);
                return PublicUtil.objToString(logMap);
            } else {
                throw new PluginDefineException("no engineLog provided");
            }
        } catch (Exception e) {
            throw new PluginDefineException("parseEngineLog error", e);
        }
    }


}