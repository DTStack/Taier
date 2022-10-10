package com.dtstack.taier.scheduler.datasource.convert.engine;

/**
 * source 常量
 *
 * @author ：wangchuan
 * date：Created in 上午9:58 2022/3/23
 * company: www.dtstack.com
 */
public interface SourceConstant {

    /**
     * datasource type
     */
    String DATASOURCE_TYPE_KEY = "dataSourceType";

    /**
     * ssl-client 文件
     */
    String SSL_CLIENT_CONF = "sslClientConf";

    /**
     * ssl client 配置
     */
    String SSL_CLIENT = "sslClient";

    /**
     * ssl config 配置
     */
    String SSL_CONFIG = "sslConfig";

    /**
     * ssl-client 文件时间戳
     */
    String SSL_FILE_TIMESTAMP = "sslFileTimestamp";

    /**
     * remote ssl dir
     */
    String REMOTE_SSL_DIR = "remoteSSLDir";

    /**
     * 路径
     */
    String PATH = "path";

    /**
     * nfs server
     */
    String SERVER = "server";

    /**
     * kerberos 在 sftp 上的绝对路径
     */
    String KERBEROS_REMOTE_PATH = "remoteDir";

    /**
     * sftp 配置
     */
    String SFTP_CONF = "sftpConf";

    /**
     * namespace
     */
    String NAMESPACE = "namespace";

    /**
     * k8s conf
     */
    String KUBERNETES_CONF = "kubernetesConf";

    /**
     * 是否开启 kerberos
     */
    String OPEN_KERBEROS = "openKerberos";

    /**
     * JDBC URL
     */
    String SCHEMA = "schema";

    /**
     * JDBC URL
     */
    String JDBC_URL = "jdbcUrl";

    /**
     * URL
     */
    String URL = "url";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 密码
     */
    String PASSWORD = "password";

    /**
     * defaultFS
     */
    String DEFAULT_FS = "defaultFS";

    /**
     * yarn conf
     */
    String YARN_CONF_KEY = "yarnConf";

    /**
     * yarn queue
     */
    String QUEUE = "queue";


    /**
     * hadoop conf
     */
    String HADOOP_CONF_KEY = "hadoopConf";

    /**
     * principal
     */
    String PRINCIPAL = "principal";

    /**
     * principal file
     */
    String PRINCIPAL_FILE = "principalFile";

    /**
     * krb5 conf
     */
    String KRB5_CONF = "java.security.krb5.conf";

    /**
     * krb5 name
     */
    String KRB_NAME = "krbName";

    /**
     * kerberos file timestamp
     */
    String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";

    /**
     * 控制台高可用配置
     */
    String FS_DEFAULT_FS = "fs.defaultFS";

    /**
     * ak
     */
    String ACCESS_KEY = "accessKey";

    /**
     * sk
     */
    String SECRET_KEY = "secretKey";

    /**
     * endpoint
     */
    String ENDPOINT = "endpoint";

    /**
     * Kylin RestFul URL
     */
    String KYLIN_RESTFUL_AUTH_URL = "hostPort";

    /**
     * kylin restful project
     */
    String KYLIN_RESTFUL_PROJECT = "project";

    /**
     * eg. ldap username
     */
    String DT_PROXY_USERNAME = "dtProxyUserName";

    /**
     * eg. ldap password
     */
    String DT_PROXY_PASSWORD = "dtProxyPassword";

    String HIVE_PROXY_ENABLE = "hive.proxy.enable";

    String PROXY_USER_FORMAT = "hive.server2.proxy.user=%s";

    /**
     * tbds id
     */
    String TBDS_ID = "tbds_id";

    /**
     * tbds key
     */
    String TBDS_KEY = "tbds_key";

    /**
     * tbds username
     */
    String TBDS_USERNAME = "tbds_username";

    String SEMICOLON = ";";
}
