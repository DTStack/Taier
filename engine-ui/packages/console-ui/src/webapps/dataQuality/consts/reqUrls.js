 // API 地址
 // '/api/dq/service';
 //const DQ_BASE_URL = 'http://api.dtstack.com/server/index.php?g=Web&c=Mock&o=simple&projectID=3&uri=/api/dq/service'; // 从config文件全局读取
const DQ_BASE_URL = '/api/dq/service'; // 从config文件全局读取

export default {

    // ===== 系统管理 ===== //
    DQ_ROLE_QUERY: `${DQ_BASE_URL}/role/pageQuery`,
    DQ_ROLE_UPDATE: `${DQ_BASE_URL}/role/addOrUpdateRole`,
    DQ_ROLE_DELETE: `${DQ_BASE_URL}/role/deleteRole`,
    DQ_ROLE_PERMISSION_TREE: `${DQ_BASE_URL}/permission/tree`,
    DQ_ROLE_PERMISSION: `${DQ_BASE_URL}/permission/getPermissionIdsByRoleId`,

    DQ_GET_USER_BY_ID: `${DQ_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DQ_USER_ROLE_ADD: `${DQ_BASE_URL}/roleUser/addRoleUser`,
    DQ_USER_ROLE_DELETE: `${DQ_BASE_URL}/roleUser/deleteRole`,
    DQ_USER_ROLE_UPDATE: `${DQ_BASE_URL}/roleUser/updateUserRole`,
    DQ_GET_USER_LIST: `${DQ_BASE_URL}/user/pageQuery`,
    DQ_GET_USER_NOT_IN_PROJECT: `${DQ_BASE_URL}/user/listNotIn`,

    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${DQ_BASE_URL}/notify/pageQuery`,
    GET_MASSAGE_BY_ID: `${DQ_BASE_URL}/notify/getOne`,
    MASSAGE_MARK_AS_READ: `${DQ_BASE_URL}/notify/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${DQ_BASE_URL}/notify/allRead`,
    MASSAGE_DELETE: `${DQ_BASE_URL}/notify/delete`,

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/pageQuery`, // 通过查询数据源
    CHECK_CONNECTION: `${DQ_BASE_URL}/dataSource/checkConnection`, // 检查数据库连接
    ADD_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/addSource`, // 新增数据源
    UPDATE_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/updateSource`, // 新增数据源
    DELETE_DATA_SOURCES: `${DQ_BASE_URL}/dataSource/deleteSource`, // 删除数据源

    GET_DATA_SOURCES_LIST: `${DQ_BASE_URL}/dataSource/list`, // 查询已添加的数据源类型
    GET_DATA_SOURCES_TYPE: `${DQ_BASE_URL}/dataSource/getTypes`, // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DQ_BASE_URL}/dataSource/tablelist`, // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DQ_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PART: `${DQ_BASE_URL}/dataSource/getPartValue`, // 查询数据源下的分区
    GET_DATA_SOURCES_PREVIEW: `${DQ_BASE_URL}/dataSource/preview`, // 预览数据源下的数据
    

    // ===== 逐行校验 ===== //
    GET_CHECK_LIST: `${DQ_BASE_URL}/verify/pageQuery`,       // 逐行校验列表数据
    GET_CHECK_DETAIL: `${DQ_BASE_URL}/verify/getOne`,       // 单个数据详情
    ADD_CHECK: `${DQ_BASE_URL}/verify/add`,             // 编辑逐行校验
    UPDATE_CHECK: `${DQ_BASE_URL}/verify/update`,             // 编辑逐行校验
    DELETE_CHECK: `${DQ_BASE_URL}/verify/deleteVerify`,  // 删除逐行校验
    GET_CHECK_REPORT: `${DQ_BASE_URL}/verify/getReport`, // 获取报告

    // ===== 规则配置 ===== //
    GET_RULE_LIST: `${DQ_BASE_URL}/monitor/pageQuery`,         // 规则配置列表
    ADD_RULE: `${DQ_BASE_URL}/monitor/add`,                    // 新增规则配置
    UPDATE_RULE: `${DQ_BASE_URL}/monitor/update`,              // 编辑规则配置
    GET_RULE_PART: `${DQ_BASE_URL}/monitor/getPart`,           // 获取规则分区
    CHANGE_RULE_STATUS: `${DQ_BASE_URL}/monitor/closeOrOpen`,         // 开启关闭监控
    // GET_RULE_DETAIL: `${DQ_BASE_URL}/monitor/getOne`,          // 单个数据详情
    RULE_REMOTE_TRIGGER: `${DQ_BASE_URL}/monitor/remoteTrigger`,   // 远程触发
    EXECUTE_RULE: `${DQ_BASE_URL}/monitor/immediatelyExecuted`,    // 立即执行

    GET_MONITOR_RULE: `${DQ_BASE_URL}/monitorRule/getRules`,     // 获取监控规则
    SAVE_MONITOR_RULE: `${DQ_BASE_URL}/monitorRule/save`,       // 保存监控规则
    DELETE_MONITOR_RULE: `${DQ_BASE_URL}/monitorRule/delete`,           // 删除监控规则

    GET_MONITOR_FUNCTION: `${DQ_BASE_URL}/function/getFunctions`,  // 统计函数

    SUBSCRIBE_TABLE: `${DQ_BASE_URL}/subscribe/subscribe`, // 订阅表
    UNSUBSCRIBE_TABLE: `${DQ_BASE_URL}/subscribe/unsubscribe`, // 取消订阅表

    // ===== common ===== //
    DQ_GET_USER_LIST: `${DQ_BASE_URL}/user/list`,
    DQ_GET_ALL_DICT: `${DQ_BASE_URL}/dict/allDicts`,
    
}
