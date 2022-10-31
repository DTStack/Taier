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

package com.dtstack.taier.common.constant;

/**
 * 表单name属性常量定义
 * @description:
 * @author: liuxx
 * @date: 2021/3/16
 */
public interface FormNames {

    /**
     * 数据源类型
     */
    String DATA_TYPE = "dataType";
    /**
     * 数据源版本
     */
    String DATA_VERSION = "dataVersion";
    /**
     * 数据源名称
     */
    String DATA_NAME = "dataName";
    /**
     * 数据源简介
     */
    String DATA_DESC = "dataDesc";
    /**
     * Kylin RestFul URL
     */
    String AUTH_URL = "authURL";
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
     * 开启高可用配置
     */
    String OPEN_HADOOP_CONFIG = "openHadoopConfig";
    /**
     * 高可用配置
     */
    String HADOOP_CONFIG = "hadoopConfig";
    /**
     * kylin高可用配置
     */
    String CONFIG = "config";
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
     * host+端口
     */
    String HOST_PORTS = "hostPorts";
    /**
     * 其他参数
     */
    String OTHERS = "others";
    /**
     * Hbase集群地址
     */
    String HBASE_QUORUM = "hbase_quorum";

    String HBASE_ZK_QUORUM = "hbase.zookeeper.quorum";

    String HBASE_ZK_PARENT = "zookeeper.znode.parent";

    String HBASE_CONFIG = "hbaseConfig";

    /**
     * Hbase根目录
     */
    String HBASE_PARENT = "hbase_parent";
    /**
     * Hbase其他参数
     */
    String HBASE_OTHER = "hbase_other";
    /**
     * 集群地址
     */
    String ADDRESS = "address";
    /**
     * 集群名称
     */
    String CLUSTER_NAME = "clusterName";
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
     * Host Port地址
     */
    String HOST_PORT = "hostPort";
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
     * Project Name
     */
    String PROJECT = "project";
    /**
     * End Point
     */
    String END_POINT = "endPoint";
    /**
     * kafka集群地址
     */
    String BROKER_LIST = "brokerList";
    /**
     * url地址
     */
    String URL = "url";
    /**
     * RESTful URL
     */
    String RESTFUL_URL = "authURL";
    /**
     * webSocket参数
     */
    String WEB_SOCKET_PARAMS = "webSocketParams";
    /**
     * Kerberos 文件上传的时间戳
     */
    String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";
    /**
     * 上传Kerberos文件
     */
    String KERBEROS_FILE = "kerberosFile";
    /**
     * Kerberos 配置
     */
    String KERBEROS_CONFIG = "kerberosConfig";
    /**
     * 开启Kerberos
     */
    String OPEN_KERBEROS = "openKerberos";
    /**
     * 初始化 Kafka Kerberos 服务信息
     */
    String SASL_KERBEROS_SERVICE_NAME = "sasl.kerberos.service.name";
    /**
     * principal
     */
    String PRINCIPAL = "principal";
    /**
     * hbase master principal
     */
    String HBASE_MASTER_PRINCIPAL = "hbase_master_kerberos_principal";
    /**
     * hbase region principal
     */
    String HBASE_REGION_PRINCIPAL = "hbase_regionserver_kerberos_principal";

    /**
     * solr choot
     */
    String CHOROT = "chroot";

    /**
     * solr zkHost
     */
    String ZK_HOSt = "zkHost";

    /**AWS S3 regine**/
    String REGINE = "regine";

    /**kafka 认证**/
    String AUTHENTICATION = "authentication";

    /**kafka SASL_PLAINTEXT认证**/
    String SASL_PLAINTEXT = "SASL_PLAINTEXT";

    /**kafka 无认证**/
    String NONE = "无";

    /**kafka 开启kerberos认证**/
    String KERBROS = "kerberos";

    /**ssl文件目录路径**/
    String KEYPATH = "keyPath";


    /**ssl文件名称**/
    String SSLFILENAME = "sslFileName";

    /**TBDS ID**/
    String ID = "tbds_id";

    /**TBDS KEY**/
    String KEY = "tbds_key";

    /**TBDS username**/
    String TBDS_USERNAME = "tbds_username";

    /**控制台配置的自定义参数**/
    String CUSTOM_CONFIG = "customConfig";

    /**控制台配置的自定义参数**/
    String METASTORE_URIS = "hive.metastore.uris";


}
