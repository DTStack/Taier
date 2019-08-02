// 从config文件全局读取
import { DL_BASE_URL } from 'config/base';

export default {

    // ===== 系统管理 ===== //
    DL_ROLE_QUERY: `${DL_BASE_URL}/role/pageQuery`,
    DL_ROLE_UPDATE: `${DL_BASE_URL}/role/addOrUpdateRole`,
    DL_ROLE_DELETE: `${DL_BASE_URL}/role/deleteRole`,
    DL_ROLE_PERMISSION_TREE: `${DL_BASE_URL}/permission/tree`,
    DL_ROLE_PERMISSION: `${DL_BASE_URL}/permission/getPermissionIdsByRoleId`,

    // ===== 用户相关 ===== //
    DL_GET_USER_BY_ID: `${DL_BASE_URL}/user/getUserById`,
    DL_GET_USER_PAGES: `${DL_BASE_URL}/user/pageQuery`,
    DL_GET_USER_NOT_IN_PROJECT: `${DL_BASE_URL}/user/listNotIn`,
    DL_USER_ROLE_ADD: `${DL_BASE_URL}/roleUser/addRoleUser`,
    DL_USER_ROLE_DELETE: `${DL_BASE_URL}/roleUser/remove`,
    DL_USER_ROLE_UPDATE: `${DL_BASE_URL}/roleUser/updateUserRole`,

    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${DL_BASE_URL}/notify/pageQuery`,
    GET_MASSAGE_BY_ID: `${DL_BASE_URL}/notify/getOne`,
    MASSAGE_MARK_AS_READ: `${DL_BASE_URL}/notify/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${DL_BASE_URL}/notify/allRead`,
    MASSAGE_DELETE: `${DL_BASE_URL}/notify/delete`,

    // ===== common ===== //
    DL_GET_USER_LIST: `${DL_BASE_URL}/user/list`, // 获取所有用户
    GET_ALL_MENU_LIST: `${DL_BASE_URL}/user/showMenuList`, // 获取所有功能菜单
    GET_PERIOD_TYPE: `${DL_BASE_URL}/tag/listPeriodType`, // 获取调度类型
    GET_NOTIFY_TYPE: `${DL_BASE_URL}/tag/listNotifyType`, // 获取通知类型

    // ===== 标签注册 ===== //
    QUERY_REGISTERED_TAG: `${DL_BASE_URL}/tag/registerQuery`, // 查询注册标签
    ADD_REGISTER_TAG: `${DL_BASE_URL}/tag/registerAdd`, // 新增注册标签

    // ===== 规则标签 ===== //
    QUERY_RULE_TAG: `${DL_BASE_URL}/tag/customQuery`, // 查询规则标签
    ADD_RULE_TAG: `${DL_BASE_URL}/tag/customAdd`, // 新增规则标签
    UPDATE_TAG_SQL_INFO: `${DL_BASE_URL}/tag/computeInfoUpdate`, // 更新标签计算信息
    GET_TAG_CONDITION: `${DL_BASE_URL}/tag/listCustomCondition`, // 获取过滤条件
    EDIT_TAG_CONDITION: `${DL_BASE_URL}/tag/editCustomCondition`, // 编辑过滤条件
    DELETE_TAG_CONDITION: `${DL_BASE_URL}/tag/deleteCustomCondition`, // 删除过滤条件

    // ===== 标签 ===== //
    UPDATE_TAG_BASE_INFO: `${DL_BASE_URL}/tag/baseInfoUpdate`, // 更新标签基本信息
    DELETE_TAG: `${DL_BASE_URL}/tag/deleteTag`, // 删除标签
    GET_RULE_TAG_RETAIL: `${DL_BASE_URL}/tag/getOne`, // 获取标签详情
    QUERY_TAG_LOG: `${DL_BASE_URL}/tag/logPageQuery`, // 查询标签更新日志
    PUBLISH_CHECK: `${DL_BASE_URL}/tag/publishCheck`, // 标签是否可发布
    PUBLISH_TAG: `${DL_BASE_URL}/tag/publishTag`, // 发布标签

    // ===== 识别列 ===== //
    QUERY_IDENTIFY_COLUMN: `${DL_BASE_URL}/identify/pageQuery`, // 查询识别列
    GET_ALL_IDENTIFY_COLUMN: `${DL_BASE_URL}/identify/identities`, // 获取所有识别列
    ADD_IDENTIFY_COLUMN: `${DL_BASE_URL}/identify/add`, // 新增识别列
    UPDATE_IDENTIFY_COLUMN: `${DL_BASE_URL}/identify/update`, // 更新识别列
    DELETE_IDENTIFY_COLUMN: `${DL_BASE_URL}/identify/delete`, // 删除识别列

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DL_BASE_URL}/dataSource/pageQuery`, // 查询数据源
    CHECK_CONNECTION: `${DL_BASE_URL}/dataSource/checkConnection`, // 检查连通性
    ADD_DATA_SOURCE: `${DL_BASE_URL}/dataSource/addSource`, // 新增数据源
    UPDATE_DATA_SOURCE: `${DL_BASE_URL}/dataSource/updateSource`, // 更新数据源
    DELETE_DATA_SOURCE: `${DL_BASE_URL}/dataSource/deleteSource`, // 删除数据源
    GET_DATA_SOURCE_BY_ID: `${DL_BASE_URL}/dataSource/getBySourceId`, // 获取数据源By Id

    GET_DATA_SOURCES_LIST: `${DL_BASE_URL}/dataSource/list`, // 查询已添加的数据源
    GET_TAG_DATA_SOURCES_LIST: `${DL_BASE_URL}/dataSource/listTargetSource`, // 查询标签可添加的数据源
    GET_DATA_SOURCES_TYPE: `${DL_BASE_URL}/dataSource/getTypes`, // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DL_BASE_URL}/dataSource/tablelist`, // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DL_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_COMPUTE_SOURCE: `${DL_BASE_URL}/dataSource/listComputeSource`, // 获取计算资源
    GET_DATA_SOURCES_PREVIEW: `${DL_BASE_URL}/dataSource/preview`, // 预览数据源下的数据

    // ===== 概览 =====//
    GET_API_FAIL_RANK: `${DL_BASE_URL}/apis/listApiCallFailRateTopNForNormal`, // 用户获取API调用失败率排行
    GET_API_FAIL_RANK_ADMIN: `${DL_BASE_URL}/apis/listApiCallFailRateTopNForManager`, // 管理员获取API调用失败率排行
    GET_API_CALL_INFO: `${DL_BASE_URL}/apis/getApiCallInfoForNormal`, // 获取API调用情况
    GET_USER_API_CALL_RANK: `${DL_BASE_URL}/apis/listApiCallNumTopN`, // 获取用户个人API调用次数排行
    GET_USER_API_SUB_INFO: `${DL_BASE_URL}/apply/getApiSubscribe`, // 获取用户个人API订购与审核情况
    GET_MARKET_API_CALL_RANK: `${DL_BASE_URL}/apis/listUserCallTopN`, // 获取市场API调用次数用户排行
    GET_API_CALL_ERROR_INFO_ADMIN: `${DL_BASE_URL}/log/getApiCallErrorInfoForManager`, // 获取市场API错误分布
    GET_MARKET_API_APPLY_INFO: `${DL_BASE_URL}/apply/getApplyCount`, // 获取API申请记录信息

    // ===== api授权审批 ====//
    HANDLE_APPLY: `${DL_BASE_URL}/apply/handleApply`, // 审批
    GET_ALL_APPLY_LIST: `${DL_BASE_URL}/apply/allApplyList`, // 获取审批列表

    // ===== api管理 ===== //
    GET_ALL_API_LIST: `${DL_BASE_URL}/tag/manageQuery`, // 获取所有管理的api
    GET_DATASOURCE_BASE_INFO: `${DL_BASE_URL}/dataSource/listDataSourceBaseInfo`, // 根据数据类型获取数据源
    DELETE_API: `${DL_BASE_URL}/apis/deleteApi`, // 删除api
    UPDATE_API: `${DL_BASE_URL}/apis/updateApiStatus`, // 更新api状态
    GET_API_CALL_RANK: `${DL_BASE_URL}/apis/getApiCallUserRankList`, // 获取api调用排行
    GET_API_BUY_STATE: `${DL_BASE_URL}/apply/getApiApplyList`, // 获取api订购状态
    DELETE_CATAGORY: `${DL_BASE_URL}/catalogue/deleteCatalogue`, // 删除节点
    ADD_CATAGORY: `${DL_BASE_URL}/catalogue/addCatalogue`, // 添加节点
    UPDATE_CATAGORY: `${DL_BASE_URL}/catalogue/updateCatalogue`, // 更新节点
    NEW_API: `${DL_BASE_URL}/apis/createApi`, // 新建api
    CHANGE_API: `${DL_BASE_URL}/apis/updateApi`, // 更新api信息
    GET_TABLE_BY_DATASOURCE: `${DL_BASE_URL}/dataSource/tablelist`, // 根据数据源获取表
    GET_TABLE_COLUMNS_DETAIL: `${DL_BASE_URL}/dataSource/tablecolumn`, // 获取表字段信息
    GET_TABLE_PREVIEW_DATA: `${DL_BASE_URL}/dataSource/preview`, // 数据预览
    GET_API_DETAIL_INFO: `${DL_BASE_URL}/apis/getApiInfo`, // 获取api详细信息

    // ===== 我的api =====//
    GET_APPLY_LIST: `${DL_BASE_URL}/apply/userApplyList`, // 获取申请列表
    UPDATE_APPLY_STATUS: `${DL_BASE_URL}/apply/updateApplyStatusForNormal`, // 用户停用，启用,禁用
    UPDATE_APPLY_STATUS_ADMIN: `${DL_BASE_URL}/apply/updateApplyStatusForManager`, // 管理员停用，启用,禁用
    GET_USER_API_CALL_INFO: `${DL_BASE_URL}/apis/getApiCallInfoForNormal`, // 用户获取api调用情况
    GET_USER_API_CALL_INFO_ADMIN: `${DL_BASE_URL}/apis/getApiCallInfoForManager`, // 管理员获取api调用情况
    GET_API_CALL_ERROR_INFO: `${DL_BASE_URL}/log/getApiCallErrorInfoForNormal`, // 获取api错误统计信息
    GET_API_CALL_ERROR_LOG: `${DL_BASE_URL}/log/queryApiCallLogForNormal`, // 用户获取api错误统计日志
    GET_API_CALL_ERROR_LOG_ADMIN: `${DL_BASE_URL}/log/queryApiCallLogForManager`, // 管理员获取api错误统计日志
    GET_API_CALL_URL: `${DL_BASE_URL}/apis/getApiCallUrl`, // 获取api调用方式
    GET_API_CREATOR_INFO: `${DL_BASE_URL}/apis/getApiCreatorInfo`, // 获取api创建人的信息

    // ===== 市场信息 =====//
    GET_CATALOGUE: `${DL_BASE_URL}/catalogue/getCatalogue`, // 获取市场分类信息
    GET_API_MARKET_LIST: `${DL_BASE_URL}/tag/marketQuery`, // 获取市场api列表
    GET_MARKET_API_DETAIL: `${DL_BASE_URL}/apis/getApiDetail`, // 获取市场api详情
    GET_API_EXT_INFO: `${DL_BASE_URL}/apis/getApiExtInfoForNormal`, // 用户获取api详情调用情况等
    GET_API_EXT_INFO_ADMIN: `${DL_BASE_URL}/apis/getApiExtInfoForManager`, // 管理员获取api详情调用情况等
    APPLY_API: `${DL_BASE_URL}/apply/apiApply`, // 申请API
    GET_MARKET_TOP_CALL_FUNC: `${DL_BASE_URL}/apis/listApiCallNumTopNForManager`// 管理员获取api调用次数topN

}
