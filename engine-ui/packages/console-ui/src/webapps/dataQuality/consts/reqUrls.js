 // API 地址
 // '/api/dq/service';
 const DQ_BASE_URL = 'http://api.dtstack.com/server/index.php?g=Web&c=Mock&o=simple&projectID=3&uri=/api/dq/service'; // 从config文件全局读取

export default {

    // ===== 数据质量 ===== //
    DQ_ROLE_QUERY: `${DQ_BASE_URL}/role/pageQuery`,
    DQ_ROLE_UPDATE: `${DQ_BASE_URL}/role/pageQuery`,
    DQ_ROLE_DELETE: `${DQ_BASE_URL}/role/deleteRole`,
    DQ_ROLE_PERMISSION_TREE: `${DQ_BASE_URL}/permission/tree`,
    DQ_ROLE_PERMISSION: `${DQ_BASE_URL}/permission/getPermissionIdsByRoleId`,
    
    DQ_GET_USER_BY_ID: `${DQ_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DQ_USER_ROLE_ADD: `${DQ_BASE_URL}/roleUser/addRoleUser`,
    DQ_USER_ROLE_DELETE: `${DQ_BASE_URL}/roleUser/deleteRole`,
    DQ_USER_ROLE_UPDATE: `${DQ_BASE_URL}/roleUser/updateUserRole`,

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/pageQuery`, // 查询数据源
}
