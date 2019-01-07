// 从config文件全局读取
import { DATA_API_BASE_URL } from 'config/base';

export default {

    // ===== common ===== //
    GET_ALL_MENU_LIST: `${DATA_API_BASE_URL}/user/showMenuList`, // 获取所有功能菜单
    // ===== api授权审批 ====//
    HANDLE_APPLY: `${DATA_API_BASE_URL}/apply/handleApply`, // 审批
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
    FORMAT_SQL: `${DATA_API_BASE_URL}/apis/sqlFormat`, // 格式化sql
    PARSER_SQL: `${DATA_API_BASE_URL}/apis/sqlParser`, // sql解析
    TEST_API: `${DATA_API_BASE_URL}/apis/apiTest`, // api测试
    CHECK_API_IS_EXIST: `${DATA_API_BASE_URL}/apis/checkNameExist`, // api名称是否存在
    GET_API_TIME_INFO_FOR_MANAGER: `${DATA_API_BASE_URL}/apis/getApiTimeInfoForManager`, // api调用延迟
    GET_API_CONFIG_INFO: `${DATA_API_BASE_URL}/apis/getDataSourceInfo`, // 获取api的配置信息

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
    GET_DATA_SOURCES_PREVIEW: `${DATA_API_BASE_URL}/dataSource/preview` // 预览数据源下的数据

}
