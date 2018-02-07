const baseUrl = APP_CONF.API_BASE_URL || ''; // 从config文件全局读取

export default {
    // ===== 用户模块 ===== //
    LOGOUT: `/uic/api/v2/logout`,
    APP_LOGOUT: `${baseUrl}/login/out`,
    GET_USER_BY_ID: `${baseUrl}/common/user/getUserById`, // 根据用户ID获取用户
    
    // ===== 角色管理 ===== //
    UPDATE_USER_ROLE: `${baseUrl}/common/roleUser/updateUserRole`, // 更新用户角色
    ADD_ROLE_USRE: `${baseUrl}/common/roleUser/addRoleUser`, // 项目用户
    GET_ROLE_LIST: `${baseUrl}/common/role/pageQuery`, // 获取角色列表
    UPDATE_ROLE: `${baseUrl}/common/role/addOrUpdateRole`, // 新建/更新角色
    DELETE_ROLE: `${baseUrl}/common/role/deleteRole`, // 删除角色
    GET_ROLE_TREE: `${baseUrl}/common/permission/tree`, // 编辑角色
    GET_ROLE_INFO: `${baseUrl}/common/permission/getPermissionIdsByRoleId`, // 获取角色信息

}
