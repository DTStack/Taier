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

package com.dtstack.taier.datasource.plugin.common.exception;

/**
 * 获取连接时出现的错误码和相关描述集合
 *
 * @author ：wangchuan
 * date：Created in 上午10:16 2020/11/6
 * company: www.dtstack.com
 */
public enum ConnErrorCode implements IErrorCode {

    /**
     * 数据库账号或密码错误
     */
    USERNAME_PASSWORD_ERROR(1, "Incorrect database account or password"),

    /**
     * 缺少账号名或密码
     */
    MISSING_USERNAME_OR_PASSWORD(2, "Account name or password is missing"),

    /**
     * 数据库不存在
     */
    DB_NOT_EXISTS(3, "Database does not exist"),

    /**
     * 不能识别该host域名，请检查host配置
     */
    UNKNOWN_HOST_ERROR(4, "The host domain name cannot be recognized, please check the host configuration"),

    /**
     * jdbcUrl格式错误
     */
    JDBC_FORMAT_ERROR(5, "jdbcUrl format error"),

    /**
     * 数据库连接超时
     */
    CONNECTION_TIMEOUT(6, "Database connection timed out"),

    /**
     * ip或端口错误
     */
    IP_PORT_FORMAT_ERROR(7, "ip or port error"),

    /**
     * 数据库版本错误，请检查使用版本是否对应
     */
    VERSION_ERROR(8, "The database version is wrong, please check whether the used version corresponds"),

    /**
     * 此用户无访问权限
     */
    DB_PERMISSION_ERROR(9, "This user has no access rights"),

    /**
     * 应用程序服务器拒绝建立连接，请检查网络是否通畅
     */
    CANNOT_ACQUIRE_CONNECT(10, "The application server refuses to establish a connection, please check if the network is smooth"),

    /**
     * 此ip拒绝连接
     */
    CANNOT_PING_IP(11, "This ip refused to connect"),

    /**
     * 此端口拒绝连接
     */
    CANNOT_TELNET_PORT(12, "Connection refused on this port"),

    /**
     * hdfs权限检查失败，请检查该用户权限
     */
    HDFS_PERMISSION_ERROR(13, "hdfs permission check failed, please check the user permission"),

    /**
     * zookeeper 上不存在此节点信息，请检查znode是否正确
     */
    ZK_NODE_NOT_EXISTS(14, "This node information does not exist on zookeeper, please check whether the znode is correct"),

    /**
     * zookeeper 服务拒绝连接，请检查zk地址、端口是否正确或zookeeper服务是否正常
     */
    ZK_IS_NOT_CONNECT(15, "The zookeeper service refused to connect, please check whether the zk address and port are correct or whether the zookeeper service is normal"),

    /**
     * 缺少用户名信息
     */
    MISSING_USERNAME(16, "Missing username information"),

    /**
     * 缺少用户名信息
     */
    URL_MISSING_DATABASE(17, "Url missing database"),

    /**
     * 未定义异常
     */
    UNDEFINED_ERROR(0, "Failed to get database connection");

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    ConnErrorCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误描述
     */
    private final String desc;
}
