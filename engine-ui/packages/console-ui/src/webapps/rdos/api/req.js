const baseUrl = APP_CONF.API_BASE_URL || ''; // 从config文件全局读取

export default {
    // ===== 用户模块 ===== //
    // LOGIN: `${baseUrl}/user/login`,
    ADD_ROLE_USRE: `${baseUrl}/common/roleUser/addRoleUser`, // 项目用户
    GET_NOT_PROJECT_USERS: `${baseUrl}/common/project/getUsersNotInProject`, // 获取所以用户
    LOGOUT: `/uic/api/v2/logout`,
    APP_LOGOUT: `${baseUrl}/login/out`,
    UPDATE_USER_ROLE: `${baseUrl}/common/roleUser/updateUserRole`, // 更新用户角色
    GET_USER_BY_ID: `${baseUrl}/common/user/getUserById`, // 根据用户ID获取用户

    // ===== 项目模块 ===== //
    CREATE_PROJECT: `${baseUrl}/common/project/createProject`, // 创建项目
    REMOVE_USER_FROM_PROJECT: `${baseUrl}/common/roleUser/removeRoleUserFromProject`, // 删除项目用户接口
    ADD_PROJECT_USER: `${baseUrl}/common/roleUser/addRoleUser`, // 添加项目用户接口
    QUERY_PROJECT_LIST: `${baseUrl}/common/project/queryProjects`, // 查询项目列表
    GET_PROJECT_LIST: `${baseUrl}/common/project/getProjects`, // 获取项目列表
    GET_USRE_PROJECTS: `${baseUrl}/common/project/getProjectUserIn`, // 获取用户所在的所有项目
    GET_PROJECT_USERS: `${baseUrl}/common/project/getProjectUsers`, // 获取所在的项目所有用户
    GET_PROJECT_BY_ID: `${baseUrl}/common/project/getProjectByProjectId`, // 获取项目详情
    UPDATE_PROJECT_INFO: `${baseUrl}/common/project/updateProjectInfo`, // 修改项目描述
    GET_PROJECT_INFO: `${baseUrl}/common/project/getProjectInfo`, // 获取项目信息包括告警

    // ===== 角色管理 ===== //
    GET_ROLE_LIST: `${baseUrl}/common/role/pageQuery`, // 获取角色列表
    UPDATE_ROLE: `${baseUrl}/common/role/addOrUpdateRole`, // 新建/更新角色
    DELETE_ROLE: `${baseUrl}/common/role/deleteRole`, // 删除角色
    GET_ROLE_TREE: `${baseUrl}/common/permission/tree`, // 编辑角色
    GET_ROLE_INFO: `${baseUrl}/common/permission/getPermissionIdsByRoleId`, // 获取角色信息

    // ========================= 实时任务 ========================= //
    // ===== task模块 ===== //
    SAVE_TASK: `${baseUrl}/stream/streamTask/addOrUpdateTask`, // 添加或者更新任务
    FORCE_UPDATE_TASK: `${baseUrl}/stream/streamTask/forceUpdate`, // 强制更新
    START_TASK: `${baseUrl}/stream/streamTask/startTask`, // 启动任务
    STOP_TASK: `${baseUrl}/stream/streamTask/stopTask`, // 停止任务
    GET_TASK: `${baseUrl}/stream/streamTask/getTaskById`, // 通过ID获取任务
    UPDATE_TASK_RES: `${baseUrl}/stream/streamTask/updateTaskResource`, // 任务添加资源
    GET_TASK_LIST: `${baseUrl}/stream/streamTask/getTaskList`, // 任务列表
    GET_TASKS_BY_STATUS: `${baseUrl}/stream/streamTask/getTaskListByStatus`, // 任务列表状态过滤接口
    SEARCH_TASKS_BY_NAME: `${baseUrl}/stream/streamTask/searchTaskByName`, // 按名称搜索任务
    SORT_TASKS: `${baseUrl}/stream/streamTask/changeTaskOrder`, // 任务列表时间排序
    DELETE_TASK: `${baseUrl}/stream/streamTask/deleteTask`, // 删除项目
    TASK_STATISTICS: `${baseUrl}/stream/streamTask/getStatusCount`, // 实时任务个状态数量统计
    GET_TASK_LOG: `${baseUrl}/stream/streamServerLog/getLogsByTaskId`, // 获取任务告警日志
    GLOBAL_SEARCH_TASK: `${baseUrl}/stream/streamTask/globalSearch`, // 全局搜索任务
    GET_TASK_TYPES: `${baseUrl}/stream/streamTask/getSupportJobTypes`, // 获取任务类型

    GET_CATALOGUE: `${baseUrl}/stream/streamCatalogue/getCatalogue`, // 获取目录
    UPDATE_CATALOGUE: `${baseUrl}/stream/streamCatalogue/updateCatalogue`, // 更新目录
    ADD_CATALOGUE: `${baseUrl}/stream/streamCatalogue/addCatalogue`, // 新增目录
    DELETE_CATALOGUE: `${baseUrl}/stream/streamCatalogue/deleteCatalogue`, // 删除目录

    CREATE_FUNC: `${baseUrl}/stream/streamFunction/addFunction`, // 添加函数
    GET_FUNC: `${baseUrl}/stream/streamFunction/getFunction`, // 获取函数
    DELETE_FUNC: `${baseUrl}/stream/streamFunction/deleteFunction`, // 删除加函数
    MOVE_FUNC: `${baseUrl}/stream/streamFunction/moveFunction`, // 移动函数
    GET_SYS_FUNC: `${baseUrl}/stream/streamFunction/getSystemFunctions`, // 获取系统函数

    // ===== resource资源模块 ===== //
    UPLOAD_RES: `${baseUrl}/upload/stream/streamResource/addResource`, // 资源上传
    GET_RES_LIST: `${baseUrl}/stream/streamResource/getResources`, // 资源列表
    DELETE_RES: `${baseUrl}/stream/streamResource/deleteResource`, // 删除资源
    GET_RES_BY_ID: `${baseUrl}/stream/streamResource/getResourceById`, // 根据ID获取资源信息
    RENAME_RES: `${baseUrl}/stream/streamResource/renameResource`, // 资源重命名

    // ===== alarm告警模块 ===== //
    GET_ALARM_LIST: `${baseUrl}/stream/streamAlarm/getAlarmList`, // 获取报警规则
    ADD_ALARM: `${baseUrl}/stream/streamAlarm/createAlarm`, // 创建报警
    UPDATE_ALARM: `${baseUrl}/stream/streamAlarm/updateAlarm`, // 更新报警
    CLOSE_ALARM: `${baseUrl}/stream/streamAlarm/closeAlarm`, // 关闭报警
    OEPN_ALARM: `${baseUrl}/stream/streamAlarm/startAlarm`, // 开启报警
    DELETE_ALARM: `${baseUrl}/stream/streamAlarm/deleteAlarm`, // 删除报警
    GET_ALARM_RECORDS: `${baseUrl}/stream/streamAlarmRecord/getAlarmRecordList`, // 获取报警记录
    ALARM_STATISTICS: `${baseUrl}/stream/streamAlarmRecord/countAlarm`, // 删除报警

    // ==== 实时运维模块 ===== //
    GET_CHECK_POINT_RANGE: `${baseUrl}/stream/streamTaskCheckpoint/getCheckpointTimeRange`, // 获取指定任务的保存点可选时间范围
    GET_CHECK_POINTS: `${baseUrl}/stream/streamTaskCheckpoint/getCheckpointList`, // 获取指定任务时间范围内的保存点
}
