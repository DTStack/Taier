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

package com.dtstack.engine.pluginapi.exception;

import java.io.Serializable;

/**
 * 错误码定义
 * FIXME 各个模块的错误码用分割线分开
 * company: www.dtstack.com
 * author: xuchao
 * create: 2017/12/1
 */
public enum ErrorCode implements ExceptionEnums, Serializable {

    /***----------------start 公共模块错误码 0 ~ 100 start----------------------------------------****/

    NOT_LOGIN(0, "not login"),
    SUCCESS(1, "success"),
    PERMISSION_LIMIT(3, "permission limit"),
    TOKEN_IS_NULL(4, "dt_token is null"),
    USER_IS_NULL(5, "user is null"),
    TOKEN_IS_INVALID(6, "dt_token is invalid"),

    UNKNOWN_ERROR(10, "unknown error"),
    SERVER_EXCEPTION(11, "server exception"),

    UNSUPPORTED_OPERATION(12, "unsupported operation"),
    INVALID_PARAMETERS(13, "invalid parameters"),
    NAME_ALREADY_EXIST(14, "name alread exist"),
    NAME_FORMAT_ERROR(15, "name format error"),
    NAME_ILLEGAL(16, "name illegal"),
    DATA_NOT_FIND(17, "data not exist"),
    DATA_ALREADY_EXISTS(18, "data alread exist."),
    NO_MASTER_NODE(19, "no master node"),
    INVALID_TASK_STATUS(20, "invalid task status"),
    INVALID_TASK_RUN_MODE(21, "invalid task run mod"),
    JOB_CACHE_NOT_EXIST(22, "job cache not exist this job"),

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

    SERVICE_NOT_EXIST(47, "service not exist"),
    SERVICE_FORBIDDEN(48, "service forbidden"),
    METHOD_NOT_EXIST(49, "method not exist"),
    METHOD_FORBIDDEN(50, "method forbidden"),
    CALL_UNLAWFUL(51, "call unlawful"),


    /***----------------end 公共模块错误码 end ----------------------------------------****/

    /***----------------start 用户 相关错误码 100~129 start----------------------------****/

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

    HTTP_CALL_ERROR(64, "http call error"),

    /***----------------end 用户 相关错误码 end ----------------------------------------****/



    /*****---start项目相关-----****/

    PROJECT_NAME_ALREADY_EXISIT(100, "项目名称已存在"),
    PROJECT_ALIAS_ALREADY_EXISIT(101, "项目显示名称已存在"),
    PROJECT_NEED_AT_LEAST_ONE_ENGINE(102, "项目至少需要设置一个引擎"),

    /*****--end项目相关-----****/


    /***----------------start 目录/文件/资源相关 相关错误码 130~149 start----------------****/

    CATALOGUE_NO_EMPTY(130, "目录非空"),
    CAN_NOT_FIND_CATALOGUE(131, "该目录不存在"),
    CAN_NOT_MOVE_CATALOGUE(132, "该目录不允许移动"),
    CAN_NOT_DELETE_RESOURCE(133, "该资源被引用,不能被删除"),
    CAN_NOT_FIND_RESOURCE(134, "资源不存在"),
    FILE_NOT_EXISTS(135, "文件不存在"),
    FILE_MUST_NOT_DIR(136, "不能选择文件夹"),
    FILE_TYPE_NOT_SUPPORTED(137, "不支持该文件格式"),
    RESOURCE_TYPE_NOT_MATCH(138, "资源类型不匹配"),
    PARENT_NODE_NOT_EXISTS(139, "父节点不存在"),


    /***----------------end 用户 相关错误码 end ----------------------------------------****/

    /***----------------start 数据源 相关错误码 150~179 start----------------------------****/

    CAN_NOT_FIND_DATA_SOURCE(150, "数据源不存在"),
    CAN_NOT_FITABLE_SOURCE_TYPE(151, "找不到对应的数据源类型"),
    CAN_NOT_MODIFY_ACTIVE_SOURCE(152, "不能修改使用中的数据源."),
    TEST_CONN_FAIL(153, "测试连接失败"),
    DATA_SOURCE_NAME_ALREADY_EXISTS(154, "数据源名称已存在"),
    DATA_SOURCE_NOT_SET(155, "未配置数据源"),
    CAN_NOT_MODIFY_DEFAULT_DATA_SOURCE(156, "默认数据源不允许修改"),

    /***----------------end 数据源 相关错误码 end ----------------------------------------****/


    /***----------------start Hbase 相关错误码 200 ~ 249 start--------------------------****/

    HBASE_GET_CONNECTION_ERROR(200, "获取 hbase 连接异常"),
    HBASE_LIST_TABLES_ERROR(201, "获取 hbase table list 异常"),
    HBASE_CAN_NOT_CLOSE_CONNECTION_ERROR(203, "hbase 关闭连接异常"),
    HBASE_CAN_NOT_CLOSE_ADMIN_ERROR(204, "hbase can not close admin error"),
    HBASE_CAN_NOT_CLOSE_TABLE_ERROR(205, "hbase can not close table error"),
    HBASE_LIST_COLUMN_FAMILY_ERROR(206, "hbase list column families error"),
    HBASE_CONFIG_CAN_NOT_BE_NULL(207, "hbase config can not be null"),

    /***----------------end Hbase 相关错误码 end ----------------------------------------****/


    /***----------------start 任务/项目 相关错误码 250 ~ 299 start----------------------------****/
    CAN_NOT_FIND_TASK(250, "该任务不存在"),
    CAN_NOT_DELETE_TASK(251, "该任务不能被删除"),
    TASK_IS_ALREADY_RUNNING(252, "该任务已经运行"),
    VIRTUAL_TASK_UNSUPPORTED_OPERATION(253, "虚节点任务不支持该操作"),
    TASK_CAN_NOT_SUBMIT(254, "任务不能发布"),
    TASK_IS_DELETED(255, "任务已经被删除"),
    NO_FILLDATA_TASK_IS_GENERATE(256, "没有补数据任务生成"),
    TASK_CAN_NOT_RUN(257, "任务不能运行"),
    CAN_NOT_FIND_JOB(258, "任务实例不存在"),
    JOB_CAN_NOT_STOP(259, "该任务处于不可停止状态"),

    SCRIPT_NOT_FOUND(260, "脚本不存在"),
    TASK_CAN_NOT_BE_CLONED(261, "增量同步任务不能被克隆到工作流"),
    CAN_NOT_PARSE_SYNC_TASK(262, "同步任务json无法解析"),

    CAN_NOT_DELETE_NORMAL_PROJECT(15, "正常状态项目不能被删除"),
    CAN_NOT_FIND_PROJECT(16, "项目不存在"),
    CAN_NOT_ACCESS_ACROSS_PROJECTS(34, "不能跨项目"),

    CAN_NOT_UPDATE_TASK_STATUS(35, "任务状态更新失败"),


    /***----------------end 任务 相关错误码 end ----------------------------------------****/


    /***----------------start 告警 相关错误码 400 ~ 450 start---------------------------****/

    ALARM_NAME_LENGTH_GT_16(401, "告警名称不能超过16个字符"),
    ALARM_ALREADY_EXIST(402, "该告警名称已经存在"),
    ALARM_NOT_EXIST(403, "该告警不存在"),

    /***----------------end 告警 相关错误码 end -----------------------------------------****/


    DATAMASK_EMBED_RULE_CANNOT_BE_DELETED(1000, "内置规则不能删除."),
    DATAMASK_CONFIG_WITH_TABLES_CANNOT_BE_DELETED(1001, "该配置还有关联的表，不能删除."),
    DATAMASK_RULE_WITH_CONFIGS_CANNOT_BE_DELETED(1002, "该规则还有关联的脱敏配置，不能删除."),

    /***----------------start 组件管理 相关错误码 start ----------------------------------------****/
    CAN_NOT_FIN_SFTP(650, "sftp组件不存在"),
    SFTP_PATH_CAN_NOT_BE_EMPTY(650, "sftp路径不能为空"),
    CANT_NOT_FIND_CLUSTER(651, "该集群不存在"),


    /***----------------end sql解析失败 end ----------------------------------------****/

    SQLPARSE_ERROR(652,"sql解析失败"),

    /***----------------end 组件管理 相关错误码 end ----------------------------------------****/

    /**
     * System Exception
     */
    SYS_404(404, "请求地址不存在"),
    SYS_500(500, "系统异常"),
    SYS_BUSINESS_EXCEPTION(501, "业务异常"),
    TENANT_CAN_NOT_FIND(502, "租户不存在"),

    NOT_USED(10000000, "")

    ;


    private int code;

	private String description;

	ErrorCode(int code, String description){
        this.code = code;
        this.description = description;
    }

	// 错误码编号
	@Override
    public int getCode(){
		return code;
	}

	@Override
    public String getDescription(){
		return description;
	}

	@Override
	public String toString() {

		return String.format("{\"code\":%d, \"description\":\"%s\"}", this.code,
				this.description);
	}
}
