package com.dtstack.engine.flink.constrant;


import java.io.File;

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
    public static String SP = File.separator;
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String LOG_LEVEL_KEY = "logLevel";
    public static final String FLINK_JOB_ALLOWNONRESTOREDSTATE_KEY = "allowNonRestoredState";
    public static String JVM_OPTIONS = "-XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing";

    public final static String AKKA_ASK_TIMEOUT = "50 s";
    public final static String AKKA_CLIENT_TIMEOUT = "300 s";
    public final static String AKKA_TCP_TIMEOUT = "60 s";

    // ------------------------------------------------------------------------
    // Resource Configs
    // ------------------------------------------------------------------------

    public static final String SQL_ENV_PARALLELISM = "sql.env.parallelism";
    public static final String MR_JOB_PARALLELISM = "mr.job.parallelism";
    public static final String FLINK_TASK_RUN_MODE_KEY = "flinkTaskRunMode";
    public final static String JOBMANAGER_MEMORY_MB = "jobmanager.memory.mb";
    public final static String TASKMANAGER_MEMORY_MB = "taskmanager.memory.mb";
    public final static String CONTAINER = "container";
    public final static String SLOTS = "slots";
    public final static int MIN_JM_MEMORY = 1024;
    public final static int MIN_TM_MEMORY = 1024;

    // ------------------------------------------------------------------------
    // Plugin Load Configs
    // ------------------------------------------------------------------------

    public static final String SQLPLUGIN_DIR = "sqlplugin";
    public static final String FLINKSQl_CORE_JAR_PREFIX = "core";

    public static final String SYNCPLUGIN_DIR = "syncplugin";
    public static final String FLINKX_CORE_JAR_PREFIX = "flinkx";

    public static final String DEFAULT_FLINK_PLUGIN_ROOT = "/opt/dtstack/flinkplugin";
    public final static String FLINK_PLUGIN_LOAD_MODE = "pluginLoadMode";
    public static final String FLINK_PLUGIN_CLASSPATH_LOAD = "classpath";
    public static final String FLINK_PLUGIN_SHIPFILE_LOAD = "shipfile";


    // ------------------------------------------------------------------------
    // Kerberos Configs
    // ------------------------------------------------------------------------

    public static final String KAFKA_SFTP_KEYTAB = "kafka.sftp.keytab";
    public static final String SECURITY_KERBEROS_LOGIN_KEYTAB= "security.kerberos.login.keytab";
    public static final String LOCAL_KEYTAB_TASK_DIR_PARENT = USER_DIR + "/keytab/";
    public static final String LOCAL_KEYTAB_DIR = USER_DIR + "/kerberosPath/";


    // ------------------------------------------------------------------------
    // Restful Url Format Configs
    // ------------------------------------------------------------------------

    public final static String FLINK_CP_URL_FORMAT = "/jobs/%s/checkpoints";
    public static final String TASKMANAGERS_URL_FORMAT = "%s/taskmanagers";
    public static final String JOBMANAGER_LOG_URL_FORMAT = "%s/jobmanager/log";
    public static final String TASKMANAGERS_KEY = "taskmanagers";

    public static final String YARN_APPLICATION_URL_FORMAT = "%s/ws/v1/cluster/apps/%s";
    public static final String YARN_CONTAINER_LOG_URL_FORMAT = "%s/node/containerlogs/%s/%s";

}
