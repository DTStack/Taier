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

    public static final String LEARNING_USER_CLASSPATH_FIRST = "learning.user.classpath.first";

    public static final boolean DEFAULT_LEARNING_USER_CLASSPATH_FIRST = true;

    public static final String LEARNING_AM_MEMORY = "learning.am.memory";

    public static final int DEFAULT_LEARNING_AM_MEMORY = 512;

    public static final String LEARNING_AM_CORES = "xlearning.am.cores";

    public static final int DEFAULT_LEARNING_AM_CORES = 1;

    public static final String CONTAINER_REQUEST_NODES = "container.request.nodes";

    public static final String LEARNING_WORKER_MEMORY = "xlearning.worker.memory";

    public static final int DEFAULT_LEARNING_WORKER_MEMORY = 512;

    public static final String LEARNING_WORKER_VCORES = "xlearning.worker.cores";

    public static final int DEFAULT_LEARNING_WORKER_VCORES = 1;

    public static final String LEARNING_WORKER_GPU = "xlearning.worker.gcores";

    public static final long DEFAULT_LEARNING_WORKER_GPU = 0;

    public static final int DEFAULT_LEARNING_APP_MEMORY = 512;

    public static final String DT_WORKER_NUM = "dt.worker.num";

    public static final String DT_HADOOP_HOME_DIR = "hadoop.home.dir";

    public static final int DEFAULT_DT_WORKER_NUM = 1;

    public static final String DT_APP_ELASTIC_CAPACITY = "elasticCapacity";

    public static final String DT_APP_YARN_ACCEPTER_TASK_NUMBER = "yarnAccepterTaskNumber";

    public static final String DT_APP_QUEUE = "queue";

    public static final String DEFAULT_DT_APP_QUEUE = "default";

    public static final String APP_PRIORITY = "yarn.app.priority";

    public static final String CONTAINER_MAX_ATTEMPTS = "container.maxattempts";

    public static final int DEFAULT_LEARNING_APP_PRIORITY = 3;

    public static final String  DEFAULT_LEARNING_PYTHON_VERSION = "3.x";

    public static final String[] DEFAULT_XLEARNING_APPLICATION_CLASSPATH = {
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

    public static final String XLEARNING_CONTAINER_HEARTBEAT_INTERVAL = "xlearning.container.heartbeat.interval";

    public static final int DEFAULT_XLEARNING_CONTAINER_HEARTBEAT_INTERVAL = 1000;

    public static final String XLEARNING_CONTAINER_HEARTBEAT_RETRY = "xlearning.container.heartbeat.retry";

    public static final int DEFAULT_XLEARNING_CONTAINER_HEARTBEAT_RETRY = 3;

    public static final String YARN_MAXIMUM_FAILED_CONTAINERS = "yarn.maximum-failed-containers";

    public static final int DEFAULT_MAXIMUM_FAILED_CONTAINERS = 3;

    /** app master */

    public static final String XLEARNING_CONTAINER_EXTRA_JAVA_OPTS = "xlearning.container.extra.java.opts";

    public static final String DEFAULT_XLEARNING_CONTAINER_JAVA_OPTS_EXCEPT_MEMORY = "";

    /**
     * am在向rm申请资源时，在同一个nm上不能申请2次以上; app独占nm
     */
    public static final String APP_NODEMANAGER_EXCLUSIVE = "exclusive";

    public static final boolean DEFAULT_APP_NODEMANAGER_EXCLUSIVE = false;

    public static final String APP_CONTAINER_PORT_RANGE = "container.port.range";

    public static final String UTF8 = "UTF-8";

    public static final String NODE_LABEL = "nodeLabel";


    public DtYarnConfiguration() {
    }

    public DtYarnConfiguration(Configuration conf) {
        super(conf);
    }
}
