package com.dtstack.engine.common.constrant;

/**
 * 常量
 * Date: 2018/1/19
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ConfigConstant {

    public static final String TYPE_NAME_KEY = "typeName";

    public static final String CUSTOMER_PRIORITY_VAL = "job.priority";

    public static final String DEFAULT_GROUP_NAME = "default_default";

    public static final String JDBC_DRIVER_CLASS_NAME = "jdbc.driverClassName";
    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_PASSWORD = "jdbc.password";
    public static final String JDBC_USERNAME = "jdbc.username";

    public static final String NODE_ZK_ADDRESS = "nodeZkAddress";
    public static final String ISDEBUG = "isDebug";
    public static final String ISSECURITY = "isSecurity";
    public static final String QUEUE_SIZE = "queueSize";

    public static final String AKKA_ACTOR_PROVIDER = "akka.actor.provider";
    public static final String AKKA_ACTOR_SERIALIZE_MESSAGES = "akka.actor.serialize-messages";
    public static final String AKKA_ACTOR_SERIALIZE_JAVA = "akka.actor.serializers.java";
    public static final String AKKA_ACTOR_SERIALIZE_PROTO = "akka.actor.serializers.proto";
    public static final String AKKA_ACTOR_SERIALIZE_BINDINGS_WORKINFO = "akka.actor.serialization-bindings.\"WorkInfo\"";
    public static final String AKKA_ACTOR_SERIALIZE_BINDINGS_STRING = "akka.actor.serialization-bindings.\"java.lang.String\"";
    public static final String AKKA_ACTOR_SERIALIZE_BINDINGS_MESSAGE = "akka.actor.serialization-bindings.\"com.google.protobuf.Message\"";
    public static final String AKKA_REMOTE_ENABLED_TRANSPORTS = "akka.remote.enabled-transports";
    public static final String AKKA_REMOTE_NETTY_TCP_HOSTNAME = "akka.remote.netty.tcp.hostname";
    public static final String AKKA_REMOTE_NETTY_TCP_PORT = "akka.remote.netty.tcp.port";

    public static String LOCAL_HADOOP_CONF_DIR = System.getProperty("user.dir") + "/conf/hadoop";

    public static final String MD5_SUM_KEY = "md5zip";

}
