 // 从config文件全局读取
 import { RDOS_BASE_URL } from 'config/base';

 export default {
 
     // ===== 用户模块 ===== //
     // LOGIN: `${RDOS_BASE_URL}/user/login`,
     ADD_ROLE_USRE: `${RDOS_BASE_URL}/common/roleUser/addRoleUser`, // 项目用户
     GET_NOT_PROJECT_USERS: `${RDOS_BASE_URL}/common/project/getUsersNotInProject`, // 获取所以用户
     LOGOUT: `/uic/api/v2/logout`,
     APP_LOGOUT: `${RDOS_BASE_URL}/login/out`,
     UPDATE_USER_ROLE: `${RDOS_BASE_URL}/common/roleUser/updateUserRole`, // 更新用户角色
     GET_USER_BY_ID: `${RDOS_BASE_URL}/common/user/getUserById`, // 根据用户ID获取用户
     SEARCH_UIC_USERS: `${RDOS_BASE_URL}/common/project/getUicUsersNotInProject`,
 
     // ===== 项目模块 ===== //
     CREATE_PROJECT: `${RDOS_BASE_URL}/common/project/createProject`, // 创建项目
     REMOVE_USER_FROM_PROJECT: `${RDOS_BASE_URL}/common/roleUser/removeRoleUserFromProject`, // 删除项目用户接口
     ADD_PROJECT_USER: `${RDOS_BASE_URL}/common/roleUser/addRoleUserNew`, // 添加项目用户接口
     QUERY_PROJECT_LIST: `${RDOS_BASE_URL}/common/project/queryProjects`, // 查询项目列表
     GET_PROJECT_LIST: `${RDOS_BASE_URL}/common/project/getProjects`, // 获取当前用户有权限的项目列表
     GET_ALL_PROJECTS: `${RDOS_BASE_URL}/common/project/getAllProjects`, // 获取所以项目列表
     GET_USRE_PROJECTS: `${RDOS_BASE_URL}/common/project/getProjectUserIn`, // 获取用户所在的所有项目
     GET_PROJECT_USERS: `${RDOS_BASE_URL}/common/project/getProjectUsers`, // 获取所在的项目所有用户
     GET_PROJECT_BY_ID: `${RDOS_BASE_URL}/common/project/getProjectByProjectId`, // 获取项目详情
     UPDATE_PROJECT_INFO: `${RDOS_BASE_URL}/common/project/updateProjectInfo`, // 修改项目描述
     GET_PROJECT_INFO: `${RDOS_BASE_URL}/common/project/getProjectInfo`, // 获取项目信息包括告警
     GET_PROJECT_LIST_INFO: `${RDOS_BASE_URL}/common/project/getProjectList`, // 首页project查询
     SET_STICKY: `${RDOS_BASE_URL}/common/project/setSticky`, // 首页project查询
     DELETE_PROJECT: `${RDOS_BASE_URL}/common/project/deleteProject`, // 删除项目接口
     UPDATE_PROJECT_SCHEDULE:`${RDOS_BASE_URL}/common/project/closeOrOpenSchedule`,// 开启或关闭项目调度
     BIND_PRODUCTION_PROJECT:`${RDOS_BASE_URL}/common/project/bindingProject`,//绑定生产项目
     
     // ===== 角色管理 ===== //
     GET_ROLE_LIST: `${RDOS_BASE_URL}/common/role/pageQuery`, // 获取角色列表
     UPDATE_ROLE: `${RDOS_BASE_URL}/common/role/addOrUpdateRole`, // 新建/更新角色
     DELETE_ROLE: `${RDOS_BASE_URL}/common/role/deleteRole`, // 删除角色
     GET_ROLE_TREE: `${RDOS_BASE_URL}/common/permission/tree`, // 编辑角色
     GET_ROLE_INFO: `${RDOS_BASE_URL}/common/permission/getPermissionIdsByRoleId`, // 获取角色信息
 
     // ========================= 实时任务 ========================= //
     SQL_FORMAT: `${RDOS_BASE_URL}/stream/streamTask/sqlFormat`,
 
     // ===== task模块 ===== //
     SAVE_TASK: `${RDOS_BASE_URL}/stream/streamTask/addOrUpdateTask`, // 添加或者更新任务
     FORCE_UPDATE_TASK: `${RDOS_BASE_URL}/stream/streamTask/forceUpdate`, // 强制更新
     START_TASK: `${RDOS_BASE_URL}/stream/streamTask/startTask`, // 启动任务
     STOP_TASK: `${RDOS_BASE_URL}/stream/streamTask/stopTask`, // 停止任务
     GET_TASK: `${RDOS_BASE_URL}/stream/streamTask/getTaskById`, // 通过ID获取任务
     UPDATE_TASK_RES: `${RDOS_BASE_URL}/stream/streamTask/updateTaskResource`, // 任务添加资源
     GET_TASK_LIST: `${RDOS_BASE_URL}/stream/streamTask/getTaskList`, // 任务列表
     GET_TASKS_BY_STATUS: `${RDOS_BASE_URL}/stream/streamTask/getTaskListByStatus`, // 任务列表状态过滤接口
     SEARCH_TASKS_BY_NAME: `${RDOS_BASE_URL}/stream/streamTask/searchTaskByName`, // 按名称搜索任务
     SORT_TASKS: `${RDOS_BASE_URL}/stream/streamTask/changeTaskOrder`, // 任务列表时间排序
     DELETE_TASK: `${RDOS_BASE_URL}/stream/streamTask/deleteTask`, // 删除项目
     TASK_STATISTICS: `${RDOS_BASE_URL}/stream/streamTask/getStatusCount`, // 实时任务个状态数量统计
     GET_TASK_LOG: `${RDOS_BASE_URL}/stream/streamServerLog/getLogsByTaskId`, // 获取任务告警日志
     GLOBAL_SEARCH_TASK: `${RDOS_BASE_URL}/stream/streamTask/globalSearch`, // 全局搜索任务
     GET_TASK_TYPES: `${RDOS_BASE_URL}/stream/streamTask/getSupportJobTypes`, // 获取任务类型
     PUBLISH_REALTIME_TASK:`${RDOS_BASE_URL}/stream/streamTask/publishStreamTask`,//发布任务
     GET_TYPE_ORIGIN_DATA: `${RDOS_BASE_URL}/stream/streamDataSource/listDataSourceBaseInfo`,//获取类型数据源
     GET_TOPIC_TYPE: `${RDOS_BASE_URL}/stream/streamDataSource/getKafkaTopics`,//获取Topic
     GET_STREM_TABLE_TYPE: `${RDOS_BASE_URL}/stream/streamDataSource/tablelist`,//获取数据源列表
     GET_CREATE_TARGET_TABLE:`${RDOS_BASE_URL}/batch/batchHiveTableInfo/getCreateTargetTableSql`,//获取目标表创建sql
     GET_COLUMNS_OF_TABLE:`${RDOS_BASE_URL}/batch/batchHiveTableInfo/simpleTableColumns`,//获取表的列名
     GET_ALL_FUNCTION_NAME:`${RDOS_BASE_URL}/batch/batchFunction/getAllFunctionName`,//获取系统函数
     
     GET_CATALOGUE: `${RDOS_BASE_URL}/stream/streamCatalogue/getCatalogue`, // 获取目录
     UPDATE_CATALOGUE: `${RDOS_BASE_URL}/stream/streamCatalogue/updateCatalogue`, // 更新目录
     ADD_CATALOGUE: `${RDOS_BASE_URL}/stream/streamCatalogue/addCatalogue`, // 新增目录
     DELETE_CATALOGUE: `${RDOS_BASE_URL}/stream/streamCatalogue/deleteCatalogue`, // 删除目录
     GET_STREAM_CATALOGUE_BY_LOCATION: `${RDOS_BASE_URL}/stream/streamCatalogue/getLocation`,
 
     CREATE_FUNC: `${RDOS_BASE_URL}/stream/streamFunction/addFunction`, // 添加函数
     GET_FUNC: `${RDOS_BASE_URL}/stream/streamFunction/getFunction`, // 获取函数
     DELETE_FUNC: `${RDOS_BASE_URL}/stream/streamFunction/deleteFunction`, // 删除加函数
     MOVE_FUNC: `${RDOS_BASE_URL}/stream/streamFunction/moveFunction`, // 移动函数
     GET_SYS_FUNC: `${RDOS_BASE_URL}/stream/streamFunction/getSystemFunctions`, // 获取系统函数
     GET_TABLE_LIST_BY_NAME:`${RDOS_BASE_URL}/batch/batchCatalogue/getTableList`,//根据表名的表查询
     GET_RECOMMEND_TASK:`${RDOS_BASE_URL}/batch/batchTask/recommendDependencyTask`,//获取推荐的依赖任务
 
     // ===== resource资源模块 ===== //
     UPLOAD_RES: `${RDOS_BASE_URL}/upload/stream/streamResource/addResource`, // 资源上传
     GET_RES_LIST: `${RDOS_BASE_URL}/stream/streamResource/getResources`, // 资源列表
     DELETE_RES: `${RDOS_BASE_URL}/stream/streamResource/deleteResource`, // 删除资源
     GET_RES_BY_ID: `${RDOS_BASE_URL}/stream/streamResource/getResourceById`, // 根据ID获取资源信息
     RENAME_RES: `${RDOS_BASE_URL}/stream/streamResource/renameResource`, // 资源重命名
 
     // ===== alarm告警模块 ===== //
     GET_ALARM_LIST: `${RDOS_BASE_URL}/stream/streamAlarm/getAlarmList`, // 获取报警规则
     ADD_ALARM: `${RDOS_BASE_URL}/stream/streamAlarm/createAlarm`, // 创建报警
     UPDATE_ALARM: `${RDOS_BASE_URL}/stream/streamAlarm/updateAlarm`, // 更新报警
     CLOSE_ALARM: `${RDOS_BASE_URL}/stream/streamAlarm/closeAlarm`, // 关闭报警
     OEPN_ALARM: `${RDOS_BASE_URL}/stream/streamAlarm/startAlarm`, // 开启报警
     DELETE_ALARM: `${RDOS_BASE_URL}/stream/streamAlarm/deleteAlarm`, // 删除报警
     GET_ALARM_RECORDS: `${RDOS_BASE_URL}/stream/streamAlarmRecord/getAlarmRecordList`, // 获取报警记录
     ALARM_STATISTICS: `${RDOS_BASE_URL}/stream/streamAlarmRecord/countAlarm`, // 删除报警
 
     // ==== 实时运维模块 ===== //
     GET_CHECK_POINT_RANGE: `${RDOS_BASE_URL}/stream/streamTaskCheckpoint/getCheckpointTimeRange`, // 获取指定任务的保存点可选时间范围
     GET_CHECK_POINTS: `${RDOS_BASE_URL}/stream/streamTaskCheckpoint/getCheckpointList`, // 获取指定任务时间范围内的保存点
 }
 