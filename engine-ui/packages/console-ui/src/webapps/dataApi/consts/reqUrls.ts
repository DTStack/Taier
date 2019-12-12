// 从config文件全局读取
import { DATA_API_BASE_URL } from 'config/base';

export default {

    // ===== common ===== //
    GET_ALL_MENU_LIST: `${DATA_API_BASE_URL}/user/showMenuList`, // 获取所有功能菜单

    // LOGIN: `${STREAM_BASE_URL}/user/login`,
    ADD_ROLE_USRE: `${DATA_API_BASE_URL}/roleUser/addRoleUser`, // 项目用户
    GET_NOT_PROJECT_USERS: `${DATA_API_BASE_URL}/project/getUsersNotInProject`, // 获取所以用户
    LOGOUT: `/uic/api/v2/logout`,
    APP_LOGOUT: `${DATA_API_BASE_URL}/login/out`,
    UPDATE_USER_ROLE: `${DATA_API_BASE_URL}/roleUser/updateUserRole`, // 更新用户角色
    GET_USER_BY_ID: `${DATA_API_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    SEARCH_UIC_USERS: `${DATA_API_BASE_URL}/project/getUicUsersNotInProject`,

    // ===== 项目模块 ===== //
    CREATE_PROJECT: `${DATA_API_BASE_URL}/project/createProject`, // 创建项目
    REMOVE_USER_FROM_PROJECT: `${DATA_API_BASE_URL}/roleUser/removeRoleUserFromProject`, // 删除项目用户接口
    ADD_PROJECT_USER: `${DATA_API_BASE_URL}/roleUser/addRoleUserNew`, // 添加项目用户接口
    QUERY_PROJECT_LIST: `${DATA_API_BASE_URL}/project/queryProjects`, // 查询项目列表
    GET_PROJECT_LIST: `${DATA_API_BASE_URL}/project/getProjects`, // 获取当前用户有权限的项目列表
    GET_ALL_PROJECTS: `${DATA_API_BASE_URL}/project/getAllProjects`, // 获取所以项目列表
    GET_USRE_PROJECTS: `${DATA_API_BASE_URL}/project/getProjectUserIn`, // 获取用户所在的所有项目
    GET_PROJECT_USERS: `${DATA_API_BASE_URL}/project/getProjectUsers`, // 获取所在的项目所有用户
    GET_PROJECT_BY_ID: `${DATA_API_BASE_URL}/project/getProjectByProjectId`, // 获取项目详情
    UPDATE_PROJECT_INFO: `${DATA_API_BASE_URL}/project/updateProjectInfo`, // 修改项目描述
    GET_PROJECT_INFO: `${DATA_API_BASE_URL}/project/getProjectInfo`, // 获取项目信息包括告警
    GET_PROJECT_LIST_INFO: `${DATA_API_BASE_URL}/project/getProjectList`, // 首页project查询
    SET_STICKY: `${DATA_API_BASE_URL}/project/setSticky`, // 置顶
    GET_PROJECT_SUMMARY: `${DATA_API_BASE_URL}/project/getProjectSummaryVO`, // 获取项目总信息
    DELETE_PROJECT: `${DATA_API_BASE_URL}/project/deleteProject`, // 删除项目接口
    UPDATE_PROJECT_SCHEDULE: `${DATA_API_BASE_URL}/project/closeOrOpenSchedule`, // 开启或关闭项目调度
    BIND_PRODUCTION_PROJECT: `${DATA_API_BASE_URL}/project/bindingProject`, // 绑定生产项目
    GET_COULD_BINDING_PROJECT_LIST: `${DATA_API_BASE_URL}/project/getBindingProjects`, // 获取可以绑定的项目

    // ===== 角色管理 ===== //
    GET_ROLE_LIST: `${DATA_API_BASE_URL}/role/pageQuery`, // 获取角色列表
    UPDATE_ROLE: `${DATA_API_BASE_URL}/role/addOrUpdateRole`, // 新建/更新角色
    DELETE_ROLE: `${DATA_API_BASE_URL}/role/deleteRole`, // 删除角色
    GET_ROLE_TREE: `${DATA_API_BASE_URL}/permission/tree`, // 编辑角色
    GET_ROLE_INFO: `${DATA_API_BASE_URL}/permission/getPermissionIdsByRoleId`, // 获取角色信息

    // ===== api授权审批 ====//
    HANDLE_APPLY: `${DATA_API_BASE_URL}/apply/handleApply`, // 审批
    EDIT_HANDLE_APPLY: `${DATA_API_BASE_URL}/apply/editHandleApply`, // 管理员修改api申请的调用周期和次数
    GET_ALL_APPLY_LIST: `${DATA_API_BASE_URL}/apply/allApplyList`, // 获取审批列表
    GET_SECURITY_LIST: `${DATA_API_BASE_URL}/apply//securityGroup/pageQuery`, // 获取安全组列表
    NEW_SECURITY: `${DATA_API_BASE_URL}/apply/securityGroup/addSecurityGroup`, // 新增安全组
    UPDATE_SECURITY: `${DATA_API_BASE_URL}/apply/securityGroup/update`, // 更新安全组
    DELETE_SECURITY: `${DATA_API_BASE_URL}/apply/securityGroup/delete`, // 删除安全组
    LIST_SECURITY_API_INFO: `${DATA_API_BASE_URL}/apply/securityGroup/listApiInfo`, // 查看关联的API
    GET_SECURITY_SIMPLE_LIST: `${DATA_API_BASE_URL}/securityGroup/listSecurityGroupForView`, // 获取安全组简略列表
    LIST_SECURITY_GROUP_BY_ID: `${DATA_API_BASE_URL}/securityGroup/listSecurityGroupByApiId`, // 获取api的安全组
    UPDATE_LIMITER: `${DATA_API_BASE_URL}/apis/updateLimiter`, // 更新调用限制和安全组

    // ===== api管理 ===== //
    GET_ALL_API_LIST: `${DATA_API_BASE_URL}/apis/listByConditionForAdmin`, // 获取所有的api
    GET_DATASOURCE_BASE_INFO: `${DATA_API_BASE_URL}/dataSource/listDataSourceBaseInfo`, // 根据数据类型获取数据源
    DELETE_API: `${DATA_API_BASE_URL}/apis/deleteApi`, // 删除api
    UPDATE_API: `${DATA_API_BASE_URL}/apis/updateApiStatus`, // 更新api状态
    GET_API_CALL_RANK: `${DATA_API_BASE_URL}/apis/getApiCallUserRankList`, // 获取api调用排行
    GET_API_BUY_STATE: `${DATA_API_BASE_URL}/apply/getApiApplyList`, // 获取api订购状态
    DELETE_CATAGORY: `${DATA_API_BASE_URL}/catalogue/deleteCatalogue`, // 删除节点
    ADD_CATAGORY: `${DATA_API_BASE_URL}/catalogue/addCatalogue`, // 添加节点
    UPDATE_CATAGORY: `${DATA_API_BASE_URL}/catalogue/updateCatalogue`, // 更新节点
    NEW_API: `${DATA_API_BASE_URL}/apis/createApi`, // 新建api
    CHANGE_API: `${DATA_API_BASE_URL}/apis/updateApi`, // 更新api信息
    SAVE_OR_UPDATE_APIINFO: `${DATA_API_BASE_URL}/apis/saveOrUpdateApiInfo`, // 保存API
    GET_TABLE_BY_DATASOURCE: `${DATA_API_BASE_URL}/dataSource/tablelist`, // 根据数据源获取表
    GET_TABLE_COLUMNS_DETAIL: `${DATA_API_BASE_URL}/dataSource/tablecolumn`, // 获取表字段信息
    GET_TABLE_PREVIEW_DATA: `${DATA_API_BASE_URL}/dataSource/preview`, // 数据预览
    GET_API_DETAIL_INFO: `${DATA_API_BASE_URL}/apis/getApiInfo`, // 获取api详细信息
    GET_PAGE_INFO: `${DATA_API_BASE_URL}/apis/getPageInfo`, // API 获取分页信息
    FORMAT_SQL: `${DATA_API_BASE_URL}/apis/sqlFormat`, // 格式化sql
    PARSER_SQL: `${DATA_API_BASE_URL}/apis/sqlParser`, // sql解析
    TEST_API: `${DATA_API_BASE_URL}/apis/apiTest`, // api测试
    CHECK_API_IS_EXIST: `${DATA_API_BASE_URL}/apis/checkNameExist`, // api名称是否存在
    CHECK_HOST_PATH_IS_EXIST: `${DATA_API_BASE_URL}/apis/checkHostValid`, // 校验host是否合法
    GET_API_TIME_INFO_FOR_MANAGER: `${DATA_API_BASE_URL}/apis/getApiTimeInfoForManager`, // api调用延迟
    GET_API_CONFIG_INFO: `${DATA_API_BASE_URL}/apis/getDataSourceInfo`, // 获取api的配置信息
    GET_REGISTER_API_INFO: `${DATA_API_BASE_URL}/apis/getRegistApiInfo`, // 获取注册api的信息

    // ===== 我的api =====//
    GET_APPLY_LIST: `${DATA_API_BASE_URL}/apply/userApplyList`, // 获取申请列表
    UPDATE_APPLY_STATUS: `${DATA_API_BASE_URL}/apply/updateApplyStatusForNormal`, // 用户停用，启用,禁用
    UPDATE_APPLY_STATUS_ADMIN: `${DATA_API_BASE_URL}/apply/updateApplyStatusForManager`, // 管理员停用，启用,禁用
    GET_USER_API_CALL_INFO: `${DATA_API_BASE_URL}/apis/getApiCallInfoForNormal`, // 用户获取api调用情况
    GET_USER_API_CALL_INFO_ADMIN: `${DATA_API_BASE_URL}/apis/getApiCallInfoForManager`, // 管理员获取api调用情况
    GET_API_CALL_ERROR_INFO: `${DATA_API_BASE_URL}/log/getApiCallErrorInfoForNormal`, // 获取api错误统计信息
    GET_API_CALL_ERROR_LOG: `${DATA_API_BASE_URL}/log/queryApiCallLogForNormal`, // 用户获取api错误统计日志
    GET_API_CALL_ERROR_LOG_ADMIN: `${DATA_API_BASE_URL}/log/queryApiCallLogForManager`, // 管理员获取api错误统计日志
    GET_API_CALL_URL: `${DATA_API_BASE_URL}/apis/getApiCallUrl`, // 获取api调用方式
    GET_API_CREATOR_INFO: `${DATA_API_BASE_URL}/apis/getApiCreatorInfo`, // 获取api创建人的信息
    GET_API_TIME_INFO: `${DATA_API_BASE_URL}/apis/getApiTimeInfoForNormal`, // api调用延迟
    GET_USER_SK_INFO: `${DATA_API_BASE_URL}/skAuth/getUserSkInfo`, // 获取用户appKey，appSecret信息
    RESET_APP_SECRET: `${DATA_API_BASE_URL}/skAuth/resetAppSecret`, // 重置app secret
    GENERATE_SECRET: `${DATA_API_BASE_URL}/skAuth/generateSkInfo`, // 生成app secret
    RESET_TOKEN: `${DATA_API_BASE_URL}/apply/resetApplyTokenForNormal`, // 重置token

    // ===== 市场信息 =====//
    GET_CATALOGUE: `${DATA_API_BASE_URL}/catalogue/getCatalogue`, // 获取市场分类信息
    GET_API_MARKET_LIST: `${DATA_API_BASE_URL}/apis/listByCondition`, // 获取市场api列表
    GET_MARKET_API_DETAIL: `${DATA_API_BASE_URL}/apis/getApiDetail`, // 获取市场api详情
    GET_API_EXT_INFO: `${DATA_API_BASE_URL}/apis/getApiExtInfoForNormal`, // 用户获取api详情调用情况等
    GET_API_EXT_INFO_ADMIN: `${DATA_API_BASE_URL}/apis/getApiExtInfoForManager`, // 管理员获取api详情调用情况等
    APPLY_API: `${DATA_API_BASE_URL}/apply/apiApply`, // 申请API
    GET_MARKET_TOP_CALL_FUNC: `${DATA_API_BASE_URL}/apis/listApiCallNumTopNForManager`, // 管理员获取api调用次数topN
    // ===== 概览 =====//
    GET_API_FAIL_RANK: `${DATA_API_BASE_URL}/apis/listApiCallFailRateTopNForNormal`, // 用户获取API调用失败率排行
    GET_API_FAIL_RANK_ADMIN: `${DATA_API_BASE_URL}/apis/listApiCallFailRateTopNForManager`, // 管理员获取API调用失败率排行
    GET_API_CALL_INFO: `${DATA_API_BASE_URL}/apis/getApiCallInfoForNormal`, // 获取API调用情况

    GET_USER_API_CALL_RANK: `${DATA_API_BASE_URL}/apis/listApiCallNumTopN`, // 获取用户个人API调用次数排行
    GET_USER_API_SUB_INFO: `${DATA_API_BASE_URL}/apply/getApiSubscribe`, // 获取用户个人API订购与审核情况

    GET_MARKET_API_CALL_RANK: `${DATA_API_BASE_URL}/apis/listUserCallTopN`, // 获取市场API调用次数用户排行
    GET_API_CALL_ERROR_INFO_ADMIN: `${DATA_API_BASE_URL}/log/getApiCallErrorInfoForManager`, // 获取市场API错误分布
    GET_MARKET_API_APPLY_INFO: `${DATA_API_BASE_URL}/apply/getApplyCount`, // 获取API申请记录信息

    // ===== 用户相关 ===== //
    DA_GET_USER_BY_ID: `${DATA_API_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DA_GET_USER_AUTH_BY_ID: `${DATA_API_BASE_URL}/user/getUserById`, // 根据用户ID获取用户权限
    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/pageQuery`, // 通过查询数据源
    CHECK_CONNECTION: `${DATA_API_BASE_URL}/dataSource/checkConnection`, // 检查数据库连接
    ADD_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/addSource`, // 新增数据源
    UPDATE_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/updateSource`, // 新增数据源
    DELETE_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/deleteSource`, // 删除数据源
    CHECK_DATASOURCE_PERMISSION: `${DATA_API_BASE_URL}/dataSource/checkPermission`, // 检查数据源是否有权限
    GET_DATA_SOURCES_LIST: `${DATA_API_BASE_URL}/dataSource/list`, // 查询已添加的数据源类型
    GET_DATA_SOURCES_TYPE: `${DATA_API_BASE_URL}/dataSource/getTypes`, // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DATA_API_BASE_URL}/dataSource/tablelist`, // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DATA_API_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PART: `${DATA_API_BASE_URL}/dataSource/getPartValue`, // 查询数据源下的分区
    GET_DATA_SOURCES_PREVIEW: `${DATA_API_BASE_URL}/dataSource/preview`, // 预览数据源下的数据,
    GET_DATA_SOURCES_CHAR_TYPE: `${DATA_API_BASE_URL}/apis/getSourceColumnType`, // 获取数据库字段类型

    // ===== webservice获取 ===== //
    GET_WSD_XML: `${DATA_API_BASE_URL}/dataSource/apis/getWsdlXml` // 注册API中webservice中的接口详情获取
}
