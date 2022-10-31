/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.datasource.convert;

import java.io.File;

/**
 * 该类用于维护一些常量
 *
 * @author ：wangchuan
 * date：Created in 下午2:26 2021/7/5
 * company: www.dtstack.com
 */
public interface Consistent {
    /**
     * kerberos 配置 key
     */
    String KERBEROS_CONFIG_KEY = "kerberosConfig";

    /**
     * ssl 认证文件路径
     */
    String KEY_PATH = "keyPath";

    /**
     * ssl-client 文件
     */
    String SSL_CLIENT_CONF = "sslClientConf";

    /**
     * ssl-client 文件时间戳
     */
    String SSL_FILE_TIMESTAMP = "sslFileTimestamp";

    /**
     * ssl-client 文件时间戳
     */
    String REMOTE_SSL_DIR = "remoteSSLDir";

    /**
     * sftp 路径
     */
    String PATH = "path";

    /**
     * ssl 文件路径
     */
    String SSL_CONFIG = "sslConfig";

    /**
     * kerberos 配置文件相对路径 key
     */
    String KERBEROS_PATH_KEY = "kerberosDir";

    /**
     * kerberos 在 sftp 上的绝对路径
     */
    String KERBEROS_REMOTE_PATH = "remoteDir";

    /**
     * sftp 配置
     */
    String SFTP_CONF = "sftpConf";

    /**
     * JDBC URL
     */
    String JDBC_URL = "jdbcUrl";

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
     * 控制台高可用配置
     */
    String FS_DEFAULT_FS = "fs.defaultFS";

    /**
     * HDFS defaultFS 正则表达式
     */
    String DEFAULT_FS_REGEX = "hdfs://.*";

    /**
     * 高可用配置
     */
    String HADOOP_CONFIG = "hadoopConfig";

    /**
     * hive metaStore uris
     */
    String META_STORE_URIS = "hiveMetastoreUris";

    /**
     * 主机名/IP
     */
    String HOST = "host";
    /**
     * 端口
     */
    String PORT = "port";
    /**
     * 协议
     */
    String PROTOCOL = "protocol";

    /**
     * 连接模式 (PORT:主动 PASV:被动)
     */
    String CONNECT_MODE = "connectMode";

    /**
     * 认证方式 (1:密码 2:私钥)
     */
    String AUTH = "auth";

    /**
     * 私钥地址
     */
    String RSA_PATH = "rsaPath";

    /**
     * host+端口 key
     */
    String HOST_PORTS = "hostPorts";

    /**
     * host+端口
     */
    String HOST_PORT = "hostPort";

    /**
     * http / https
     */
    String PORTAL = "portal";

    /**
     * 请求头
     */
    String HEADER = "header";

    /**
     * 其他参数
     */
    String OTHERS = "others";

    /**
     * Hbase 集群地址
     */
    String HBASE_QUORUM = "hbase_quorum";

    /**
     * hbase 集群地址
     */
    String HBASE_ZK_QUORUM = "hbase.zookeeper.quorum";

    /**
     * Hbase zk 节点
     */
    String HBASE_PARENT = "hbase_parent";

    /**
     * Hbase 其他参数
     */
    String HBASE_OTHER = "hbase_other";

    /**
     * hbase master kerberos principal
     */
    String HBASE_MASTER_PRINCIPAL = "hbase.master.kerberos.principal";

    /**
     * hbase region server kerberos principal
     */
    String HBASE_REGION_PRINCIPAL = "hbase.master.kerberos.principal";

    /**
     * 集群地址
     */
    String ADDRESS = "address";

    /**
     * solr choot
     */
    String CHOROT = "chroot";

    /**
     * solr zkHost
     */
    String ZK_HOSt = "zkHost";

    /**
     * 数据库
     */
    String DATABASE = "database";

    /**
     * redis模式
     */
    String REDIS_TYPE = "redisType";

    /**
     * master名称
     */
    String MASTER_NAME = "masterName";

    /**
     * hostname
     */
    String HOST_NAME = "hostname";

    /**
     * AccessId
     */
    String ACCESS_ID = "accessId";

    /**
     * ak
     */
    String ACCESS_KEY = "accessKey";

    /**
     * sk
     */
    String SECRET_KEY = "secretKey";

    /**
     * aws s3 region
     */
    String REGION = "region";

    /**
     * kafka集群地址
     */
    String BROKER_LIST = "brokerList";

    /**
     * kafka 认证类型
     */
    String KAFKA_AUTHENTICATION = "authentication";

    /**
     * url地址
     */
    String URL = "url";

    /**
     * KNOX 代理
     */
    String KNOX_PROXY = "knoxProxy";

    /**
     * uri地址
     */
    String URI = "uri";

    /**
     * webSocket参数
     */
    String WEB_SOCKET_PARAMS = "webSocketParams";

    /**
     * Kylin RestFul URL
     */
    String KYLIN_RESTFUL_AUTH_URL = "authURL";

    /**
     * kylin restful project
     */
    String KYLIN_RESTFUL_PROJECT = "project";

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

    /**
     * iceberg clients
     */
    String CLIENTS = "clients";

    /**
     * iceberg conf dir
     */
    String CONF_DIR = "confDir";

    /**
     * iceberg warehouse
     */
    String WAREHOUSE = "warehouse";

    /**
     * 文件目录分隔符
     */
    String SEPARATOR = File.separator;

    /**
     * confluent5 linkjson中特有参数
     */
    String SCHEMA_REGISTRY = "SchemaRegistry";
    /**
     * catalog
     */
    String CATALOG = "catalog";

    /**
     * rocket ma nameServerAddress
     */
    String NAME_SERVER_ADDRESS = "nameServerAddress";


    String SEMICOLON = ";";

    String PROXY_USER_FORMAT = "hive.server2.proxy.user=%s";

    /**
     * rabbitmq virtualHost
     */
    String VIRTUALHOST = "virtualHost";
    /**
     * rabbitmq managementTcpPort
     */
    String MANAGEMENT_TCP_PORT = "managementTcpPort";

    String SCHEMA = "schema";

    interface SSLConfig {
        /**
         * TLS 验证的方法。共有三种模式：（FULL 默认）CA和NONE
         * 对于FULL，执行正常的 TLS 验证
         * 对于CA，仅验证 CA，但允许主机名不匹配
         * 对于NONE，没有验证
         */
        String SSLVerification = "SSLVerification";

        /**
         * 连接到启用了证书身份验证的 Trino 集群时使用。指定PEM或JKS文件的路径
         */
        String SSLKeyStorePath = "SSLKeyStorePath";

        /**
         * KeyStore 的密码(如果有)
         */
        String SSLKeyStorePassword = "SSLKeyStorePassword";

        /**
         * 密钥库的类型。默认类型由 Java keystore.type安全属性提供
         */
        String SSLKeyStoreType = "SSLKeyStoreType";

        /**
         * 要使用的 Java TrustStore 文件的位置。验证 HTTPS 服务器证书
         */
        String SSLTrustStorePath = "SSLTrustStorePath";

        /**
         * TrustStore 的密码
         */
        String SSLTrustStorePassword = "SSLTrustStorePassword";

        /**
         * TrustStore 的类型。默认类型由 Java keystore.type安全属性提供
         */
        String SSLTrustStoreType = "SSLTrustStoreType";

        /**
         * keyStore文件名
         */
        String keyStoreName = "keyStoreName";

        /**
         * truststore文件名
         */
        String truststoreName = "truststoreName";

        /**
         * ssl config
         */
        String SSL_CONF = "sslConf";

        /**
         * 是否开启ssl
         */
        String checkSSL = "checkSSL";
    }
}
