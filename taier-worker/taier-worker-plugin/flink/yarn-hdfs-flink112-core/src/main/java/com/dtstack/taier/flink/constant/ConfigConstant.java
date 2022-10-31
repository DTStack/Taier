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


package com.dtstack.taier.flink.constant;

import org.apache.flink.configuration.ConfigOption;
import sun.security.action.GetPropertyAction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.security.AccessController.doPrivileged;
import static org.apache.flink.configuration.ConfigOptions.key;

/**
 * @program: engine-plugins
 * @author: xiuzhu
 * @create: 2021/07/15
 */

public class ConfigConstant {

    // ------------------------------------------------------------------------
    // General Configs
    // ------------------------------------------------------------------------

    public static final String SPLIT = "_";
    public static final String SP = File.separator;
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String TMP_DIR = USER_DIR + SP + "tmp";
    public static final Path IO_TMP_DIR = Paths.get(doPrivileged(new GetPropertyAction("java.io.tmpdir")));

    public static final String LOG_LEVEL_KEY = "logLevel";
    public static final String SQL_ENV_PARALLELISM = "sql.env.parallelism";
    public static final String MR_JOB_PARALLELISM = "mr.job.parallelism";
    public static final String FLINK_TASK_RUN_MODE_KEY = "flinkTaskRunMode";
    public static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";

    /**
     * akka configuration
     */
    public static final ConfigOption<String> AKKA_ASK_TIMEOUT = key("akka.ask.timeout")
            .stringType()
            .defaultValue("50 s");
    public static final ConfigOption<String> AKKA_CLIENT_TIMEOUT = key("akka.client.timeout")
            .stringType()
            .defaultValue("300 s");
    public static final ConfigOption<String> AKKA_TCP_TIMEOUT = key("akka.tcp.timeout")
            .stringType()
            .defaultValue("60 s");

    public static final String JOBMANAGER_COMPONEN = "jobmanager";
    public static final String TASKMANAGER_COMPONEN = "taskmanager";

    /**
     * session check configuration
     */
    public static final String  SESSION_CHECK_JAR_NAME= "dt-flink-check.jar";
    public static final String  SESSION_CHECK_MAIN_CLASS= "com.dtstack.taier.flink.check.FlinkSessionCheck";
    public static final String DEFAULT_SESSION_CHECK_PATH = "/worker-plugins/flinkcommon";

    public static final String HADOOP_CONF_BYTES_KEY = "hadoop.conf.bytes";
    public static final String YARN_CONF_BYTES_KEY = "yarn.conf.bytes";

    public static final String CHILD_FIRST_LOADER_PATTERNS = "classloader.child-first-patterns";
    // 指定具体child loader类的全类名，使用;号分隔
    public static final String CHILD_FIRST_LOADER_PATTERNS_DEFAULT = "org.apache.flink.table.planner.plan.QueryOperationConverter;org.apache.flink.streaming.api.graph.StreamGraph;org.apache.flink.streaming.api.functions.async.RichAsyncFunction;org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders;org.apache.flink.table.planner.plan.nodes.common.CommonLookupJoin;org.apache.flink.table.planner.plan.nodes.common.CommonPhysicalTableSourceScan";

    // prefix name of class that should be loaded by DtClassloader
    public static final String PARENT_FIRST_LOADER_PATTERNS_DEFAULT = "com.fasterxml.jackson.;com.dtstack.flinkx.util.FactoryHelper;org.codehaus.;com.dtstack.chunjun.util.FactoryHelper";

    public static final String KEY_PROMGATEWAY_JOBNAME = "metrics.reporter.promgateway.jobName";

    // ------------------------------------------------------------------------
    // Resource Configs
    // ------------------------------------------------------------------------
    public static final int MIN_JM_MEMORY = 1024;
    public static final int MIN_TM_MEMORY = 2048;

    public final static String DEFAULT_JOBMANAGER_MEMORY = "1g";

    public final static String DEFAULT_TASKMANAGER_MEMORY = "2g";

    public static final String CONTAINER = "container";

    // ------------------------------------------------------------------------
    // yarn Configs
    // ------------------------------------------------------------------------
    public static final ConfigOption<Integer> YARN_APPLICATION_ATTEMPTS =
            key("yarn.application-attempts")
                    .intType()
                    .defaultValue(3);

    public static final ConfigOption<Integer> YARN_APPLICATION_ATTEMPT_FAILURES_VALIDITY_INTERVAL =
            key("yarn.application-attempt-failures-validity-interval")
                    .intType()
                    .defaultValue(3600000);



    // ------------------------------------------------------------------------
    // Plugin Load Configs
    // ------------------------------------------------------------------------
    /**
     * flinkx core jar prefix
     */
    public static final String FLINKX_CORE_JAR_PREFIX = "flinkx-core";
    public static final String CHUNJUN_CORE_JAR_PREFIX = "chunjun-core";

    public static final String FLINKX_PLUGIN_LOAD_MODE = "pluginLoadMode";

    public static final String FLINK_PLUGIN_CLASSPATH_LOAD = "classpath";
    public static final String FLINK_PLUGIN_SHIPFILE_LOAD = "shipfile";

    // ------------------------------------------------------------------------
    // Kerberos Configs
    // ------------------------------------------------------------------------

    public static final String OPEN_KERBEROS_KEY = "openKerberos";
    public static final String ZOOKEEPER_CLIENT_APPEND_JAAS_ENABLE= "zookeeper.client.append.jaas.enable";

    // ------------------------------------------------------------------------
    // Restful Url Format Configs
    // ------------------------------------------------------------------------

    public static final int HTTP_MAX_RETRY = 3;
    public static final String HTTP_AUTHENTICATION_TOKEN_KEY = "http.authentication.token";
    public static final String TASKMANAGERS_URL_FORMAT = "%s/taskmanagers";
    public static final String JOBMANAGER_LOG_URL_FORMAT = "%s/jobmanager/log";
    public static final String JOB_EXCEPTIONS_URL_FORMAT = "/jobs/%s/exceptions";
    public final static String JOB_ACCUMULATOR_URL_FORMAT = "/jobs/%s/accumulators";
    public static final String JOB_URL_FORMAT = "/jobs/%s";
    public static final String JOB_CHECKPOINTS_URL_FORMAT = "/jobs/%s/checkpoints";
    public static final String TASKMANAGERS_KEY = "taskmanagers";

    public static final String YARN_APPLICATION_URL_FORMAT = "%s/ws/v1/cluster/apps/%s";
    public static final String YARN_CONTAINER_LOG_URL_FORMAT = "%s/node/containerlogs/%s/%s";

    // ------------------------------------------------------------------------
    // checkpoint clean Configs
    // ------------------------------------------------------------------------

    public static final String CHECKPOINT_RETAIN_TIME = "checkpoint.retain.time";
    public static final int DEFAULT_CHECKPOINT_RETAIN_TIME = 604800;

    // ------------------------------------------------------------------------
    // jar file Configs
    // ------------------------------------------------------------------------

    public static final String PREFIX_HDFS = "hdfs://";

    public static final String REMOTE_FLINK_LIB_DIR = "remoteFlinkLibDir";
    public static final String REMOTE_CHUNJUN_DIST_DIR = "remoteChunjunDistDir";
    public static final String FLINK_LIB_DIR = "flinkLibDir";
    public static final String CHUNJUN_DIST_DIR = "chunjunDistDir";
}
