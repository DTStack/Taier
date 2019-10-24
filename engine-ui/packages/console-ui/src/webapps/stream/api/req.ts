// 从config文件全局读取
import { STREAM_BASE_URL, STREAM_BASE_URL_NOT_SERVICE } from 'config/base';

export default {

    // ===== 用户模块 ===== //
    // LOGIN: `${STREAM_BASE_URL}/user/login`,
    ADD_ROLE_USRE: `${STREAM_BASE_URL}/roleUser/addRoleUser`, // 项目用户
    GET_NOT_PROJECT_USERS: `${STREAM_BASE_URL}/project/getUsersNotInProject`, // 获取所以用户
    LOGOUT: `/uic/api/v2/logout`,
    APP_LOGOUT: `${STREAM_BASE_URL}/login/out`,
    UPDATE_USER_ROLE: `${STREAM_BASE_URL}/roleUser/updateUserRole`, // 更新用户角色
    GET_USER_BY_ID: `${STREAM_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    SEARCH_UIC_USERS: `${STREAM_BASE_URL}/project/getUicUsersNotInProject`,

    // ===== 项目模块 ===== //
    CREATE_PROJECT: `${STREAM_BASE_URL}/project/createProject`, // 创建项目
    REMOVE_USER_FROM_PROJECT: `${STREAM_BASE_URL}/roleUser/removeRoleUserFromProject`, // 删除项目用户接口
    ADD_PROJECT_USER: `${STREAM_BASE_URL}/roleUser/addRoleUserNew`, // 添加项目用户接口
    QUERY_PROJECT_LIST: `${STREAM_BASE_URL}/project/queryProjects`, // 查询项目列表
    GET_PROJECT_LIST: `${STREAM_BASE_URL}/project/getProjects`, // 获取当前用户有权限的项目列表
    GET_ALL_PROJECTS: `${STREAM_BASE_URL}/project/getAllProjects`, // 获取所以项目列表
    GET_USRE_PROJECTS: `${STREAM_BASE_URL}/project/getProjectUserIn`, // 获取用户所在的所有项目
    GET_PROJECT_USERS: `${STREAM_BASE_URL}/project/getProjectUsers`, // 获取所在的项目所有用户
    GET_PROJECT_BY_ID: `${STREAM_BASE_URL}/project/getProjectByProjectId`, // 获取项目详情
    UPDATE_PROJECT_INFO: `${STREAM_BASE_URL}/project/updateProjectInfo`, // 修改项目描述
    GET_PROJECT_INFO: `${STREAM_BASE_URL}/project/getProjectInfo`, // 获取项目信息包括告警
    GET_PROJECT_LIST_INFO: `${STREAM_BASE_URL}/project/getProjectList`, // 首页project查询
    SET_STICKY: `${STREAM_BASE_URL}/project/setSticky`, // 首页project查询
    DELETE_PROJECT: `${STREAM_BASE_URL}/project/deleteProject`, // 删除项目接口
    UPDATE_PROJECT_SCHEDULE: `${STREAM_BASE_URL}/project/closeOrOpenSchedule`, // 开启或关闭项目调度
    BIND_PRODUCTION_PROJECT: `${STREAM_BASE_URL}/project/bindingProject`, // 绑定生产项目
    GET_COULD_BINDING_PROJECT_LIST: `${STREAM_BASE_URL}/project/getBindingProjects`, // 获取可以绑定的项目

    // ===== 角色管理 ===== //
    GET_ROLE_LIST: `${STREAM_BASE_URL}/role/pageQuery`, // 获取角色列表
    UPDATE_ROLE: `${STREAM_BASE_URL}/role/addOrUpdateRole`, // 新建/更新角色
    DELETE_ROLE: `${STREAM_BASE_URL}/role/deleteRole`, // 删除角色
    GET_ROLE_TREE: `${STREAM_BASE_URL}/permission/tree`, // 编辑角色
    GET_ROLE_INFO: `${STREAM_BASE_URL}/permission/getPermissionIdsByRoleId`, // 获取角色信息

    // ========================= 实时任务 ========================= //
    SQL_FORMAT: `${STREAM_BASE_URL}/streamTask/sqlFormat`,

    // ===== task模块 ===== //
    SAVE_TASK: `${STREAM_BASE_URL}/streamTask/addOrUpdateTask`, // 添加或者更新任务
    FORCE_UPDATE_TASK: `${STREAM_BASE_URL}/streamTask/forceUpdate`, // 强制更新
    START_TASK: `${STREAM_BASE_URL}/streamTask/startTask`, // 启动任务
    STOP_TASK: `${STREAM_BASE_URL}/streamTask/stopTask`, // 停止任务
    GET_TASK: `${STREAM_BASE_URL}/streamTask/getTaskById`, // 通过ID获取任务
    UPDATE_TASK_RES: `${STREAM_BASE_URL}/streamTask/updateTaskResource`, // 任务添加资源
    GET_TASK_LIST: `${STREAM_BASE_URL}/streamTask/getTaskList`, // 任务列表
    GET_TASKS_BY_STATUS: `${STREAM_BASE_URL}/streamTask/getTaskListByStatus`, // 任务列表状态过滤接口
    SEARCH_TASKS_BY_NAME: `${STREAM_BASE_URL}/streamTask/searchTaskByName`, // 按名称搜索任务
    SORT_TASKS: `${STREAM_BASE_URL}/streamTask/changeTaskOrder`, // 任务列表时间排序
    DELETE_TASK: `${STREAM_BASE_URL}/streamTask/deleteTask`, // 删除项目
    CLONE_TASK: `${STREAM_BASE_URL}/streamTask/cloneTask`, // 克隆任务
    TASK_STATISTICS: `${STREAM_BASE_URL}/streamTask/getStatusCount`, // 实时任务个状态数量统计
    GET_TASK_LOG: `${STREAM_BASE_URL}/streamServerLog/getLogsByTaskId`, // 获取任务告警日志
    GET_TASK_FAILOVER_LOG: `${STREAM_BASE_URL}/streamServerLog/getFailoverLogsByTaskId`, // 获取任务告警日志
    GET_RUNNING_TASK_LOG: `${STREAM_BASE_URL}/streamServerLog/getRuntimeLogsByTaskIdAndStart`,
    GLOBAL_SEARCH_TASK: `${STREAM_BASE_URL}/streamTask/globalSearch`, // 全局搜索任务
    GET_TASK_TYPES: `${STREAM_BASE_URL}/streamTask/getSupportJobTypes`, // 获取任务类型
    PUBLISH_REALTIME_TASK: `${STREAM_BASE_URL}/streamTask/publishStreamTask`, // 发布任务
    GET_TIMEZONE_LIST: `${STREAM_BASE_URL}/streamTask/getAllTimeZone`, // 获取源表中的时区列表
    CONVERT_TO_SCRIPT_MODE: `${STREAM_BASE_URL}/streamTask/guideToTemplate `, // 转换向导到脚本模式
    GET_TYPE_ORIGIN_DATA: `${STREAM_BASE_URL}/streamDataSource/listDataSourceBaseInfo`, // 获取类型数据源
    GET_TOPIC_TYPE: `${STREAM_BASE_URL}/streamDataSource/getKafkaTopics`, // 获取Topic
    GET_STREM_TABLE_TYPE: `${STREAM_BASE_URL}/streamDataSource/tablelist`, // 获取数据源列表
    GET_BINLOG_LIST_BY_SOURCE: `${STREAM_BASE_URL}/streamDataSource/getBinLogListBySource`, // 获取binlog列表
    GET_DATA_PREVIEW: `${STREAM_BASE_URL}/streamDataSource/getTopicData`, // 获取kafka topic预览数据

    GET_CATALOGUE: `${STREAM_BASE_URL}/streamCatalogue/getCatalogue`, // 获取目录
    UPDATE_CATALOGUE: `${STREAM_BASE_URL}/streamCatalogue/updateCatalogue`, // 更新目录
    ADD_CATALOGUE: `${STREAM_BASE_URL}/streamCatalogue/addCatalogue`, // 新增目录
    DELETE_CATALOGUE: `${STREAM_BASE_URL}/streamCatalogue/deleteCatalogue`, // 删除目录
    GET_STREAM_CATALOGUE_BY_LOCATION: `${STREAM_BASE_URL}/streamCatalogue/getLocation`,

    CREATE_FUNC: `${STREAM_BASE_URL}/streamFunction/addFunction`, // 添加函数
    GET_FUNC: `${STREAM_BASE_URL}/streamFunction/getFunction`, // 获取函数
    GET_ALL_FUNC: `${STREAM_BASE_URL}/streamFunction/getAllFunctionName`, // 获取所有函数名
    DELETE_FUNC: `${STREAM_BASE_URL}/streamFunction/deleteFunction`, // 删除加函数
    MOVE_FUNC: `${STREAM_BASE_URL}/streamFunction/moveFunction`, // 移动函数
    GET_SYS_FUNC: `${STREAM_BASE_URL}/streamFunction/getSystemFunctions`, // 获取系统函数

    UNLOCK_FILE: `${STREAM_BASE_URL}/common/readWriteLock/getLock`, // 解锁文件
    // ===== resource资源模块 ===== //
    UPLOAD_RES: `${STREAM_BASE_URL_NOT_SERVICE}/upload/streamResource/addResource`, // 资源上传
    GET_RES_LIST: `${STREAM_BASE_URL}/streamResource/getResources`, // 资源列表
    DELETE_RES: `${STREAM_BASE_URL}/streamResource/deleteResource`, // 删除资源
    GET_RES_BY_ID: `${STREAM_BASE_URL}/streamResource/getResourceById`, // 根据ID获取资源信息
    RENAME_RES: `${STREAM_BASE_URL}/streamResource/renameResource`, // 资源重命名

    // ===== alarm告警模块 ===== //
    GET_ALARM_LIST: `${STREAM_BASE_URL}/streamAlarm/getAlarmList`, // 获取报警规则
    ADD_ALARM: `${STREAM_BASE_URL}/streamAlarm/createAlarm`, // 创建报警
    UPDATE_ALARM: `${STREAM_BASE_URL}/streamAlarm/updateAlarm`, // 更新报警
    CLOSE_ALARM: `${STREAM_BASE_URL}/streamAlarm/closeAlarm`, // 关闭报警
    OEPN_ALARM: `${STREAM_BASE_URL}/streamAlarm/startAlarm`, // 开启报警
    DELETE_ALARM: `${STREAM_BASE_URL}/streamAlarm/deleteAlarm`, // 删除报警
    GET_ALARM_RECORDS: `${STREAM_BASE_URL}/streamAlarmRecord/getAlarmRecordList`, // 获取报警记录
    ALARM_STATISTICS: `${STREAM_BASE_URL}/streamAlarmRecord/countAlarm`, // 删除报警

    // ==== 实时运维模块 ===== //
    GET_CHECK_POINT_RANGE: `${STREAM_BASE_URL}/streamTaskCheckpoint/getCheckpointTimeRange`, // 获取指定任务的保存点可选时间范围
    GET_CHECK_POINTS: `${STREAM_BASE_URL}/streamTaskCheckpoint/getCheckpointList`, // 获取指定任务时间范围内的保存点
    GET_TASK_METRICS: `${STREAM_BASE_URL}/streamJobMetric/getTaskMetrics`, // 获取指标
    GET_DATA_DELAY: `${STREAM_BASE_URL}/streamJobMetric/getDataDelay`, // 获取数据延迟
    GET_DATA_DELAY_DETAIL: `${STREAM_BASE_URL}/streamJobMetric/getDataDelayDetail`, // 数据延迟详情
    GET_CHECKPOINT_LIST: `${STREAM_BASE_URL}/streamTaskCheckpoint/pageQuery`, // 获取checkpoint列表
    GET_CHECKPOINT_OVERVIEW: `${STREAM_BASE_URL}/streamTaskCheckpoint/getDataCount`, // 获取checkpoint统计信息
    GET_TOPIC_DETAIL: `${STREAM_BASE_URL}/streamJobMetric/getTopicDetail`, // 获取topic详情
    GET_DIRTY_TABLE_OVERVIEW: `${STREAM_BASE_URL}/streamDirtyData/preview`, // 脏数据概览
    GET_DIRTY_TABLE_INFO: `${STREAM_BASE_URL}/streamDirtyData/tableInfo`, // 获取任务脏数据信息
    CHECK_SOURCE_STATUS: `${STREAM_BASE_URL}/streamDataSource/getDataSourceLinkStatus` // 获取任务的异常数据源
}
