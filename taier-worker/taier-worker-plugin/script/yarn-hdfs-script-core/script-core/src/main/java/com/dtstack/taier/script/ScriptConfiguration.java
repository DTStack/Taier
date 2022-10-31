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

package com.dtstack.taier.script;

import org.apache.hadoop.conf.Configuration;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-16 19:19
 */
public class ScriptConfiguration extends Configuration {
    public static final String CONTAINER_STAGING_DIR = "container.staging.dir";

    public static final String SCRIPT_APP_ENV = "script.app.env";

    public static final String DEFAULT_CONTAINER_STAGING_DIR = "/insight/script/staging";

    public static final String SCRIPT_CMD_OPTS = "script.cmd.opts";

    public static final String SCRIPT_RUNNING_APPLICATIONID = "script.running.applicationsId";

    public static final String SCRIPT_LOCALFILE = "script.localFile";

    public static final String SCRIPT_USER_CLASSPATH_FIRST = "script.user.classpath.first";

    public static final boolean DEFAULT_SCRIPT_USER_CLASSPATH_FIRST = true;

    public static final String SCRIPT_AM_MEMORY = "script.am.memory";

    public static final int DEFAULT_SCRIPT_AM_MEMORY = 512;

    public static final String SCRIPT_AM_CORES = "script.am.cores";

    public static final int DEFAULT_SCRIPT_AM_CORES = 1;

    public static final String SCRIPT_MAX_WORKER_MEMORY = "script.worker.memory";

    public static final int DEFAULT_SCRIPT_MAX_WORKER_MEMORY = 20000;

    public static final String SCRIPT_WORKER_MEM_AUTO_SCALE = "script.worker.mem.autoscale";

    public static final Double DEFAULT_SCRIPT_WORKER_MEM_AUTO_SCALE = 0.5;

    public static final String SCRIPT_WORKER_NODES = "script.worker.nodes";

    public static final String SCRIPT_WORKER_RACKS = "script.worker.racks";

    public static final String SCRIPT_WORKER_MEMORY = "script.worker.memory";

    public static final int DEFAULT_SCRIPT_WORKER_MEMORY = 512;

    public static final String SCRIPT_WORKER_CORES = "script.worker.cores";

    public static final int DEFAULT_SCRIPT_WORKER_CORES = 1;

    public static final String SCRIPT_WORKER_GPU = "script.worker.gcores";

    public static final int DEFAULT_SCRIPT_WORKER_GPU = 0;

    public static final String SCRIPT_WORKER_NUM = "script.worker.num";

    public static final int DEFAULT_SCRIPT_WORKER_NUM = 1;

    public static final String SCRIPT_LOGLEVEL = "script.logLevel";

    public static final String DEFAULT_SCRIPT_LOGLEVEL = "INFO";

    public static final String SCRIPT_LAUNCH_CMD = "script.launch.cmd";

    public static final String SCRIPT_FILES = "script.files";

    public static final String SCRIPT_CACHEFILES = "script.cacheFiles";

    public static final String SCRIPT_APP_NAME = "script.app.name";

    public static final String SCRIPT_APP_TYPE = "script.app.type";

    public static final String SCRIPT_OUTPUTS = "script.outputs";

    public static final String SCRIPT_INPUTS = "script.inputs";

    public static final String HADOOP_HOME_DIR = "hadoop.home.dir";

    public static final String APP_YARN_ACCEPTER_TASK_NUMBER = "yarnAccepterTaskNumber";

    public static final String APP_QUEUE = "queue";

    public static final String SCRIPT_SHIP_FILES = "script.ship-files";

    public static final String DEFAULT_APP_QUEUE = "default";

    public static final String APP_PRIORITY = "yarn.app.priority";

    public static final int DEFAULT_SCRIPT_APP_PRIORITY = 3;

    public static final String APP_MAX_ATTEMPTS = "app.maxattempts";

    public static final int DEFAULT_APP_MAX_ATTEMPTS = 3;

    public static final String INTERNAL_APPLICATION_OUTPUTFILES = "internal.application.outputfiles";

    public static final String INTERNAL_APPLICATION_OUTPUT_PARENTDIR = "internal.application.output.parentdir";

    public static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";

    public static final String[] DEFAULT_SCRIPT_APPLICATION_CLASSPATH = {
            "$HADOOP_CONF_DIR",
            "$HADOOP_HOME/share/hadoop/common/*",
            "$HADOOP_HOME/share/hadoop/common/lib/*",
            "$HADOOP_HOME/share/hadoop/hdfs/*",
            "$HADOOP_HOME/share/hadoop/hdfs/lib/*",
            "$HADOOP_HOME/share/hadoop/yarn/*",
            "$HADOOP_HOME/share/hadoop/yarn/lib/*",
            "$HADOOP_HOME/share/hadoop/mapreduce/*",
            "$HADOOP_HOME/share/hadoop/mapreduce/lib/*"
    };

    /**
     *  Python configuration
     */
    public static final String SCRIPT_PYTHON2_PATH = "script.python2.path";
    public static final String SCRIPT_PYTHON3_PATH = "script.python3.path";

    public static final String SCRIPT_APPMASTERJAR_PATH = "AppMaster.jar";
    public static final String SCRIPT_LOG4J_FILENAME = "log4j.properties";
    public static final String SCRIPT_HAS_LOG4J = "script.has.log4j";

    public static final String SCRIPT_CONTAINER_HEARTBEAT_INTERVAL = "script.container.heartbeat.interval";

    public static final int DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_INTERVAL = 10 * 1000;

    public static final String SCRIPT_CONTAINER_HEARTBEAT_TIMEOUT = "script.container.heartbeat.timeout";

    public static final long DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_TIMEOUT = 2 * 60 * 1000L;

    public static final String SCRIPT_LOCALRESOURCE_TIMEOUT = "script.localresource.timeout";

    public static final int DEFAULT_SCRIPT_LOCALRESOURCE_TIMEOUT = 5 * 60 * 1000;

    public static final String SCRIPT_CONTAINER_HEARTBEAT_RETRY = "script.container.heartbeat.retry";

    public static final int DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_RETRY = 3;

    public static final String DEFAULT_SCRIPT_APPMASTER_EXTRA_JAVA_OPTS = "";

    public static final String SCRIPT_JAVA_OPTS = "script.java.opts";

    public static final String DEFAULT_SCRIPT_CONTAINER_EXTRA_JAVA_OPTS = "";

    public static final String SCRIPT_ALLOCATE_INTERVAL = "script.allocate.interval";

    public static final int DEFAULT_SCRIPT_ALLOCATE_INTERVAL = 1000;

    public static final String SCRIPT_CONTAINER_MAX_FAILURES_RATE = "script.container.maxFailures.rate";

    public static final double DEFAULT_SCRIPT_CONTAINER_MAX_FAILURES_RATE = 1;

    public static final String SCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM = "asyncCheckYarnClientThreadNum";

    public static final int DEFAULT_SCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM = 3;

    /**
     * am在向rm申请资源时，在同一个nm上不能申请2次以上; app独占nm
     */
    public static final String SCRIPT_WORKER_EXCLUSIVE = "script.worker.exclusive";

    public static final boolean DEFAULT_SCRIPT_WORKER_EXCLUSIVE = false;

    public static final String UTF8 = "UTF-8";

    public static final String NODE_LABEL = "nodeLabel";

    public static final String JAVA_PATH = "java.path";

    public static final String DEFAULT_LOG4J_CONTENT =
            "log4j.rootLogger=INFO,Client\n"
                    + "log4j.logger.Client=INFO,Client\n"
                    + "log4j.additivity.Client = false\n"
                    + "log4j.appender.Client=org.apache.log4j.RollingFileAppender\n"
                    + "log4j.appender.Client.File=${log.file}\n"
                    + "log4j.appender.Client.MaxFileSize=5MB\n"
                    + "log4j.appender.Client.MaxBackupIndex=10\n"
                    + "log4j.appender.Client.layout=org.apache.log4j.PatternLayout\n"
                    + "log4j.appender.Client.layout.ConversionPattern=%d{yyyy-MM-dd HH\\:mm\\:ss,SSS} %-5p %-60c %x - %m%n";

    public ScriptConfiguration() {
    }

    public ScriptConfiguration(boolean loadDefaults) {
        super(loadDefaults);
    }
}