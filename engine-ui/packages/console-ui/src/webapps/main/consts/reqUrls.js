import {
    UIC_BASE_URL,
    DATA_API_BASE_URL,
    STREAM_BASE_URL,
    RDOS_BASE_URL,
    DQ_BASE_URL,
    DL_BASE_URL,
    CONSOLE_BASE_URL
} from 'config/base';

export default {
    // ===== 用户模块 ===== //
    LOGOUT: `${UIC_BASE_URL}/v2/logout`,
    CHECKISROOT: `${CONSOLE_BASE_URL}/status/status`, // 验证是否为租户root管理员

    //= ==== 开发套件 ====//
    RDOS_SEARCH_UIC_USERS: `${RDOS_BASE_URL}/common/project/getUicUsersNotInProject`,
    RDOS_ADD_USER: `${RDOS_BASE_URL}/common/roleUser/addRoleUserNew`,

    //= ==== 数据质量 ====//
    DQ_SEARCH_UIC_USERS: `${DQ_BASE_URL}/user/getUicUsersNotInProject`,
    DQ_ADD_USER: `${DQ_BASE_URL}/roleUser/addRoleUserNew`,

    //= ==== 数据api ====//
    //* *消息**//
    DATAAPI_MASSAGE_QUERY: `${DATA_API_BASE_URL}/notify/pageQuery`,
    DATAAPI_GET_MASSAGE_BY_ID: `${DATA_API_BASE_URL}/notify/getOne`,
    DATAAPI_MASSAGE_MARK_AS_READ: `${DATA_API_BASE_URL}/notify/tabRead`,
    DATAAPI_MASSAGE_MARK_AS_ALL_READ: `${DATA_API_BASE_URL}/notify/allRead`,
    DATAAPI_MASSAGE_DELETE: `${DATA_API_BASE_URL}/notify/delete`,

    //* * 用户角色 *//
    DATAAPI_QUERY_USER: `${DATA_API_BASE_URL}/user/pageQuery`, // 查询系统用户信息
    DATAAPI_REMOVE_USER: `${DATA_API_BASE_URL}/roleUser/remove`, // 删除用户
    DATAAPI_UPDATE_USER_ROLE: `${DATA_API_BASE_URL}/roleUser/updateUserRole`, // 更改用户角色
    DATAAPI_ADD_USER: `${DATA_API_BASE_URL}/roleUser/addRoleUserNew`, // 添加用户
    DATAAPI_ROLE_QUERY: `${DATA_API_BASE_URL}/role/pageQuery`, // 角色列表
    DATAAPI_GET_ROLE_TREE: `${DATA_API_BASE_URL}/permission/tree`, // 获取权限树
    DATAAPI_ROLE_PERMISSION: `${DATA_API_BASE_URL}/permission/getPermissionIdsByRoleId`, // 获取角色的权限
    DATAAPI_ROLE_PERMISSION_ADD_OR_EDIT: `${DATA_API_BASE_URL}/role/addOrUpdateRole`, // 更新或添加角色权限
    DATAAPI_REMOVE_ROLE: `${DATA_API_BASE_URL}/role/deleteRole`, // 删除角色
    DATAAPI_SEARCH_UIC_USERS: `${DATA_API_BASE_URL}/user/getUicUsersNotInProject`, // 获取UIC用户列表

    //= ==== 数据标签 ====//
    //* * 消息 **//
    DL_MASSAGE_QUERY: `${DL_BASE_URL}/notify/pageQuery`,
    DL_GET_MASSAGE_BY_ID: `${DL_BASE_URL}/notify/getOne`,
    DL_MASSAGE_MARK_AS_READ: `${DL_BASE_URL}/notify/tabRead`,
    DL_MASSAGE_MARK_AS_ALL_READ: `${DL_BASE_URL}/notify/allRead`,
    DL_MASSAGE_DELETE: `${DL_BASE_URL}/notify/delete`,

    //* * 用户角色 *//
    DL_QUERY_USER: `${DL_BASE_URL}/user/pageQuery`, // 查询系统用户信息
    DL_REMOVE_USER: `${DL_BASE_URL}/roleUser/remove`, // 删除用户
    DL_ADD_USER: `${DL_BASE_URL}/roleUser/addRoleUserNew`, // 添加用户
    DL_UPDATE_USER_ROLE: `${DL_BASE_URL}/roleUser/updateUserRole`, // 更改用户角色
    DL_ROLE_QUERY: `${DL_BASE_URL}/role/pageQuery`, // 角色列表
    DL_GET_ROLE_TREE: `${DL_BASE_URL}/permission/tree`, // 获取权限树
    DL_ROLE_PERMISSION: `${DL_BASE_URL}/permission/getPermissionIdsByRoleId`, // 获取角色的权限
    DL_ROLE_PERMISSION_ADD_OR_EDIT: `${DL_BASE_URL}/role/addOrUpdateRole`, // 更新或添加角色权限
    DL_REMOVE_ROLE: `${DL_BASE_URL}/role/deleteRole`, // 删除角色
    DL_SEARCH_UIC_USERS: `${DL_BASE_URL}/user/getUicUsersNotInProject`, // 获取UIC

    //= ==== stream ====//
    //* *消息**//
    STREAM_MASSAGE_QUERY: `${STREAM_BASE_URL}/notifyRecord/pageQuery`,
    STREAM_GET_MASSAGE_BY_ID: `${STREAM_BASE_URL}/notifyRecord/getOne`,
    STREAM_MASSAGE_MARK_AS_READ: `${STREAM_BASE_URL}/notifyRecord/tabRead`,
    STREAM_MASSAGE_MARK_AS_ALL_READ: `${STREAM_BASE_URL}/notifyRecord/allRead`,
    STREAM_MASSAGE_DELETE: `${STREAM_BASE_URL}/notifyRecord/delete`,

    //* * 用户角色 *//
    STREAM_QUERY_USER: `${STREAM_BASE_URL}/project/getProjectUsers`, // 查询系统用户信息
    STREAM_REMOVE_USER: `${STREAM_BASE_URL}/roleUser/removeRoleUserFromProject`, // 删除用户
    STREAM_UPDATE_USER_ROLE: `${STREAM_BASE_URL}/roleUser/updateUserRole`, // 更改用户角色
    STREAM_ADD_USER: `${STREAM_BASE_URL}/roleUser/addRoleUserNew`, // 添加用户

    STREAM_ROLE_QUERY: `${STREAM_BASE_URL}/role/pageQuery`, // 角色列表
    STREAM_GET_ROLE_TREE: `${STREAM_BASE_URL}/permission/tree`, // 获取权限树
    STREAM_ROLE_PERMISSION: `${STREAM_BASE_URL}/permission/getPermissionIdsByRoleId`, // 获取角色的权限
    STREAM_ROLE_PERMISSION_ADD_OR_EDIT: `${STREAM_BASE_URL}/role/addOrUpdateRole`, // 更新或添加角色权限
    STREAM_REMOVE_ROLE: `${STREAM_BASE_URL}/role/deleteRole`, // 删除角色
    STREAM_SEARCH_UIC_USERS: `${STREAM_BASE_URL}/project/getUicUsersNotInProject` // 获取UIC用户列表
}
