package com.dtstack.engine.common.constrant;

/**
 * 常量
 * Date: 2018/1/19
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ConfigConstant {

    public static final String TYPE_NAME_KEY = "typeName";
    public static final String CUSTOMER_PRIORITY_VAL = "job.priority";
    public static final String DEFAULT_GROUP_NAME = "default_default";


    public static String LOCAL_HADOOP_CONF_DIR = System.getProperty("user.dir") + "/conf/hadoop";
    public static final String MD5_SUM_KEY = "md5zip";


    public static final String AKKA_REMOTE_NETTY_TCP_HOSTNAME = "akka.remote.netty.tcp.hostname";
    public static final String AKKA_REMOTE_NETTY_TCP_PORT = "akka.remote.netty.tcp.port";

    public static final String AKKA_MASTER_SYSTEM = "masterSystem";
    public static final String AKKA_MASTER_NAME = "akka.master.name";
    public static final String AKKA_MASTER_REMOTE_PATH = "akka.master.remotePath";
    public static final String AKKA_MASTER_MASTERADDRESS = "akka.master.masterAddress";

    public static final String AKKA_WORKER_SYSTEM = "workerSystem";
    public static final String AKKA_WORKER_NAME = "akka.worker.name";
    public static final String AKKA_WORKER_REMOTE_PATH = "akka.worker.remotePath";

}
