package com.dtstack.engine.flink.constrant;


import sun.security.action.GetPropertyAction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.security.AccessController.doPrivileged;

/**
 * 
 * @author sishu.yss
 *
 */
public class ConfigConstrant {

    // ------------------------------------------------------------------------
    // General Configs
    // ------------------------------------------------------------------------

    public static final String SPLIT = "_";
    public static final String SP = File.separator;
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final Path IO_TMPDIR = Paths.get(doPrivileged(new GetPropertyAction("java.io.tmpdir")));

    public static final String LOG_LEVEL_KEY = "logLevel";
    public static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";
    public static final String JVM_OPTIONS = "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing";

    public static final  String AKKA_ASK_TIMEOUT = "50 s";
    public static final  String AKKA_CLIENT_TIMEOUT = "300 s";
    public static final  String AKKA_TCP_TIMEOUT = "60 s";

    public static final String JOBMANAGER_COMPONEN = "jobmanager";
    public static final String TASKMANAGER_COMPONEN = "taskmanager";

    // ------------------------------------------------------------------------
    // Resource Configs
    // ------------------------------------------------------------------------

    public static final String SQL_ENV_PARALLELISM = "sql.env.parallelism";
    public static final String MR_JOB_PARALLELISM = "mr.job.parallelism";
    public static final String FLINK_TASK_RUN_MODE_KEY = "flinkTaskRunMode";
    public static final String JOBMANAGER_MEMORY_MB = "jobmanager.memory.mb";
    public static final String TASKMANAGER_MEMORY_MB = "taskmanager.memory.mb";
    public static final String CONTAINER = "container";
    public static final String SLOTS = "slots";
    public static final int MIN_JM_MEMORY = 1024;
    public static final int MIN_TM_MEMORY = 1024;

    // ------------------------------------------------------------------------
    // Plugin Load Configs
    // ------------------------------------------------------------------------

    public static final String SQLPLUGIN_DIR = "sqlplugin";
    public static final String FLINKSQl_CORE_JAR_PREFIX = "core";

    public static final String SYNCPLUGIN_DIR = "syncplugin";
    public static final String FLINKX_CORE_JAR_PREFIX = "flinkx";

    public static final String DEFAULT_FLINK_PLUGIN_ROOT = "/opt/dtstack/flinkplugin";
    public static final String FLINK_PLUGIN_LOAD_MODE = "pluginLoadMode";

    public static final String FLINK_PLUGIN_CLASSPATH_LOAD = "classpath";
    public static final String FLINK_PLUGIN_SHIPFILE_LOAD = "shipfile";


    // ------------------------------------------------------------------------
    // Kerberos Configs
    // ------------------------------------------------------------------------

    public static final String KAFKA_SFTP_KEYTAB = "kafka.sftp.keytab";
    public static final String SECURITY_KERBEROS_LOGIN_KEYTAB= "security.kerberos.login.keytab";
    public static final String LOCAL_KEYTAB_DIR_PARENT = USER_DIR + "/kerberos/keytab";


    // ------------------------------------------------------------------------
    // Restful Url Format Configs
    // ------------------------------------------------------------------------

    public static final int HTTP_MAX_RETRY = 3;
    public static final String HTTP_AUTHENTICATION_TOKEN_KEY = "http.authentication.token";
    public static final String FLINK_CP_URL_FORMAT = "/jobs/%s/checkpoints";
    public static final String TASKMANAGERS_URL_FORMAT = "%s/taskmanagers";
    public static final String JOBMANAGER_LOG_URL_FORMAT = "%s/jobmanager/log";
    public static final String TASKMANAGERS_KEY = "taskmanagers";

    public static final String YARN_APPLICATION_URL_FORMAT = "%s/ws/v1/cluster/apps/%s";
    public static final String YARN_CONTAINER_LOG_URL_FORMAT = "%s/node/containerlogs/%s/%s";

}
