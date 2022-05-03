package com.dtstack.taier.common.exception;

import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author yuebai
 * @date 2021-09-07
 */
public enum ErrorCode implements ExceptionEnums, Serializable {


    /**
     * 0 ~ 100 常用错误code
     * 100 ~ 200 运维中心错误code
     * 200 ~ 300 控制台配置错误code
     * 300 ~ 400 任务开发错误code
     * 400 ~ 500 数据源错误code
     */
    NOT_LOGIN(0, "not login", "未登录"),
    SUCCESS(1, "success", ""),
    //权限不足 前端页面会进入404
    PERMISSION_LIMIT(3, "permission limit", "权限不足"),
    TOKEN_IS_NULL(4, "dt_token is null", "token信息为空"),
    USER_IS_NULL(5, "user is null", "用户不存在"),
    TOKEN_IS_INVALID(6, "dt_token is invalid", "无效token"),
    TENANT_IS_NULL(7, "tenant is null", "租户为空"),
    PARAM_NULL(8, "param is null", "参数不能为空"),
    FILE_PARSE_ERROR(9, "file parse error","文件解析异常"),


    UNKNOWN_ERROR(10, "unknown error", "未知错误"),
    SERVER_EXCEPTION(11, "server exception", "服务异常"),
    EMPTY_PARAMETERS(12, "empty parameters", "必填参数为空"),



    INVALID_PARAMETERS(13, "invalid parameters", "非法参数"),
    NAME_ALREADY_EXIST(14, "name already exist","名称已存在"),
    NAME_FORMAT_ERROR(15, "","名称格式错误"),
    DATA_NOT_FIND(17, "data not exist","数据不存在"),
    INVALID_TASK_STATUS(20, "invalid task status","无效的task"),
    INVALID_TASK_RUN_MODE(21, "invalid task run mod","无效的运行模式"),
    JOB_CACHE_NOT_EXIST(22, "job cache not exist this job","任务不存在cache"),
    JOB_STATUS_IS_SAME(23, "job status is same as cluster","任务状态与集群一致"),
    FUNCTION_CAN_NOT_FIND(28, "function can not found","方法不存在"),
    SFTP_NOT_FOUND(29, "sftp can not found","sftp不存在"),

    UPDATE_EXCEPTION(30, "update exception", "更新异常"),
    CONFIG_ERROR(51, "config error","配置错误"),

    HTTP_CALL_ERROR(64, "http call error", "远程调用失败"),

    INVALID_PAGE_PARAM(65, "page params invalid","无效的分页数据"),
    TENANT_ID_NOT_NULL(66, "TenantId cat not be null","租户id不能为空"),

    SYSTEM_FUNCTION_CAN_NOT_MODIFY(67, "","系统方法不能修改"),
    CATALOGUE_NO_EMPTY(130, "","目录不为空"),
    CAN_NOT_FIND_CATALOGUE(131,"", "该目录不存在"),
    CAN_NOT_MOVE_CATALOGUE(132, "","该目录不允许移动至当前目录和子目录"),
    CAN_NOT_DELETE_RESOURCE(133, "","该资源被引用,不能被删除"),
    CAN_NOT_FIND_RESOURCE(134, "","资源不存在"),
    FILE_NOT_EXISTS(135, "","文件不存在"),
    FILE_MUST_NOT_DIR(136, "","不能选择文件夹"),
    FILE_TYPE_NOT_SUPPORTED(137, "","不支持该文件格式"),
    RESOURCE_TYPE_NOT_MATCH(138, "","资源类型不匹配"),
    PARENT_NODE_NOT_EXISTS(139, "","父节点不存在"),
    SUBDIRECTORY_OR_FILE_AMOUNT_RESTRICTIONS(140,"", "当前目录下直接一层的子目录或者文件的个数总数不可超过2000"),
    CREATE_TENANT_CATALOGUE_LEVE(141, "","创建目录层级不能大于3"),
    FILE_NAME_REPETITION(142, "","同一路径下不能存在同名文件夹"),
    CATALOGUE_NOT_EMPTY(143, "catalogue is not null","目录信息不能为空"),
    CATALOGUE_NAME_NOT_EMPTY(144, "catalogue name cannot null","目录名称不能为空"),
    CATALOGUE_NAME_CANNOT_CONTAIN_SPACES(145, "catalogue name cannot in space","目录名称中不能含有空格"),
    CATALOGUE_EXISTS(146, "this catalogue exist","目录已存在"),
    CATALOGUE_INIT_FAILED(147, "init catalogue failed","初始化目录失败"),
    CATALOGUE_FUNCTION_MANAGE_UN_INIT(148, "function manage catalogue un init","函数管理未初始化"),
    TENANT_NAME_VERIFICATION_ERROR(149, "tenant name verification error","租户名称只能由字母、数字、下划线组成，且长度不超过64个字符"),



    RESOURCE_COMPONENT_NOT_CONFIG(200,"please config resource component", "请先配置调度组件"),
    STORE_COMPONENT_NOT_CONFIG(201,"please config store component", "请先配置存储组件"),
    UNSUPPORTED_PLUGIN(203, "unsupported plugin", "插件不支持"),
    COMPONENT_TYPE_UNDEFINED(204,"component type undefined", "该组件类型未定义"),
    CAN_NOT_FIND_SFTP(205, "","开启kerberos认证后，需配置SFTP服务"),
    GET_COLUMN_ERROR(206, "","获取数据库中相关表、字段信息时失败. 请联系 DBA 核查该库、表信息。"),
    TABLE_CAN_NOT_FIND(207, "","该表不存在"),
    CANT_NOT_FIND_CLUSTER(208, "cluster can not found","该集群不存在"),
    METADATA_COMPONENT_NOT_DELETED(209, "metadata component can not deleted","集群已绑定租户，对应元数据不能删除"),
    BIND_COMPONENT_NOT_DELETED(210, "component can not deleted","集群已绑定租户，对应计算和调度组件不能删除"),
    COMPONENT_TYPE_CODE_NOT_NULL(211, "component type code cat not be null","组件code不能为空"),
    STORE_COMPONENT_CONFIG_NULL(212, "storage component is null","存储组件配置为空"),
    RESOURCE_COMPONENT_CONFIG_NULL(213, "resource component is null","资源组件配置为空"),
    RESOURCE_NOT_SUPPORT_COMPONENT_VERSION(214, "resource component {} not support {} version {}","资源组件 {} 不支持 {} 版本 {}"),
    COMPONENT_CONFIG_NOT_SUPPORT_VERSION(215, "component {} config not support {} version {}","组件 {} 配置不支持版本 {}"),
    NOT_SUPPORT_COMPONENT(216, "not support component {} version {} ,pluginName is empty","不支持组件 {} 版本 {}，找不到插件名称"),
    DEPEND_ON_COMPONENT_NOT_CONFIG(217, "depend_on_component_not_config","依赖组件未配置"),
    RESOURCE_COMPONENT_NOT_SUPPORT_DEPLOY_TYPE(218, "resource component {} not support deployType {}","资源组件不支持部署版本 {}"),
    COMPONENT_INVALID(219, "component_invalid","组件不支持"),
    CLUSTER_ID_EMPTY(220, "cluster id is empty","集群id为空"),
    S3_STORE_COMPONENT_NOT_CONFIG(221, "Please configure s3 storage components first", "请先配置 S3 存储组件"),
    STORE_COMPONENT_NOT_CHOOSE(222, "Please choose storeType first", "请先选择存储组件"),
    SFTP_SERVER_NOT_CONFIG(223, "Please configure the sftp server to upload files", "请先配置SFTP公共组件"),
    CHANGE_META_NOT_PERMIT_WHEN_BIND_CLUSTER(224, "cluster has bind tenant can not change metadata component", "集群已经绑定过租户,不允许修改"),
    PRE_COMPONENT_NOT_EXISTS(225, "pre Component does not exist", "前置组件不存在"),
    META_COMPONENT_NOT_EXISTS(226, "%s component does not exist or not is metadata component", "当前集群未添加%s组件或未设置该组件为元数据获取方式"),

    TEMPLATE_TASK_CONTENT_NOT_NULL(249, "Template task content can not be null","模板任务的内容不能为空"),
    CAN_NOT_FIND_TASK(250, "task can not found","该任务不存在"),
    CAN_NOT_DELETE_TASK(251, "","该任务不能被删除"),
    VIRTUAL_TASK_UNSUPPORTED_OPERATION(253, "","虚节点任务不支持该操作"),
    CAN_NOT_PARSE_SYNC_TASK(262, "","同步任务json无法解析"),
    LOCK_IS_NOT_EXISTS(10055,"","该锁不存在"),
    TASK_CAN_NOT_SUBMIT(254, "","任务不能发布"),
    NO_FILLDATA_TASK_IS_GENERATE(256, "can not build fill data","没有补数据任务生成"),
    CAN_NOT_FIND_JOB(258, "job can not found","任务实例不存在"),
    JOB_CAN_NOT_STOP(259, "job can not stop","该任务处于不可停止状态"),
    JOB_ID_CAN_NOT_EMPTY(260, "job id can not empty","jobId不能为空"),
    ROLE_SIZE_LIMIT(300, "role size limit","超过管理员用户限制"),
    USER_NOT_ADMIN(301, "user not admin","当前操作用户不是管理员"),
    APPLICATION_CAT_NOT_EMPTY(302, "application can not be empty","application不能为空"),
    APPLICATION_NOT_FOUND(303, "application not found on yarn","获取不到application信息"),
    APPLICATION_NOT_MATCH(304, "application not match on yarn","application和当前任务jobId不匹配"),
    GET_APPLICATION_INFO_ERROR(305, "get application information error","获取applicationId信息错误"),

    SOURCE_CAN_NOT_AS_INPUT(451, "","该数据源不能作为输入数据源"),
    SOURCE_CAN_NOT_AS_OUTPUT(452, "","该数据源不能作为输出数据源"),
    CAN_NOT_FIND_DATA_SOURCE(453, "","数据源不存在"),
    CAN_NOT_FITABLE_SOURCE_TYPE(454, "not found table source table ","找不到对应source Type"),
    CAN_NOT_MODIFY_ACTIVE_SOURCE(455, "","不能修改使用中的数据源."),
    TEST_CONN_FAIL(456, "","测试连接失败"),
    DATA_SOURCE_NAME_ALREADY_EXISTS(457,"", "数据源名称已存在"),
    DATA_SOURCE_NOT_SET(458, "","未配置数据源"),
    CAN_NOT_MODIFY_DEFAULT_DATA_SOURCE(459, "","默认数据源不允许修改"),
    ERROR_DEFAULT_FS_FORMAT(460, "error default fs format","defaultFS格式不正确"),
    DATASOURCE_CONF_ERROR(461, "source conf is error","数据源信息配置错误"),
    DATASOURCE_DUP_NAME(462, "has same name source","数据源有重名!"),
    CAN_NOT_DEL_AUTH_DS(463, "","数据源已授权给产品，不可删除"),
    CAN_NOT_DEL_META_DS(464, "","不可删除默认数据源"),
    SHIFT_DATASOURCE_ERROR(465, "","迁移数据源发生错误"),
    IMPORT_DATA_SOURCE_DUP_FAIL(466, "","存在数据源重复引入, 引入失败"),
    NOT_FIND_EDIT_CONSOLE_DS(467, "","控制台修改的数据源不存在, 修改失败"),
    IMPORT_DS_NOT_MATCH_APP(468, "","该数据源类型不属于该产品，无法授权"),
    CONSOLE_EDIT_JDBC_FORMAT_ERROR(469, "","控制台修改jdbcUrl格式不正确!"),
    CONSOLE_EDIT_CAN_NOT_CONNECT(470, "","控制台修改信息连接失败, 无法保存!"),
    API_CANT_DEL_NOT_META_DS(471, "","API服务调用无法删除非默认数据源!"),
    API_CANT_DEL_NOT_TENANT(472, "","该数据源非该租户创建，无法删除!"),
    IMPORT_DATA_SOURCE_AUTH_FAIL(473, "","存在数据源未授权, 引入失败"),
    CANCEL_AUTH_DATA_SOURCE_FAIL(474, "","取消授权的产品已引入该数据源，授权失败"),

    NOT_EXISTS_PROJECT(601, "","不存在项目对应的数据库"),
    TABLE_INFO_ERR(602, "","table info ref not right"),
    CREATE_TABLE_ERR(603, "","创建表失败"),
    ALTER_TABLE_ERR(604, "","修改表出错"),
    GET_DIRTY_ERROR(605, "","get dirty data error"),
    ONLY_EXECUTE_CREATE_TABLE_SQL(606, "","只允许执行 'create table ....' sql 格式"),
    SQLPARSE_ERROR(652, "sql parse error", "sql解析失败"),

    TASK_PARAM_CONTENT_NOT_NULL(700, "task params content can not be null","任务中存在未赋值的系统参数或自定义参数,请检查任务参数配置");


    private final int code;
    private final String enMsg;
    private final String zhMsg;

    ErrorCode(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return getMsg();
    }

    public String getMsg() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return this.zhMsg;
        } else {
            return this.enMsg;
        }
    }
}