package com.dtstack.engine.dtscript;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

public class DtYarnConfiguration extends YarnConfiguration {

    private static final String DT_YARN_DEFAULT_XML_FILE = "dt-yarn-default.xml";

    private static final String DT_YARN_SITE_XML_FILE = "dt-yarn-default.xml";

    static {
        YarnConfiguration.addDefaultResource(DT_YARN_DEFAULT_XML_FILE);
        YarnConfiguration.addDefaultResource(DT_YARN_SITE_XML_FILE);
    }

    /**
     * Configuration used in Client
     */
    public static final String DEFAULT_APP_TYPE = "shell";

    public static final String CONTAINER_STAGING_DIR = "container.staging.dir";

    public static final String DEFAULT_CONTAINER_STAGING_DIR = "/dtInsight/aiworks/staging";

    public static final String DTSCRIPT_USER_CLASSPATH_FIRST = "dtscript.user.classpath.first";

    public static final boolean DEFAULT_DTSCRIPT_USER_CLASSPATH_FIRST = true;

    public static final String DTSCRIPT_AM_MEMORY = "dtscript.am.memory";

    public static final int DEFAULT_DTSCRIPT_AM_MEMORY = 512;

    public static final String DTSCRIPT_AM_CORES = "dtscript.am.cores";

    public static final int DEFAULT_DTSCRIPT_AM_CORES = 1;

    public static final String DTSCRIPT_MAX_WORKER_MEMORY = "dtscript.worker.memory";

    public static final int DEFAULT_DTSCRIPT_MAX_WORKER_MEMORY = 20000;

    public static final String DTSCRIPT_WORKER_MEM_AUTO_SCALE = "dtscript.worker.mem.autoscale";

    public static final Double DEFAULT_DTSCRIPT_WORKER_MEM_AUTO_SCALE = 0.5;

    public static final String CONTAINER_REQUEST_NODES = "container.request.nodes";

    public static final String DTSCRIPT_WORKER_MEMORY = "dtscript.worker.memory";

    public static final int DEFAULT_DTSCRIPT_WORKER_MEMORY = 512;

    public static final String DTSCRIPT_WORKER_VCORES = "dtscript.worker.cores";

    public static final int DEFAULT_DTSCRIPT_WORKER_VCORES = 1;

    public static final String DTSCRIPT_WORKER_GPU = "dtscript.worker.gcores";

    public static final long DEFAULT_DTSCRIPT_WORKER_GPU = 0;

    public static final int DEFAULT_DTSCRIPT_APP_MEMORY = 512;

    public static final String DT_WORKER_NUM = "dt.worker.num";

    public static final String DT_HADOOP_HOME_DIR = "hadoop.home.dir";

    public static final int DEFAULT_DT_WORKER_NUM = 1;

    public static final String DT_APP_ELASTIC_CAPACITY = "elasticCapacity";

    public static final String DT_APP_YARN_ACCEPTER_TASK_NUMBER = "yarnAccepterTaskNumber";

    public static final String DT_APP_QUEUE = "queue";

    public static final String DEFAULT_DT_APP_QUEUE = "default";

    public static final String APP_PRIORITY = "yarn.app.priority";

    public static final String APP_MAX_ATTEMPTS = "app.maxattempts";

    public static final int DEFAULT_APP_MAX_ATTEMPTS = 3;

    public static final int DEFAULT_DTSCRIPT_APP_PRIORITY = 3;

    public static final String DEFAULT_DTSCRIPT_PYTHON_VERSION = "3.x";

    public static final String[] DEFAULT_DTSCRIPT_APPLICATION_CLASSPATH = {
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
    public static final String PYTHON2_PATH = "python2.path";

    public static final String PYTHON3_PATH = "python3.path";

    public static final String DTSCRIPT_APPMASTERJAR_PATH = "AppMaster.jar";
    public static final String DEFAULT_DTSCRIPT_APPMASTERJAR_PATH = "/dtInsight/dtscript/dtscript-core-4.0.0.jar";

    /** heart beat */

    public static final String DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL = "dtscript.container.heartbeat.interval";

    public static final int DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_INTERVAL = 10 * 1000;

    public static final String DTSCRIPT_CONTAINER_HEARTBEAT_TIMEOUT = "dtscript.container.heartbeat.timeout";

    public static final long DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_TIMEOUT = 2 * 60 * 1000;

    public static final String DTSCRIPT_LOCALRESOURCE_TIMEOUT = "dtscript.localresource.timeout";

    public static final int DEFAULT_DTSCRIPT_LOCALRESOURCE_TIMEOUT = 5 * 60 * 1000;

    public static final String DTSCRIPT_CONTAINER_HEARTBEAT_RETRY = "dtscript.container.heartbeat.retry";

    public static final int DEFAULT_DTSCRIPT_CONTAINER_HEARTBEAT_RETRY = 3;

    public static final String YARN_MAXIMUM_FAILED_CONTAINERS = "yarn.maximum-failed-containers";

    public static final int DEFAULT_MAXIMUM_FAILED_CONTAINERS = 3;

    /** app master */

    public static final String DTSCRIPT_APPMASTER_EXTRA_JAVA_OPTS = "dtscript.appmaster.extra.java.opts";

    public static final String DEFAULT_DTSCRIPT_APPMASTER_EXTRA_JAVA_OPTS = "";

    public static final String DTSCRIPT_CONTAINER_EXTRA_JAVA_OPTS = "dtscript.container.extra.java.opts";

    public static final String DEFAULT_DTSCRIPT_CONTAINER_EXTRA_JAVA_OPTS = "";

    public static final String DTSCRIPT_ALLOCATE_INTERVAL = "dtscript.allocate.interval";

    public static final int DEFAULT_DTSCRIPT_ALLOCATE_INTERVAL = 1000;

    public static final String DTSCRIPT_CONTAINER_MAX_FAILURES_RATE = "dtscript.container.maxFailures.rate";

    public static final double DEFAULT_DTSCRIPT_CONTAINER_MAX_FAILURES_RATE = 1;

    public static final String DTSCRIPT_CONTAINER_RESERVED_MEMORY = "dtscript.container.reserved.memory";

    public static final int DEFAULT_DTSCRIPT_CONTAINER_RESERVED_MEMORY = 500;

    public static final String DTSCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM = "asyncCheckYarnClientThreadNum";

    public static final int DEFAULT_DTSCRIPT_ASYNC_CHECK_YARN_CLIENT_THREAD_NUM = 3;

    /**
     * am在向rm申请资源时，在同一个nm上不能申请2次以上; app独占nm
     */
    public static final String APP_NODEMANAGER_EXCLUSIVE = "exclusive";

    public static final boolean DEFAULT_APP_NODEMANAGER_EXCLUSIVE = false;

    public static final String APP_CONTAINER_PORT_RANGE = "container.port.range";

    public static final String UTF8 = "UTF-8";

    public static final String NODE_LABEL = "nodeLabel";

    public static final String HADOOP_PROXY_USER = "HADOOP_PROXY_USER";

    public static final String JAVA_PATH = "java.path";


    public DtYarnConfiguration() {
    }

    public DtYarnConfiguration(Configuration conf) {
        super(conf);
    }
}
