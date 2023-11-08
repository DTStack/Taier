package com.dtstack.taier.spark.common.constant;

public class ConfigConstant {

    public static final String HIVE_METASTORE_URIS = "hive.metastore.uris";

    public static final String KEY_DEFAULT_FILE_FORMAT = "hive.default.fileformat";

    public static final String SPARK_KERBEROS_REMOTE_KRB5 = "spark.kerberos.remotekrb5";

    public static final String SPARK_KERBEROS_REMOTE_KEYTAB = "spark.kerberos.remotekeytab";

    public static final String SPARK_HADOOP_CONF_REMOTE_DIR = "spark.hadoopconf.remotedir";

    public static final String SPARK_CLEAR_RESOURCED_RATE = "spark.clear.resource.rate";

    public static final String SPARK_RESOURCES_DIR = "spark.resources.dir";

    public static final String SPARK_DRIVER_EXTRA_JAVA_OPTIONS = "spark.driver.extraJavaOptions";

    public static final String SPARK_EXECUTOR_EXTRA_JAVA_OPTIONS =
            "spark.executor.extraJavaOptions";

    public static final String DRIVER_CORE_KEY = "driver.cores";

    public static final String DRIVER_MEM_KEY = "driver.memory";

    public static final String DRIVER_MEM_OVERHEAD_KEY = "yarn.driver.memoryOverhead";

    public static final String EXECUTOR_INSTANCES_KEY = "executor.instances";

    public static final String EXECUTOR_MEM_KEY = "executor.memory";

    public static final String EXECUTOR_CORES_KEY = "executor.cores";

    public static final String EXECUTOR_MEM_OVERHEAD_KEY = "yarn.executor.memoryOverhead";

    public static final String SPARK_RANGER_CONF_REMOTE_DIR = "spark.ranger.conf.remote.dir";

    public static final String SPARK_RANGER_ENABLED = "spark.ranger.enabled";

    public static final String SPARK_SQL_EXTENSIONS = "spark.sql.extensions";

    public static final String HTTP_AUTHENTICATION_TOKEN_KEY = "http.authentication.token";

    public static final String SPARK_EVENT_LOG_DIR = "spark.eventLog.dir";

    public static final String SPARK_LOCAL_SPARK_HOME = "spark.local.spark.home";

    public static final String SPARK_PROMETHEUS_SINK_JAR_PATH = "spark.prometheus.sink.jar.path";

    public static final String SPARK_YARN_ARCHIVE = "spark.yarn.archive";

    public static final String SPARK_PYTHON_EXT_LIB_PATH = "spark.python.extLib.path";

    private ConfigConstant() {}
}
