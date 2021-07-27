package com.dtstack.engine.common.constrant;

import java.io.File;

/**
 * 常量
 * Date: 2018/1/19
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ConfigConstant {

    public static String SP = File.separator;
    public static final String SPLIT = "_";
    public static final String RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT = "default";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String BACKSLASH = "/";

    /**
     * first clusterName，second queueName
     */
    public static final String DEFAULT_GROUP_NAME = String.join(SPLIT, RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT, RESOURCE_NAMESPACE_OR_QUEUE_DEFAULT);

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String LOCAL_KEYTAB_DIR_PARENT = USER_DIR + "/kerberos/keytab";
    public static final String LOCAL_KRB5_DIR_PARENT = USER_DIR + "/kerberos/krb5";
    public static final String LOCAL_KRB5_MERGE_DIR_PARENT = USER_DIR + "/kerberos/merge";
    public static final String MERGE_KRB5_NAME = "mergeKrb5.conf";
    public static final String MERGE_KRB5_CONTENT_KEY = "mergeKrbContent";


    public static final String VERSION = "version";
    public static final String IS_METADATA = "metadata";
    public static final String CUSTOMER_PRIORITY_VAL = "job.priority";


    public static final String AKKA_LOCALMODE = "akka.localMode";
    public static final String AKKA_ACTOR_PROVIDER = "akka.actor.provider";

    public static final String AKKA_REMOTE_NETTY_TCP_HOSTNAME = "akka.remote.netty.tcp.hostname";
    public static final String AKKA_REMOTE_NETTY_TCP_PORT = "akka.remote.netty.tcp.port";

    public static final String AKKA_DAGSCHEDULEX_SYSTEM = "dagschedulex";

    public static final String AKKA_MASTER_MASTERADDRESS = "akka.master.masterAddress";

    public static final String AKKA_ASK_TIMEOUT = "akka.ask.timeout";
    public static final String AKKA_ASK_RESULTTIMEOUT = "akka.ask.resultTimeout";
    public static final String AKKA_ASK_SUBMIT_TIMEOUT = "akka.ask.submitTimeout";
    public static final String AKKA_ASK_CONCURRENT = "akka.ask.concurrent";

    public static final String AKKA_WORKER_NODE_LABELS = "akka.worker.node.labels";
    public static final String AKKA_WORKER_SYSTEMRESOURCE_PROBE_INTERVAL = "akka.worker.systemresource.probe.Interval";
    public static final String AKKA_WORKER_TIMEOUT = "akka.worker.timeout";
    public static final String AKKA_WORKER_LOGSTORE_JDBCURL = "akka.worker.logstore.jdbcUrl";
    public static final String AKKA_WORKER_LOGSTORE_USERNAME = "akka.worker.logstore.username";
    public static final String AKKA_WORKER_LOGSTORE_PASSWORD = "akka.worker.logstore.password";

    public static final String DAGSCHEULEX_JDBC_URL = "jdbc.url";
    public static final String DAGSCHEULEX_JDBC_USERNAME = "jdbc.username";
    public static final String DAGSCHEULEX_JDBC_PASSWORD = "jdbc.password";

    public static final String JDBCURL = "jdbcUrl";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String INITIAL_SIZE = "initialSize";
    public static final String MINIDLE = "minIdle";
    public static final String MAXACTIVE = "maxActive";

    public static final String SQL_CHECKPOINT_TIMEOUT = "sql.checkpoint.timeout";
    public static final String FLINK_CHECKPOINT_TIMEOUT = "flink.checkpoint.timeout";
    public static final Long DEFAULT_CHECKPOINT_TIMEOUT = 600000L;

    public static final String KERBEROS = "kerberos";
    public static final String KERBEROS_PATH = "kerberos";

    public static final String REMOTE_DIR = "remoteDir";
    public static final String PRINCIPAL_FILE = "principalFile";
    public static final String PRINCIPAL = "principal";
    public static final String KRB_NAME = "krbName";
    public static final String OPEN_KERBEROS = "openKerberos";
    public static final String KRBNAME_DEFAULT = "krb5.conf";
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
    public static final String KRB5_CONF = "krb5.conf";
    public static final String KEYTAB_SUFFIX = ".keytab";
    public static final String KERBEROS_CONFIG = "kerberosConfig";


    public static final String LDAP_USER_NAME = "dtProxyUserName";

    public static final String ZIP_SUFFIX = ".zip";
    public static final String USER_DIR_UNZIP = System.getProperty("user.dir") + File.separator + "unzip";
    public static final String USER_DIR_DOWNLOAD = System.getProperty("user.dir") + File.separator + "download";

    public static final long DEFAULT_KUBERNETES_PARENT_NODE = -2L;
    public static final long DEFAULT_TENANT  = -1L;


    /**
     * component_config other类型 key
     */
    public static final String TYPE_NAME_KEY = "typeName";
    public static final String TYPE_NAME = "typeName";
    public static final String HADOOP_VERSION = "hadoopVersion";
    public static final String MD5_SUM_KEY = "md5zip";
    public static final String FLINK_ON_STANDALONE_CONF = "flinkOnStandaloneConf";


    public static final Long DEFAULT_CLUSTER_ID = -1L;

    public static final String DEFAULT_CLUSTER_NAME = "default";
    public final static String CLUSTER = "cluster";
    public final static String QUEUE = "queue";
    public final static String TENANT_ID = "tenantId";
    public static final String DEPLOY_MODEL = "deployMode";
    public static final String NAMESPACE = "namespace";
    public static final String MAILBOX_CUTTING = "@";

    public static final String WORKER_TIMEOUT = "worker.timeout";
    public static final String WORKER_LOGSTORE_JDBCURL = "akka.worker.logstore.jdbcUrl";
    public static final String WORKER_LOGSTORE_USERNAME = "akka.worker.logstore.username";
    public static final String WORKER_LOGSTORE_PASSWORD = "akka.worker.logstore.password";
    public static final String WORKER_INITIAL_SIZE = "akka.worker.initial.size";
    public static final String WORKER_MIN_ACTIVE = "akka.worker.min.active";
    public static final String WORKER_MAX_ACTIVE = "akka.worker.max.active";


}
