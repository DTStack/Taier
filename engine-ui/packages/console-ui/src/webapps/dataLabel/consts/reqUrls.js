// 从config文件全局读取
import { DL_BASE_URL } from 'config/base';

export default {

    // ===== common ===== //
    DL_GET_USER_LIST: `${DL_BASE_URL}/user/list`,                     // 获取所有用户
    GET_ALL_MENU_LIST:`${DL_BASE_URL}/user/showMenuList`,       //获取所有功能菜单

    // ===== 系统管理 ===== //
    DL_ROLE_QUERY: `${DL_BASE_URL}/role/pageQuery`,
    DL_ROLE_UPDATE: `${DL_BASE_URL}/role/addOrUpdateRole`,
    DL_ROLE_DELETE: `${DL_BASE_URL}/role/deleteRole`,
    DL_ROLE_PERMISSION_TREE: `${DL_BASE_URL}/permission/tree`,
    DL_ROLE_PERMISSION: `${DL_BASE_URL}/permission/getPermissionIdsByRoleId`,

    // ===== 用户相关 ===== //
    DL_GET_USER_BY_ID: `${DL_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
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

    // ===== 总览 ===== //

    // ===== 标签注册 ===== //
    QUERY_REGISTERED_TAG: `${DL_BASE_URL}/tag/registerQuery`,         // 查询注册标签
    ADD_REGISTER_TAG: `${DL_BASE_URL}/tag/registerAdd`,               // 新增注册标签
    UPDATE_TAG: `${DL_BASE_URL}/tag/update`,                          // 更新标签
    DELETE_TAG: `${DL_BASE_URL}/tag/deleteTag`,                       // 删除标签

    // ===== 标签生成 ===== //
    QUERY_RULE_TAG: `${DL_BASE_URL}/tag/customQuery`,                 // 查询生成标签
    ADD_RULE_TAG: `${DL_BASE_URL}/tag/customAdd`,                     // 新增标签生成
    UPDATE_TAG: `${DL_BASE_URL}/tag/update`,                          // 更新标签
    DELETE_TAG: `${DL_BASE_URL}/tag/deleteTag`,                       // 删除标签
    GET_RULE_TAG_RETAIL: `${DL_BASE_URL}/tag/getOne`,                 // 标签生成详情
    EDIT_TAG_CONDITION: `${DL_BASE_URL}/tag/editCustomCondition`,     // 编辑过滤条件
    DELETE_TAG_CONDITION: `${DL_BASE_URL}/tag/deleteCustomCondition`, // 删除过滤条件

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DL_BASE_URL}/dataSource/pageQuery`,          // 查询数据源
    CHECK_CONNECTION: `${DL_BASE_URL}/dataSource/checkConnection`,    // 检查连通性
    ADD_DATA_SOURCE: `${DL_BASE_URL}/dataSource/addSource`,           // 新增数据源
    UPDATE_DATA_SOURCE: `${DL_BASE_URL}/dataSource/updateSource`,     // 更新数据源
    DELETE_DATA_SOURCE: `${DL_BASE_URL}/dataSource/deleteSource`,     // 删除数据源
    GET_DATA_SOURCE_BY_ID: `${DL_BASE_URL}/dataSource/getBySourceId`, // 获取数据源By Id

    GET_DATA_SOURCES_LIST: `${DL_BASE_URL}/dataSource/list`,          // 查询已添加的数据源类型
    GET_DATA_SOURCES_TYPE: `${DL_BASE_URL}/dataSource/getTypes`,      // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DL_BASE_URL}/dataSource/tablelist`,    // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DL_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PREVIEW: `${DL_BASE_URL}/dataSource/preview`,    // 预览数据源下的数据

    // ===== api授权审批 ====//
    HANDLE_APPLY: `${DL_BASE_URL}/apply/handleApply`,//审批
    GET_ALL_APPLY_LIST: `${DL_BASE_URL}/apply/allApplyList`,//获取审批列表

    // ===== api管理 ===== //
    GET_ALL_API_LIST: `${DL_BASE_URL}/tag/customQuery`,//获取所有的api
    GET_DATASOURCE_BASE_INFO: `${DL_BASE_URL}/dataSource/listDataSourceBaseInfo`,//根据数据类型获取数据源
    DELETE_API: `${DL_BASE_URL}/apis/deleteApi`,//删除api
    UPDATE_API: `${DL_BASE_URL}/apis/updateApiStatus`,//更新api状态
    GET_API_CALL_RANK: `${DL_BASE_URL}/apis/getApiCallUserRankList`,//获取api调用排行
    GET_API_BUY_STATE: `${DL_BASE_URL}/apply/getApiApplyList`,//获取api订购状态
    DELETE_CATAGORY: `${DL_BASE_URL}/catalogue/deleteCatalogue`,//删除节点
    ADD_CATAGORY: `${DL_BASE_URL}/catalogue/addCatalogue`,//添加节点
    UPDATE_CATAGORY: `${DL_BASE_URL}/catalogue/updateCatalogue`,//更新节点
    NEW_API: `${DL_BASE_URL}/apis/createApi`,//新建api
    CHANGE_API: `${DL_BASE_URL}/apis/updateApi`,//更新api信息
    GET_TABLE_BY_DATASOURCE: `${DL_BASE_URL}/dataSource/tablelist`,//根据数据源获取表
    GET_TABLE_COLUMNS_DETAIL: `${DL_BASE_URL}/dataSource/tablecolumn`,//获取表字段信息
    GET_TABLE_PREVIEW_DATA: `${DL_BASE_URL}/dataSource/preview`,//数据预览
    GET_API_DETAIL_INFO: `${DL_BASE_URL}/apis/getApiInfo`,//获取api详细信息

    // ===== 我的api =====//
    GET_APPLY_LIST: `${DL_BASE_URL}/apply/userApplyList`,//获取申请列表
    UPDATE_APPLY_STATUS: `${DL_BASE_URL}/apply/updateApplyStatusForNormal`,//用户停用，启用,禁用
    UPDATE_APPLY_STATUS_ADMIN: `${DL_BASE_URL}/apply/updateApplyStatusForManager`,//管理员停用，启用,禁用
    GET_USER_API_CALL_INFO: `${DL_BASE_URL}/apis/getApiCallInfoForNormal`,//用户获取api调用情况
    GET_USER_API_CALL_INFO_ADMIN: `${DL_BASE_URL}/apis/getApiCallInfoForManager`,//管理员获取api调用情况
    GET_API_CALL_ERROR_INFO: `${DL_BASE_URL}/log/getApiCallErrorInfoForNormal`,//获取api错误统计信息
    GET_API_CALL_ERROR_LOG: `${DL_BASE_URL}/log/queryApiCallLogForNormal`,//用户获取api错误统计日志
    GET_API_CALL_ERROR_LOG_ADMIN: `${DL_BASE_URL}/log/queryApiCallLogForManager`,//管理员获取api错误统计日志
    GET_API_CALL_URL: `${DL_BASE_URL}/apis/getApiCallUrl`,//获取api调用方式
    GET_API_CREATOR_INFO: `${DL_BASE_URL}/apis/getApiCreatorInfo`,//获取api创建人的信息

    // ===== 市场信息 =====//
    GET_CATALOGUE: `${DL_BASE_URL}/catalogue/getCatalogue`,//获取市场分类信息
    GET_API_MARKET_LIST: `${DL_BASE_URL}/tag/pageQuery`,//获取市场api列表
    GET_MARKET_API_DETAIL: `${DL_BASE_URL}/apis/getApiDetail`,//获取市场api详情
    GET_API_EXT_INFO: `${DL_BASE_URL}/apis/getApiExtInfoForNormal`,//用户获取api详情调用情况等
    GET_API_EXT_INFO_ADMIN: `${DL_BASE_URL}/tag/getApiExtInfoForManager`,//管理员获取api详情调用情况等
    APPLY_API: `${DL_BASE_URL}/apply/tagApply`,//申请API
    GET_MARKET_TOP_CALL_FUNC: `${DL_BASE_URL}/apis/listApiCallNumTopNForManager`,//管理员获取api调用次数topN

}
