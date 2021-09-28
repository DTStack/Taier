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

package com.dtstack.engine.datasource.common.exception;

import com.dtstack.dtcenter.common.exception.ExceptionEnums;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode implements ExceptionEnums {
    /***----------------start 公共模块错误码 0 ~ 100 start----------------------------------------****/

    NOT_LOGIN(0, "未登录"),
    SUCCESS(1, "执行成功"),
    PERMISSION_LIMIT(3, "没有权限"),
    TOKEN_IS_NULL(4, "dt_token is null"),
    USER_IS_NULL(5, "无此用户"),
    TOKEN_IS_INVALID(6, "dt_token is invalid"),

    UNKNOWN_ERROR(10, "未定义异常"),
    SERVER_EXCEPTION(11, "server runtime error"),

    UNSUPPORTED_OPERATION(12, "不支持的操作"),
    INVALID_PARAMETERS(13, "参数不正确"),
    NAME_ALREADY_EXIST(14, "名称已经存在"),
    NAME_FORMAT_ERROR(15, "名称格式错误"),
    NAME_ILLEGAL(16, "名称非法"),
    DATA_NOT_FIND(17, "数据不存在"),

    DATA_ALREADY_EXISTS(18, "数据已存在."),

    FUNCTION_CAN_NOT_FIND(28, "方法不存在"),
    SYSTEM_FUNCTION_CAN_NOT_MODIFY(29, "系统方法不能修改"),
    CAN_NOT_EXECUTE_THIS_SQL(31, "can not execute this sql:"),
    TABLE_COLUMN_CAN_NOT_BE_NULL(32, "table column can not be null"),
    SQL_IS_NULL(33, "SQL 不能为空"),
    SQL_EXE_EXCEPTION(36, "SQL 执行异常"),
    TABLE_HAS_NOT_PARTITION_KEY(37, "表没有设置分区键"),
    TABLE_PART_NOT_CONTAIN_ALL_KEYS(38, "参数需要包含全部的表分区键"),
    ONLY_EXECUTE_CREATE_TABLE_SQL(39, "只允许执行 'create table ....' sql 格式"),
    SQL_FORMAT_ERROR(40, "sql 格式错误"),

    SERVICE_NOT_EXIST(47, "该服务不存在"),
    SERVICE_FORBIDDEN(48, "该服务禁止请求"),
    METHOD_NOT_EXIST(49, "方法不存在"),
    METHOD_FORBIDDEN(50, "该方法禁止被调用"),
    CONFIG_ERROR(51, "配置错误"),


    /***----------------end 公共模块错误码 end ----------------------------------------****/

    /***----------------start 用户 相关错误码 100~129 start----------------------------****/
    USER_IS_NOT_TENANT_OWNER(52, "用户不是租户所有者"),
    USER_CAN_NOT_EDIT_SELF(53, "用户无法对自己操作"),
    USER_NOT_FIND(54, "用户不存在"),
    USER_ROLE_ALREADY_IN_PROJECT(55, "项目中已经存在对用户的添加角色"),
    USER_IS_NOT_PROJECT_OWNER(56, "用户不是项目所有者"),
    USER_IS_NOT_PROJECT_ADMIN(57, "用户不是项目管理员"),
    CAN_NOT_REMOVE_PROJECT_USER(58, "用户不能被移除"),
    GET_USER_ERROR(59, "获取用户出错"),

    ROLE_NOT_FOUND(60, "角色不存在"),
    ROLE_IDS_CANNOT_BE_EMPTY(61, "分配角色不能为空"),

    IP_PORT_FORMAT_ERROR(62, "地址格式错误"),
    IP_PORT_CONN_ERROR(63, "IP端口连接失败"),

    /***----------------end 用户 相关错误码 end ----------------------------------------****/


    /*****---start项目相关-----****/

    PROJECT_NAME_ALREADY_EXISIT(100, "项目标识在当前集群下已存在"),
    PROJECT_ALIAS_ALREADY_EXISIT(101, "项目显示名称已存在"),
    PROJECT_NEED_AT_LEAST_ONE_ENGINE(102, "项目至少需要设置一个引擎"),

    /*****--end项目相关-----****/


    /***----------------start 目录/文件/资源相关 相关错误码 130~149 start----------------****/

    CATALOGUE_NO_EMPTY(130, "目录非空"),
    CAN_NOT_FIND_CATALOGUE(131, "该目录不存在"),
    CAN_NOT_MOVE_CATALOGUE(132, "该目录不允许移动至当前目录和子目录"),
    CAN_NOT_DELETE_RESOURCE(133, "该资源被引用,不能被删除"),
    CAN_NOT_FIND_RESOURCE(134, "资源不存在"),
    FILE_NOT_EXISTS(135, "文件不存在"),
    FILE_MUST_NOT_DIR(136, "不能选择文件夹"),
    FILE_TYPE_NOT_SUPPORTED(137, "不支持该文件格式"),
    RESOURCE_TYPE_NOT_MATCH(138, "资源类型不匹配"),
    PARENT_NODE_NOT_EXISTS(139, "父节点不存在"),
    SUBDIRECTORY_OR_FILE_AMOUNT_RESTRICTIONS(140, "当前目录下直接一层的子目录或者文件的个数总数不可超过2000"),
    CREATE_PROJECT_CATALOGUE_LEVE(141, "创建目录层级不能大于3"),
    FILE_NAME_REPETITION(142, "同一路径下不能存在同名文件夹"),


    /***----------------end 用户 相关错误码 end ----------------------------------------****/

    /***----------------start 数据源 相关错误码 150~179 start----------------------------****/

    CAN_NOT_FIND_DATA_SOURCE(150, "数据源不存在"),
    CAN_NOT_FITABLE_SOURCE_TYPE(151, "找不到对应的数据源类型"),
    CAN_NOT_MODIFY_ACTIVE_SOURCE(152, "不能修改使用中的数据源."),
    TEST_CONN_FAIL(153, "测试连接失败"),
    DATA_SOURCE_NAME_ALREADY_EXISTS(154, "数据源名称已存在"),
    DATA_SOURCE_NOT_SET(155, "未配置数据源"),
    CAN_NOT_MODIFY_DEFAULT_DATA_SOURCE(156, "默认数据源不允许修改"),
    ERROR_DEFAULT_FS_FORMAT(157, "defaultFS格式不正确"),
    CAN_NOT_FIND_SFTP(158, "开启kerberos认证后，需配置SFTP服务"),
    DATASOURCE_CONF_ERROR(159, "数据源信息配置错误"),
    DATASOURCE_DUP_NAME(160, "数据源有重名!"),
    CAN_NOT_DEL_AUTH_DS(161, "数据源已授权给产品，不可删除"),
    CAN_NOT_DEL_META_DS(162, "不可删除默认数据源"),
    SHIFT_DATASOURCE_ERROR(163, "迁移数据源发生错误"),
    IMPORT_DATA_SOURCE_DUP_FAIL(164, "存在数据源重复引入, 引入失败"),
    NOT_FIND_EDIT_CONSOLE_DS(165, "控制台修改的数据源不存在, 修改失败"),
    IMPORT_DS_NOT_MATCH_APP(166, "该数据源类型不属于该产品，无法授权"),
    CONSOLE_EDIT_JDBC_FORMAT_ERROR(167, "控制台修改jdbcUrl格式不正确!"),
    CONSOLE_EDIT_CAN_NOT_CONNECT(168, "控制台修改信息连接失败, 无法保存!"),
    API_CANT_DEL_NOT_META_DS(169, "API服务调用无法删除非默认数据源!"),
    API_CANT_DEL_NOT_TENANT(170, "该数据源非该租户创建，无法删除!"),
    IMPORT_DATA_SOURCE_AUTH_FAIL(171, "存在数据源未授权, 引入失败"),
    CANCEL_AUTH_DATA_SOURCE_FAIL(172, "取消授权的产品已引入该数据源，授权失败"),



    /***----------------end 数据源 相关错误码 end ----------------------------------------****/


    /***----------------start 公共服务通用 相关错误码 300001 start----------------------------****/
    // public-service


    /***----------------end 公共服务通用 相关错误码 301000 end----------------------------****/


    /***----------------start 公共服务-数据源中心 相关错误码 301001 start----------------------------****/
    // datasource


    /***----------------end 公共服务-数据源中心 相关错误码 302000 end----------------------------****/


    /***----------------start 公共服务-数据模型 相关错误码 302001 start----------------------------****/
    // data-model


    /***----------------end 公共服务-数据模型 相关错误码 303000 end----------------------------****/

    /***---------------- start HTTP 请求 相关错误码 400000 start ------------------------------------------****/
    HTTP_CONSOLE_ERROR(400000,"控制台请求失败"),
    /***---------------- end HTTP 请求 相关错误码 401000 end ------------------------------------------****/
    ;
    private int code;
    private String description;

}
