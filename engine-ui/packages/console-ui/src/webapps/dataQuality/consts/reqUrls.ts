// 从config文件全局读取
import { DQ_BASE_URL, DQ_BASE_URL_NOT_SERVICE } from 'config/base';

export default {

    // ===== 系统管理 ===== //
    DQ_ROLE_QUERY: `${DQ_BASE_URL}/role/pageQuery`,
    DQ_ROLE_UPDATE: `${DQ_BASE_URL}/role/addOrUpdateRole`,
    DQ_ROLE_DELETE: `${DQ_BASE_URL}/role/deleteRole`,
    DQ_ROLE_PERMISSION_TREE: `${DQ_BASE_URL}/permission/tree`,
    DQ_ROLE_PERMISSION: `${DQ_BASE_URL}/permission/getPermissionIdsByRoleId`,

    // ===== 用户相关 ===== //
    DQ_GET_USER_BY_ID: `${DQ_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DQ_GET_USER_LIST: `${DQ_BASE_URL}/user/list`,
    DQ_GET_USER_PAGES: `${DQ_BASE_URL}/user/pageQuery`,
    DQ_USER_ROLE_ADD: `${DQ_BASE_URL}/roleUser/addRoleUser`,
    DQ_USER_ROLE_DELETE: `${DQ_BASE_URL}/roleUser/remove`,
    DQ_USER_ROLE_UPDATE: `${DQ_BASE_URL}/roleUser/updateUserRole`,
    DQ_GET_USER_NOT_IN_PROJECT: `${DQ_BASE_URL}/user/listNotIn`,

    // ===== 项目相关 ===== //

    GET_ALL_PROJECTS: `${DQ_BASE_URL}/service/project/getProjects`, // 获取所有的项目列表
    GET_PROJECT_LIST: `${DQ_BASE_URL}/common/project/listProject`, // 获取项目列表
    GET_PROJECT_DETAIL: `${DQ_BASE_URL}/common/project/getProject`, // 项目详情
    CREATE_PROJECT: `${DQ_BASE_URL}/common/project/createProject`, // 创建项目
    GET_TOP_PROJECT: `${DQ_BASE_URL}/common/projectHit/listProjectTop5`, // 项目TOP5列表
    UPDATE_PROJECT: `${DQ_BASE_URL}/common/project/updateProject`, // 更新项目
    GET_ALL_JOB_STATUS: `${DQ_BASE_URL}/service/maintain/queryAllJobStatusStatistics`, // 所有项目指标统计

    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${DQ_BASE_URL}/notify/pageQuery`,
    GET_MASSAGE_BY_ID: `${DQ_BASE_URL}/notify/getOne`,
    MASSAGE_MARK_AS_READ: `${DQ_BASE_URL}/notify/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${DQ_BASE_URL}/notify/allRead`,
    MASSAGE_DELETE: `${DQ_BASE_URL}/notify/delete`,

    // ===== 总览 ===== //
    GET_TOP_RECORD: `${DQ_BASE_URL}/monitorRecord/countTopRecord`, // 查询告警top20的表
    GET_ALARM_SUM: `${DQ_BASE_URL}/monitorRecord/sumAlarm`, // 告警汇总（今日告警总数/最近7天告警总数/最近30天告警总数）
    GET_ALARM_TREND: `${DQ_BASE_URL}/monitorRecord/monthAlarm`, // 近30天每日告警数
    GET_DASHBOARD_USAGE: `${DQ_BASE_URL}/monitorRecord/usage`, // 使用情况（已配置表数 / 已配置规则数 / 昨日新增表数 / 昨日新增规则数）

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/pageQuery`, // 通过查询数据源
    CHECK_CONNECTION: `${DQ_BASE_URL}/dataSource/checkConnection`, // 检查数据库连接
    CHECK_CONNECTION_KERBEROS: `${DQ_BASE_URL_NOT_SERVICE}/upload/service/dataSource/checkConnectionWithKerberos`, // 检查数据库连接当开启kerberos时
    ADD_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/addSource`, // 新增数据源
    ADD_DATA_SOURCES_KERBEROS: `${DQ_BASE_URL_NOT_SERVICE}/upload/service/dataSource/addSourceWithKerberos`, // 新增数据源开启kerberos时
    UPDATE_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/updateSource`, // 新增数据源
    UPDATE_DATA_SOURCES_KERBEROS: `${DQ_BASE_URL_NOT_SERVICE}/upload/service/dataSource/updateSourceWithKerberos`, // 更新新数据源开启kerberos时
    DELETE_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/deleteSource`, // 删除数据源

    GET_DATA_SOURCES_LIST: `${DQ_BASE_URL}/dataSource/list`, // 查询已添加的数据源类型
    GET_DATA_SOURCES_TYPE: `${DQ_BASE_URL}/dataSource/getTypes`, // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DQ_BASE_URL}/dataSource/tablelist`, // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DQ_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PART: `${DQ_BASE_URL}/dataSource/getPartValue`, // 查询数据源下的分区
    GET_DATA_SOURCES_PREVIEW: `${DQ_BASE_URL}/dataSource/preview`, // 预览数据源下的数据
    CHECK_DATASOURCE_PERMISSION: `${DQ_BASE_URL}/dataSource/checkPermission`, // 检查数据源是否有权限
    // ===== 逐行校验 ===== //
    GET_CHECK_LIST: `${DQ_BASE_URL}/verify/pageQuery`, // 逐行校验列表数据
    GET_CHECK_DETAIL: `${DQ_BASE_URL}/verify/getOne`, // 单个数据详情
    ADD_CHECK: `${DQ_BASE_URL}/verify/add`, // 编辑逐行校验
    UPDATE_CHECK: `${DQ_BASE_URL}/verify/update`, // 编辑逐行校验
    DELETE_CHECK: `${DQ_BASE_URL}/verify/deleteVerify`, // 删除逐行校验
    GET_CHECK_REPORT: `${DQ_BASE_URL}/verify/report`, // 获取报告信息
    GET_CHECK_REPORT_TABLE: `${DQ_BASE_URL}/verify/reportTable`, // 获取报告差异表格数据
    UPDATE_CHECK_ENV_PARAMS: `${DQ_BASE_URL}/verify/updateTaskParams`, // 更新逐行校验环境参数

    // ===== 规则配置 ===== //
    GET_MONITOR_LIST: `${DQ_BASE_URL}/monitor/pageQuery`, // 规则配置列表
    ADD_MONITOR: `${DQ_BASE_URL}/monitor/add`, // 新增规则配置
    UPDATE_MONITOR: `${DQ_BASE_URL}/monitor/update`, // 编辑规则配置
    GET_RULE_PART: `${DQ_BASE_URL}/monitor/getPart`, // 获取规则分区
    CHANGE_MONITOR_STATUS: `${DQ_BASE_URL}/monitor/closeOrOpen`, // 开启关闭监控
    GET_MONITOR_DETAIL: `${DQ_BASE_URL}/monitor/detail`, // 告警信息详情
    RULE_REMOTE_TRIGGER: `${DQ_BASE_URL}/monitor/remoteTrigger`, // 远程触发
    EXECUTE_MONITOR: `${DQ_BASE_URL}/monitor/immediatelyExecuted`, // 立即执行
    CHECK_MONITOR: `${DQ_BASE_URL}/monitor/checkMonitor`, // 检测监控对象是否已存在
    UPDATE_TASK_PARAMS: `${DQ_BASE_URL}/monitor/updateTaskParams`, //  更新环境参数

    GET_REMOTE_TRIGGER: `${DQ_BASE_URL}/monitor/getRemoteTrigger`, // 获取配置的远程触发
    ADD_REMOTE_TRIGGER: `${DQ_BASE_URL}/monitor/addRemoteTriger`, // 新增和更新远程触发
    DEL_REMOTE_TRIGGER: `${DQ_BASE_URL}/monitor/deleteRemoteTrigger`, // 删除远程触发

    GET_MONITOR_RULE: `${DQ_BASE_URL}/monitorRule/getRules`, // 获取监控规则
    SAVE_MONITOR_RULE: `${DQ_BASE_URL}/monitorRule/save`, // 保存监控规则
    DELETE_MONITOR_RULE: `${DQ_BASE_URL}/monitorRule/delete`, // 删除监控规则
    GET_RULE_FUNCTION: `${DQ_BASE_URL}/function/getFunctions`, // 统计函数
    GET_MONITOR_TABLE_COLUMN: `${DQ_BASE_URL}/monitor/tablecolumn`, // 表字段

    SUBSCRIBE_TABLE: `${DQ_BASE_URL}/subscribe/subscribe`, // 订阅
    UNSUBSCRIBE_TABLE: `${DQ_BASE_URL}/subscribe/unSubscribe`, // 取消订阅
    CHECK_SUBSCRIBE: `${DQ_BASE_URL}/subscribe/isSubscribe`, // 是否订阅

    // ===== 任务查询 ===== //
    GET_TASK_LIST: `${DQ_BASE_URL}/monitorRecord/pageQuery`, // 查询任务列表
    GET_TASK_DETAIL: `${DQ_BASE_URL}/monitorRecord/detailReport`, // 任务详细报告
    GET_TABLE_REPORT: `${DQ_BASE_URL}/monitorRecord/tableReport`, // 任务表级报告
    GET_TASK_ALARM_NUM: `${DQ_BASE_URL}/monitorRecord/ruleMonth`, // 任务报警统计值
    GET_FORMAT_TABLE_RESULT: `${DQ_BASE_URL}/monitorRecord/getFormatTableResult`, // 获取格式化临时表数据
    GET_INVALID_TABLE_RESULT: `${DQ_BASE_URL}/monitorRecord/getDirtyTableResult`, // 获取无效的检测结果
    DOWNLOAD_INVALID_TABLE_RESULT: `/api/dq/export/monitorRecord/downloadDirtyTableResult`, // 获取无效的检测结果

    // ===== common ===== //
    DQ_GET_ALL_DICT: `${DQ_BASE_URL}/dict/allDicts`

}
