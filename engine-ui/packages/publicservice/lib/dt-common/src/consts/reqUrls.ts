/**
 * UIC 请求 BASE 地址
 */
export const UIC_BASE_URL = '/uic/api';
export const RDOS_BASE_URL = '/api/rdos';
export const STREAM_BASE_URL = '/api/streamapp/service';
export const STREAM_BASE_URL_NOT_SERVICE = '/api/streamapp';
export const DQ_BASE_URL = '/api/dq/service';
export const DQ_BASE_URL_NOT_SERVICE = '/api/dq';
export const DATA_API_BASE_URL = '/api/da/service';
export const TAG_ENGINE_URL = '/api/v1'; // 标签引擎
export const CONSOLE_BASE_URL = '/api/console/service';
export const CONSOLE_BASE_UPLOAD_URL = '/api/console';
export const ANALYTICS_ENGINE_BASE_URL = '/api/analysis/service';
export const SCIENCE_BASE_URL = '/api/dataScience';

export default {
  // ===== license APP ===== //
  getLicenseApp: `${UIC_BASE_URL}/v2/license/menu`, // 获取具有license权限App // 暂缺
  CHECK_IS_OVERDUE: `${UIC_BASE_URL}/v2/license/verify`, // 检查是否过期 // 暂缺

  // ===== 用户模块 ===== //
  LOGOUT: `${UIC_BASE_URL}/v2/logout`,
  API_LOGOUT: `${DATA_API_BASE_URL}/login/out`,
  CHECKISROOT: `${CONSOLE_BASE_URL}/status/status`, // 验证是否为租户root管理员
  GET_FULL_TENANT: `${UIC_BASE_URL}/v2/account/user/get-full-tenants-by-name?tenantName=`, // 获取所有租户
  SWITCH_TENANT: `${UIC_BASE_URL}/v2/account/user/switch-tenant`, // 切换租户
  GET_PRODUCTS: `${UIC_BASE_URL}/v2/account/user/get-products`, // 获取产品权限

  // ===== 安全审计 ===== //
  GET_AUDIT_LIST: `${CONSOLE_BASE_URL}/securityAudit/pageQuery`, // 获取审计列表
  GET_OPERATION_LIST: `${CONSOLE_BASE_URL}/securityAudit/getOperationList`, // 获取动作

  //= ==== 开发套件 ====//
  RDOS_SEARCH_UIC_USERS: `${RDOS_BASE_URL}/common/project/getUicUsersNotInProject`,
  RDOS_ADD_USER: `${RDOS_BASE_URL}/common/roleUser/addRoleUserNew`,

  //= ==== 数据质量 ====//
  DQ_SEARCH_UIC_USERS: `${DQ_BASE_URL}/user/getUicUsersNotInProject`,
  DQ_ADD_USER: `${DQ_BASE_URL}/roleUser/addRoleUserNew`,
  DQ_ROLE_QUERY: `${DQ_BASE_URL}/role/pageQuery`,
  DQ_ROLE_UPDATE: `${DQ_BASE_URL}/role/addOrUpdateRole`,
  DQ_ROLE_DELETE: `${DQ_BASE_URL}/role/deleteRole`,
  DQ_ROLE_PERMISSION_TREE: `${DQ_BASE_URL}/permission/tree`,
  DQ_ROLE_PERMISSION: `${DQ_BASE_URL}/permission/getPermissionIdsByRoleId`,
  DQ_GET_USER_PAGES: `${DQ_BASE_URL}/user/pageQuery`,
  DQ_GET_PROJECT_LIST: `${DQ_BASE_URL}/project/getProjects`, // 获取当前用户有权限的项目列表
  DQ_USER_ROLE_DELETE: `${DQ_BASE_URL}/roleUser/remove`,
  DQ_USER_ROLE_UPDATE: `${DQ_BASE_URL}/roleUser/updateUserRole`,

  //* *消息**//
  DQ_MASSAGE_QUERY: `${DQ_BASE_URL}/notify/pageQuery`,
  DQ_GET_MASSAGE_BY_ID: `${DQ_BASE_URL}/notify/getOne`,
  DQ_MASSAGE_MARK_AS_READ: `${DQ_BASE_URL}/notify/tabRead`,
  DQ_MASSAGE_MARK_AS_ALL_READ: `${DQ_BASE_URL}/notify/allRead`,
  DQ_MASSAGE_DELETE: `${DQ_BASE_URL}/notify/delete`,

  //= ==== 数据api ====//
  //* *消息**//
  DATAAPI_MASSAGE_QUERY: `${DATA_API_BASE_URL}/notify/pageQuery`,
  DATAAPI_GET_MASSAGE_BY_ID: `${DATA_API_BASE_URL}/notify/getOne`,
  DATAAPI_MASSAGE_MARK_AS_READ: `${DATA_API_BASE_URL}/notify/tabRead`,
  DATAAPI_MASSAGE_MARK_AS_ALL_READ: `${DATA_API_BASE_URL}/notify/allRead`,
  DATAAPI_MASSAGE_DELETE: `${DATA_API_BASE_URL}/notify/delete`,

  //* * 用户角色 *//
  DATAAPI_QUERY_USER: `${DATA_API_BASE_URL}/user/pageQuery`, // 查询系统用户信息
  DATAAPI_REMOVE_USER: `${DATA_API_BASE_URL}/roleUser/removeRoleUserFromProject`, // 删除用户
  DATAAPI_UPDATE_USER_ROLE: `${DATA_API_BASE_URL}/roleUser/updateUserRole`, // 更改用户角色
  DATAAPI_ADD_USER: `${DATA_API_BASE_URL}/roleUser/addRoleUserNew`, // 添加用户
  DATAAPI_ROLE_QUERY: `${DATA_API_BASE_URL}/role/pageQuery`, // 角色列表
  DATAAPI_GET_ROLE_TREE: `${DATA_API_BASE_URL}/permission/tree`, // 获取权限树
  DATAAPI_ROLE_PERMISSION: `${DATA_API_BASE_URL}/permission/getPermissionIdsByRoleId`, // 获取角色的权限
  DATAAPI_ROLE_PERMISSION_ADD_OR_EDIT: `${DATA_API_BASE_URL}/role/addOrUpdateRole`, // 更新或添加角色权限
  DATAAPI_REMOVE_ROLE: `${DATA_API_BASE_URL}/role/deleteRole`, // 删除角色
  DATAAPI_SEARCH_UIC_USERS: `${DATA_API_BASE_URL}/user/getUicUsersNotInProject`, // 获取UIC用户列表
  DATAAPI_GET_PROJECT_LIST: `${DATA_API_BASE_URL}/project/getProjects`, // 获取当前用户有权限的项目列表

  //= ==== 数据标签 ====//
  //* * 消息 **//
  DL_MASSAGE_QUERY: `${TAG_ENGINE_URL}/notify/pageQuery`,
  DL_GET_MASSAGE_BY_ID: `${TAG_ENGINE_URL}/notify/getOne`,
  DL_MASSAGE_MARK_AS_READ: `${TAG_ENGINE_URL}/notify/tabRead`,
  DL_MASSAGE_MARK_AS_ALL_READ: `${TAG_ENGINE_URL}/notify/allRead`,
  DL_MASSAGE_DELETE: `${TAG_ENGINE_URL}/notify/delete`,

  //* * 用户角色 *//
  DL_QUERY_USER: `${TAG_ENGINE_URL}/project/getProjectUsers`, // 查询系统用户信息
  DL_REMOVE_USER: `${TAG_ENGINE_URL}/roleUser/removeRoleUserFromProject`, // 删除用户
  DL_UPDATE_USER_ROLE: `${TAG_ENGINE_URL}/roleUser/updateUserRole`, // 更改用户角色
  DL_ADD_USER: `${TAG_ENGINE_URL}/roleUser/addRoleUserNew`, // 添加用户

  DL_ROLE_QUERY: `${TAG_ENGINE_URL}/role/pageQuery`, // 角色列表
  DL_GET_ROLE_TREE: `${TAG_ENGINE_URL}/permission/tree`, // 获取权限树
  DL_ROLE_PERMISSION: `${TAG_ENGINE_URL}/permission/getPermissionIdsByRoleId`, // 获取角色的权限
  DL_ROLE_PERMISSION_ADD_OR_EDIT: `${TAG_ENGINE_URL}/role/addOrUpdateRole`, // 更新或添加角色权限
  DL_REMOVE_ROLE: `${TAG_ENGINE_URL}/role/deleteRole`, // 删除角色
  DL_SEARCH_UIC_USERS: `${TAG_ENGINE_URL}/project/getUicUsersNotInProject`,
  DL_GET_PROJECT_LIST: `${TAG_ENGINE_URL}/project/getProjects`,

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
  STREAM_SEARCH_UIC_USERS: `${STREAM_BASE_URL}/project/getUicUsersNotInProject`, // 获取UIC用户列表

  //= ==== 算法平台 ====//
  //* *消息**//
  SCIENCE_MASSAGE_QUERY: `${SCIENCE_BASE_URL}/service/notifyRecord/pageQuery`,
  SCIENCE_GET_MASSAGE_BY_ID: `${SCIENCE_BASE_URL}/service/notifyRecord/getOne`,
  SCIENCE_MASSAGE_MARK_AS_READ: `${SCIENCE_BASE_URL}/service/notifyRecord/tabRead`,
  SCIENCE_MASSAGE_MARK_AS_ALL_READ: `${SCIENCE_BASE_URL}/service/notifyRecord/allRead`,
  SCIENCE_MASSAGE_DELETE: `${SCIENCE_BASE_URL}/service/notifyRecord/delete`,

  SCIENCE_GET_ALL_PROJECTS: `${SCIENCE_BASE_URL}/service/project/getProjects`, // 获取所有的项目列表

  //* * 用户角色 *//
  SCIENCE_QUERY_USER: `${SCIENCE_BASE_URL}/service/project/getProjectUsers`, // 查询系统用户信息
  SCIENCE_REMOVE_USER: `${SCIENCE_BASE_URL}/service/roleUser/removeRoleUserFromProject`, // 删除用户
  SCIENCE_UPDATE_USER_ROLE: `${SCIENCE_BASE_URL}/service/roleUser/updateUserRole`, // 更改用户角色
  SCIENCE_ADD_USER: `${SCIENCE_BASE_URL}/service/roleUser/addRoleUserNew`, // 添加用户
  SCIENCE_ROLE_QUERY: `${SCIENCE_BASE_URL}/service/role/pageQuery`, // 角色列表
  SCIENCE_GET_ROLE_TREE: `${SCIENCE_BASE_URL}/service/permission/tree`, // 获取权限树
  SCIENCE_ROLE_PERMISSION: `${SCIENCE_BASE_URL}/service/permission/getPermissionIdsByRoleId`, // 获取角色的权限
  SCIENCE_ROLE_PERMISSION_ADD_OR_EDIT: `${SCIENCE_BASE_URL}/service/roleUser/updateUserRole`, // 更新或添加角色权限
  SCIENCE_REMOVE_ROLE: `${SCIENCE_BASE_URL}/role/deleteRole`, // 删除角色
  SCIENCE_SEARCH_UIC_USERS: `${SCIENCE_BASE_URL}/service/project/getUicUsersNotInProject`, // 获取UIC用户列表
};
