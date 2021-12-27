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

package com.dtstack.engine.common.exception;


/**
 * 错误码定义
 * FIXME 各个模块的错误码用分割线分开
 * company: www.dtstack.com
 * author: xuchao
 * create: 2017/12/1
 */
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


    JSON_PARSING_FAILED(52,"json parsing failed"),
    PYTHON_NOT_SUPPORT(10053,"python不支持2.x和3.x之外的版本类型"),
    TASK_TYPE_NOT_SUPPORT(10054,"不支持的任务类型"),
    LOCK_IS_NOT_EXISTS(10055,"该锁不存在"),
    TASK_DEPENDENCY_LOOP(10056,"任务发生依赖闭环"),
    TASK_SUBMIT_ERROR(10057,"任务提交异常"),
    ENGINE_RETURN_NULL(10058,"engine返回commitId为空"),
    TASK_SAVE_ERROR(10059,"任务保存异常"),
    SYS_BUSINESS_EXCEPTION(501, "业务异常"),



    CONN_DB_ERROR(529, "连接数据库失败. 请检查您的 账号、密码、数据库名称、IP、Port或者向 DBA 寻求帮助(注意网络环境)."),
    GET_TABLE_INFO_FAILED(531, "获取表相关信息失败."),
    GET_COLUMN_INFO_FAILED(530, "获取表字段相关信息失败."),
    UNSUPPORTED_TYPE(532, "不支持的数据库类型. 请注意查看 FlinkX / DataX 已经支持的数据库类型以及数据库版本."),
    IP_PORT_CONN_ERROR(533, "数据库服务器端口连接失败,请检查您的数据库配置或服务状态."),
    USERNAME_PWD_ERROR(534, "用户名或密码错误."),
    CANNOT_ACQUIRE_CONNECT(535, "应用程序服务器拒绝建立连接."),
    IP_PORT_FORMAT_ERROR(536, "地址格式错误"),
    DB_NOT_EXISTS(537, "数据库不存在"),
    TABLE_NOT_EXISTS(538, "表不存在"),
    JDBC_FORMAT_ERROR(539, "jdbcUrl格式错误"),


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

    /***----------------end 用户 相关错误码 end ----------------------------------------****/


    /*****---start项目相关-----****/

    PROJECT_NAME_ALREADY_EXISIT(100, "项目标识在当前集群下已存在"),
    PROJECT_ALIAS_ALREADY_EXISIT(101, "项目显示名称已存在"),
    PROJECT_NEED_AT_LEAST_ONE_ENGINE(102, "项目至少需要设置一个引擎"),
    PROJECT_ENGINE_NOT_EXIST(103, "项目引擎不存在"),

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
    CREATE_TENANT_CATALOGUE_LEVE(141, "创建目录层级不能大于3"),
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

    CAN_NOT_DELETE_NORMAL_PROJECT(270, "正常状态项目不能被删除"),
    CAN_NOT_FIND_PROJECT(271, "项目不存在"),
    CAN_NOT_DELETE_PROJECT(272, "项目不能被删除"),
    CAN_NOT_ACCESS_ACROSS_PROJECTS(273, "不能跨项目"),
    PROJECT_ID_CANNOT_BE_NULL(274, "项目ID不能为空"),

    CAN_NOT_UPDATE_TASK_STATUS(35, "任务状态更新失败"),


    /***----------------end 任务 相关错误码 end ----------------------------------------****/


    /***----------------start 告警 相关错误码 400 ~ 450 start---------------------------****/

    ALARM_NAME_LENGTH_GT_16(401, "告警名称不能超过16个字符"),
    ALARM_ALREADY_EXIST(402, "该告警名称已经存在"),
    ALARM_NOT_EXIST(403, "该告警不存在"),

    /***----------------end 告警 相关错误码 end -----------------------------------------****/


    /***----------------start 数据源 相关错误码 450 ~ 470 start--------------------------****/
    SOURCE_CAN_NOT_AS_INPUT(451, "该数据源不能作为输入数据源"),
    SOURCE_CAN_NOT_AS_OUTPUT(452, "该数据源不能作为输入数据源"),
    LOADER_ERROR(455, "数据源插件异常"),
    /***----------------end 数据源 相关错误码 end ----------------------------------------****/


    /***----------------start 数据库相关的错误码 500 ~ 600 start----------------------------------------****/
    MYSQL_CONN_USERPWD_ERROR(501, "数据库用户名或者密码错误，请检查填写的账号密码或者联系DBA确认账号和密码是否正确"),
    MYSQL_CONN_IPPORT_ERROR(502, "数据库服务的IP地址或者Port错误，请检查填写的IP地址和Port或者联系DBA确认IP地址和Port是否正确。如果是同步中心用户请联系DBA确认idb上录入的IP和PORT信息和数据库的当前实际信息是一致的"),
    MYSQL_CONN_DB_ERROR(503, "数据库名称错误，请检查数据库实例名称或者联系DBA确认该实例是否存在并且在正常服务"),

    ORACLE_CONN_USERPWD_ERROR(504, "数据库用户名或者密码错误，请检查填写的账号密码或者联系DBA确认账号和密码是否正确"),
    ORACLE_CONN_IPPORT_ERROR(505, "数据库服务的IP地址或者Port错误，请检查填写的IP地址和Port或者联系DBA确认IP地址和Port是否正确。如果是同步中心用户请联系DBA确认idb上录入的IP和PORT信息和数据库的当前实际信息是一致的"),
    ORACLE_CONN_DB_ERROR(506, "数据库名称错误，请检查数据库实例名称或者联系DBA确认该实例是否存在并且在正常服务"),

    //execute query错误
    MYSQL_QUERY_TABLE_NAME_ERROR(507, "表不存在，请检查表名或者联系DBA确认该表是否存在"),
    MYSQL_QUERY_SQL_ERROR(508, "SQL语句执行出错，请检查Where条件是否存在拼写或语法错误"),
    MYSQL_QUERY_COLUMN_ERROR(509, "Column信息错误，请检查该列是否存在，如果是常量或者变量，请使用英文单引号’包起来"),
    MYSQL_QUERY_SELECT_PRI_ERROR(510, "读表数据出错，因为账号没有读表的权限，请联系DBA确认该账号的权限并授权"),

    ORACLE_QUERY_TABLE_NAME_ERROR(511, "表不存在，请检查表名或者联系DBA确认该表是否存在"),
    ORACLE_QUERY_SQL_ERROR(512, "SQL语句执行出错，原因可能是你填写的列不存在或者where条件不符合要求，1，请检查该列是否存在，如果是常量或者变量，请使用英文单引号’包起来;  2，请检查Where条件是否存在拼写或语法错误"),
    ORACLE_QUERY_SELECT_PRI_ERROR(513, "读表数据出错，因为账号没有读表的权限，请联系DBA确认该账号的权限并授权"),
    ORACLE_QUERY_SQL_PARSER_ERROR(514, "SQL语法出错，请检查Where条件是否存在拼写或语法错误"),

    //PreSql,Post Sql错误
    MYSQL_PRE_SQL_ERROR(515, "PreSQL语法错误，请检查"),
    MYSQL_POST_SQL_ERROR(516, "PostSql语法错误，请检查"),
    MYSQL_QUERY_SQL_PARSER_ERROR(517, "SQL语法出错，请检查Where条件是否存在拼写或语法错误"),

    ORACLE_PRE_SQL_ERROR(518, "PreSQL语法错误，请检查"),
    ORACLE_POST_SQL_ERROR(519, "PostSql语法错误，请检查"),

    //SplitPK 错误
    MYSQL_SPLIT_PK_ERROR(520, "SplitPK错误，请检查"),
    ORACLE_SPLIT_PK_ERROR(521, "SplitPK错误，请检查"),

    //Insert,Delete 权限错误
    MYSQL_INSERT_ERROR(522, "数据库没有写权限，请联系DBA"),
    MYSQL_DELETE_ERROR(523, "数据库没有Delete权限，请联系DBA"),
    ORACLE_INSERT_ERROR(524, "数据库没有写权限，请联系DBA"),
    ORACLE_DELETE_ERROR(525, "数据库没有Delete权限，请联系DBA"),

    JDBC_NULL(526, "JDBC URL为空，请检查配置"),
    JDBC_OB10_ADDRESS_ERROR(527, "JDBC OB10格式错误，请联系askdatax"),
    CONF_ERROR(528, "您的配置错误."),

    COLUMN_SPLIT_ERROR(533, "根据主键进行切分失败."),

    SET_SESSION_ERROR(534, "设置 session 失败."),

    RS_ASYNC_ERROR(535, "异步获取ResultSet next失败."),

    REQUIRED_VALUE(536, "您缺失了必须填写的参数值."),

    ILLEGAL_VALUE(537, "您填写的参数值不合法."),

    ILLEGAL_SPLIT_PK(538, "您填写的主键列不合法, DataX 仅支持切分主键为一个,并且类型为整数或者字符串类型."),

    SPLIT_FAILED_ILLEGAL_SQL(539, "DataX尝试切分表时, 执行数据库 Sql 失败. 请检查您的配置 table/splitPk/where 并作出修改."),

    SQL_EXECUTE_FAIL(540, "执行数据库 Sql 失败, 请检查您的配置的 数据表/过滤条件/sql语句/字段是否有问题，或者向 DBA 寻求帮助."),

    // only for reader
    READ_RECORD_FAIL(541, "读取数据库数据失败. 请检查您的配置的 数据表/过滤条件/sql语句/字段是否有问题，或者向 DBA 寻求帮助."),

    TABLE_QUERYSQL_MIXED(542, "您配置凌乱了. 不能同时既配置table又配置querySql"),

    TABLE_QUERYSQL_MISSING(543, "您配置错误. table和querySql 应该并且只能配置一个."),

    // only for writer
    WRITE_DATA_ERROR(544, "往您配置的写入表中写入数据时失败."),

    NO_INSERT_PRIVILEGE(545, "数据库没有写权限，请联系DBA"),

    NO_DELETE_PRIVILEGE(546, "数据库没有DELETE权限，请联系DBA"),

    GET_COLUMN_ERROR(547, "获取数据库中相关表、字段信息时失败. 请联系 DBA 核查该库、表信息。"),

    GET_HIVE_PARTITION_ERROR(548, "获取分区信息失败"),

    //da job
    CAN_NOT_FIND_MYSQL_JOURNAL_NAME(548, "采集起点配置失败，采集起点文件名不存在"),

    SHOW_TABLE_ERROR(549, "获取数据库table 列表失败"),

    /***----------------end 数据库相关的错误码 end ----------------------------------------****/

    /***----------------start  相关错误码 600 ~ 620 start-----------------------------****/
    TABLE_CAN_NOT_FIND(600, "该表不存在"),
    NOT_EXISTS_PROJECT(601, "不存在项目对应的数据库"),
    TABLE_INFO_ERR(602, "table info ref not right"),
    CREATE_TABLE_ERR(603, "创建表失败"),
    ALTER_TABLE_ERR(604, "修改表出错"),
    GET_DIRTY_ERROR(605, "get dirty data error"),

    HIVE_CREATE_TABLE_ERROR_605(605, "外部表路径下不能有目录"),
    HIVE_CREATE_TABLE_ERROR_606(606, "外部表地址不能为文件"),

    IMPORT_DATA_ERROR(607, "数据导入失败"),
    IMPORT_DATA_NULL_ERROR(608, "按名称匹配时元字段不能为空"),
    MONITOR_ERROR_1(609, "添加字段监控失败"),
    ONLY_ACCEPT_ONE_SQL(610, "ddl建表不支持多条语句"),
    CREATE_TABLE_ERR_2(611, "表名不符合规范"),
    DROP_TABLE_ERROR(612, "删除表失败"),
    /***----------------end hive 相关错误码 end ------------------------------------------****/


    /***----------------start libra 相关错误码 620 ~ 640 start-----------------------------****/
    LIBRA_IMPORT_DATA_ERROR(620, "数据导入失败"),
    /***----------------end hive 相关错误码 end ------------------------------------------****/

    BATCH_TABLE_MODEL_ERR_1(701, "此原子指标被衍生指标引用，无法删除"),
    BATCH_TABLE_MODEL_ERR_2(702, "指标命名不能重复"),
    BATCH_TABLE_MODEL_ERR_3(703, "名称和数据类型不能为空"),

    DATAMASK_EMBED_RULE_CANNOT_BE_DELETED(1000, "内置规则不能删除."),
    DATAMASK_CONFIG_WITH_TABLES_CANNOT_BE_DELETED(1001, "该配置还有关联的表，不能删除."),
    DATAMASK_RULE_WITH_CONFIGS_CANNOT_BE_DELETED(1002, "该规则还有关联的脱敏配置，不能删除."),

    /** sdk服务异常 */
    SDK_ERROR(1003, "sdk服务异常."),

    /***----------------engine 相关-----------------------------****/

    /***----------------start 小文件合并错误码 800 ~ 900 start----------------------------------------****/
    CAN_NOT_FIND_MERGE_RULE(801, "合并规则不存在."),
    ONLY_SUPPORT_HIVE(802, "只支持hive表进行小文件合并."),
    PARTITION_MUST_BE_SELECTED(803, "分区表小文件合并时必须选择分区."),
    LOCATION_NOT_NULL(804, "location不能为空."),
    TABLE_IS_MERGING(805, "当前表正在进行合并...请等待合并完成."),
    PROJECT_MERGE_RULE_EXISTS(806, "当前项目已经存在文件治理规则."),
    TABLE_MERGE_TIME_IS_NULL(807, "文件治理时间不完整."),
    TABLE_MERGE_TIME_WRONG_FORMAT(808, "文件治理时间格式错误."),
    TABLE_MERGE_TIME_MAST_GRANTER_THAN_NOW(809, "文件治理时间必须大于当前时间."),
    CAN_NOT_FIND_MERGE_RECORD(810, "合并记录不存在."),
    FILE_FORMAT_IS_NOT_SUPPORT(811, "不支持该存储格式的文件合并."),
    PROJECT_NO_PERMISSION(812, "不能操作没有管理权限的项目."),
    ONLY_MERGING_CAN_STOP(813, "只有正在合并状态才可以停止合并."),
    RULE_IS_MERGING_OR_CANCEL(814, "该规则正在执行或正在取消，不能进行删除."),
    TABLE_IS_EMPTY(815, "未配置表"),
    RULE_TIME_ERROR(816, "配置的时间不正确"),
    RULE_NAME_ERROR(817, "规则名称未输入或超长"),
    RULE_NAME_REPETITION(818, "规则名称重复"),
    ONLY_MERGING_CAN_CANCEL(819, "只有等待资源状态或未开始才可以停止合并."),

    /***----------------end 小文件合并错误码 end ----------------------------------------****/

    ENGINE_POST_JOB_ID_ERROR(1200, "engine 获取job异常"),

    NOT_USED(10000000, "");

    private int code;

    private String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 错误码编号
    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {

        return String.format("{\"code\":%d, \"description\":\"%s\"}", this.code,
                this.description);
    }
}
