package com.dtstack.taier.datasource.plugin.common.constant;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:57 2020/8/27
 * @Description：Hadoop
 */
public class KerberosConstant {
    /**
     * Hadoop 开启 Kerberos 是否需要二次认证
     */
    public static final String HADOOP_SECURITY_AUTHORIZATION = "hadoop.security.authorization";

    /**
     * HBASE 开启 Kerberos 参数
     */
    public static final String HBASE_SECURITY_AUTHORIZATION = "hbase.security.authentication";

    /**
     * pheonix kerberos 参数
     */
    public static final String PHOENIX_QUERYSERVER_KERBEROS_PRINCIPAL = "phoenix.queryserver.kerberos.principal";

    /**
     * krb5 系统属性键
     */
    public static final String KEY_JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";

    /**
     * principal 键
     */
    public static final String PRINCIPAL = "principal";

    /**
     * Hbase master Principal 键
     */
    public static final String HBASE_MASTER_PRINCIPAL = "hbase.master.kerberos.principal";

    /**
     * Hbase region Principal 键
     */
    public static final String HBASE_REGION_PRINCIPAL = "hbase.regionserver.kerberos.principal";

    /**
     * hbase kerberos 是否对zk生效
     */
    public static final String HBASE_KERBEROS_EFFECT_ZOOKEEPER = "hbase.kerberos.effect.zookeeper";

    /**
     * principal 文件 键
     */
    public static final String PRINCIPAL_FILE = "principalFile";

    /**
     * KEYTAB_PATH 文件 键
     */
    public static final String KEYTAB_PATH = "keytabPath";

    /**
     * Kafka kerberos keytab 键
     */
    public static final String KAFKA_KERBEROS_KEYTAB = "kafka.kerberos.keytab";

    /**
     * Kafka Principal 参数，也可选 Principal
     */
    public static final String KAFKA_KERBEROS_SERVICE_NAME = "sasl.kerberos.service.name";

    /**
     * Resource Manager Configs
     */
    public static final String RM_PREFIX = "yarn.resourcemanager.";

    /**
     * MR 任务的 Principal 信息，也可以认为是 Yarn 的 Principal
     */
    public static final String RM_PRINCIPAL = RM_PREFIX + "principal";

    /**
     * local kerberos dir
     */
    public static final String LOCAL_KERBEROS_DIR = "localKerberosDir";
}
