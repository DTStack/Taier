 // API 地址
 // '/api/dq/service';
 //const DQ_BASE_URL = 'http://api.dtstack.com/server/index.php?g=Web&c=Mock&o=simple&projectID=3&uri=/api/dq/service'; // 从config文件全局读取
const DQ_BASE_URL = '/api/dq/service'; // 从config文件全局读取

export default {

    // ===== 系统管理 ===== //
    DQ_ROLE_QUERY: `${DQ_BASE_URL}/role/pageQuery`,
    DQ_ROLE_UPDATE: `${DQ_BASE_URL}/role/pageQuery`,
    DQ_ROLE_DELETE: `${DQ_BASE_URL}/role/deleteRole`,
    DQ_ROLE_PERMISSION_TREE: `${DQ_BASE_URL}/permission/tree`,
    DQ_ROLE_PERMISSION: `${DQ_BASE_URL}/permission/getPermissionIdsByRoleId`,

    DQ_GET_USER_BY_ID: `${DQ_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DQ_USER_ROLE_ADD: `${DQ_BASE_URL}/roleUser/addRoleUser`,
    DQ_USER_ROLE_DELETE: `${DQ_BASE_URL}/roleUser/deleteRole`,
    DQ_USER_ROLE_UPDATE: `${DQ_BASE_URL}/roleUser/updateUserRole`,
    DQ_GET_USER_LIST: `${DQ_BASE_URL}/user/list`,


    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/pageQuery`, // 查询数据源
    GET_DATA_SOURCES_TYPE: `${DQ_BASE_URL}/dataSource/list`, // 查询数据源类型
    GET_DATA_SOURCES_TABLE: `${DQ_BASE_URL}/dataSource/tablelist`, // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DQ_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PART: `${DQ_BASE_URL}/dataSource/getPartValue`, // 查询数据源下的分区
    GET_DATA_SOURCES_PREVIEW: `${DQ_BASE_URL}/dataSource/preview`, // 预览数据源下的数据
    

    // ===== 逐行校验 ===== //
    GET_CHECK_LIST: `${DQ_BASE_URL}/verify/pageQuery`,       // 逐行校验列表数据
    GET_CHECK_DETAIL: `${DQ_BASE_URL}/verify/getOne`,       // 单个数据详情
    EDIT_CHECK: `${DQ_BASE_URL}/verify/add`,             // 编辑逐行校验
    DELETE_CHECK: `${DQ_BASE_URL}/verify/deleteVerify`,  // 删除逐行校验
    GET_CHECK_REPORT: `${DQ_BASE_URL}/verify/getReport`, // 获取报告
}
