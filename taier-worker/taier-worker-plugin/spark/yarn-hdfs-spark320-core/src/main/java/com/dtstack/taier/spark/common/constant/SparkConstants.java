package com.dtstack.taier.spark.common.constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SparkConstants {

    public static final String SPARK = "spark";

    public static final String SPLIT = "_";

    public static final String SP = File.separator;

    public static final String USER_DIR = System.getProperty("user.dir");

    public static final String TMP_DIR = USER_DIR + SP + "tmp";

    public static final String GRAMMAR = "grammar";

    public static final String TYPE_NAME_KEY = "typeName";

    public static final String MODEL_PARAM = "modelParam";

    public static final String APP_ENV = "--app-env";

    public static final String HDFS_PREFIX = "hdfs://";

    public static final String HTTP_PREFIX = "http://";

    public static final String KEY_DEFAULT_FILE_FORMAT = "hive.default.fileformat";

    public static final String DEFAULT_FILE_FORMAT = "orc";

    public static final String LOG_LEVEL_KEY = "logLevel";

    public static final String HADOOP_CONF = "__hadoop_conf__";

    public static final String HIVE_SITE = "/hive-site.xml";

    public static final String CORE_SITE = "/core-site.xml";

    public static final String YARN_SITE = "/yarn-site.xml";

    public static final String UDF_JAR = "udf_jar";

    public static final String IS_CARBON_SPARK_KEY = "isCarbondata";

    public static final String SESSION_CONF_KEY_PREFIX = "session.";

    public static final String SPARK_CONFIG_PREFIX = "spark.";

    public static final String PYTHON_RUNNER_CLASS = "org.apache.spark.deploy.PythonRunner";

    public static final String PYTHON_RUNNER_DEPENDENCY_RES_KEY = "extRefResource";

    public static final String SPARK_LOCAL_LOG4J_KEY = "spark_local_log4j_key";

    public static final String SPARK_CONF_DIR = "sparkconf";

    public static final String SPARK_LOG4J_FILE_NAME = "log4j-spark.properties";

    public static final int DEFAULT_CORES = 1;

    public static final int DEFAULT_INSTANCES = 1;

    public static final int DEFAULT_MEM = 512;

    public static final int DEFAULT_MEM_OVERHEAD = 384;

    public static final String DEFAULT_SPARK_YARN_ARCHIVE = "%s/sparkjars/jars";

    public static final String DEFAULT_SPARK_SQL_PROXY_JAR_PATH =
            "%s/user/spark/spark-sql-proxy.jar";

    public static final String DEFAULT_SPARK_PYTHON_EXTLIBPATH =
            "%s/pythons/pyspark.zip,/pythons/py4j-0.10.4-src.zip";

    public static final String DEFAULT_SPARK_SQL_PROXY_MAIN_CLASS =
            "com.dtstack.engine.spark.sql.SqlProxy";

    public static final String DEFAULT_CARBON_SQL_PROXY_MAIN_CLASS =
            "com.dtstack.engine.spark.sql.CarbondataSqlProxy";

    public static final String HIVE_CONF_NAME = "hive-site.xml";

    public static final String CARBON_HIVE_CONF_NAME = "carbon-hive-site.xml";

    public static final String DEFAULT_APP_NAME = "spark_default_name";

    public static final String SQL_KEY = "sql";

    public static final String APP_NAME_KEY = "appName";

    public static final String SPARK_SESSION_CONF_KEY = "sparkSessionConf";

    // ------------------------------------------------------------------------
    // Kerberos Configs
    // ------------------------------------------------------------------------
    public static final String SPARK_JAVAOPTIONS = "-Djava.security.krb5.conf=./krb5.conf";

    public static final String SPARK_JAVA_OPTIONS_KRB5CONF =
            "-Djava.security.krb5.conf=./krb5.conf";

    public static final String SPARK_JAVA_OPTIONS_LOG4J_CONTENT =
            "-Dlog4j.configuration=./__spark_conf__/log4j.properties";

    // 默认hdfs resource文件清除频率
    public static final String SPARK_DEFAULT_CLEAR_RESOURCED_RATE = "30";

    public static final String SPARK_LOCAL_TMP = "spark_local_tmp";

    public static final String SPARK_LOG4J_CONTENT =
            "log4j.rootLogger=INFO,Client\n"
                    + "log4j.logger.Client=INFO,Client\n"
                    + "log4j.additivity.Client = false\n"
                    + "log4j.appender.console.target=System.err\n"
                    + "log4j.appender.Client=org.apache.log4j.ConsoleAppender\n"
                    + "log4j.appender.Client.layout=org.apache.log4j.PatternLayout\n"
                    + "log4j.appender.Client.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p %-60c %x - %m%n";

    public static final String LOCAL_KEYTAB_DIR_PARENT = USER_DIR + "/kerberos/keytab";

    public static final String RANGER_SECURITY = "/ranger-spark-security.xml";

    public static final String RANGER_AUDIT = "/ranger-spark-audit.xml";

    public static final String XML_SUFFIX = ".xml";

    public static final String RANGER = "ranger";

    public static final String TMP_RANGER_FILE_PATH = USER_DIR + "/tmp/tmpRangerConf";

    public static final String DEFAULT_SPARK_PROMETHEUS_SINK_JAR_PATH =
            "/opt/dtstack/DTSpark2.4/spark_pkg/jars/spark-prometheus-sink-2.4.8-dt.jar";

    public static final ArrayList<String> FILTER_PARAM =
            new ArrayList<>(
                    Arrays.asList(
                            "fs.hdfs.impl.disable.cache",
                            "fs.file.impl.disable.cache",
                            "hive.execution.engine"));

    private SparkConstants() {}
}
