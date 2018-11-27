import mc from 'mirror-creator';

export const taskQueryActionType = mc([
    'CHANGE_LOADING',
    'GET_TASK_LIST',
    'GET_TASK_DETAIL',
    'GET_TASK_TABLE_REPORT',
    'GET_TASK_ALARM_NUM'
], { prefix: 'taskQuery/' })
