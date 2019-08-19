import mc from 'mirror-creator';

export const dashBoardActionType = mc([
    'CHANGE_LOADING',
    'GET_TOP_RECORD',
    'GET_ALARM_SUM',
    'GET_ALARM_TREND',
    'GET_USAGE',
    'CHOOSE_USER_DATE', // 切换用户界面日期类别
    'CHOOSE_ADMIN_DATE', // 切换管理员界面日期类别

    'GET_USER_CALL_INFO', // 设置用户调用信息概览
    'GET_API_FAIL_RANK', // 获取用户API调用失败率排行
    'GET_USER_API_CALL_RANK', // 获取用户个人API调用次数排行
    'GET_USER_API_SUB_INFO', // 获取用户个人API订购与审核情况

    'GET_MARKET_API_FAIL_RANK', // 获取市场API调用失败率排行
    'GET_MARKET_CALL_INFO', // 设置市场调用信息概览
    'GET_MARKET_API_CALL_RANK', // 获取市场API调用次数用户排行
    'GET_MARKET_API_ERROR_INFO', // 获取市场API错误分布
    'GET_MARKET_API_APPLY_INFO', // 获取API申请记录信息
    'GET_MARKET_TOP_CALL_FUNC', // 获取市场方法调用排行

    'CLOSE_APPROVAL_WARNING' // 关闭首页审批提醒

], { prefix: 'dashBoard/' })
