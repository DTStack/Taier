import mc from 'mirror-creator';

export const dashBoardActionType = mc([
    'CHANGE_LOADING',
    'GET_TOP_RECORD',
    'GET_ALARM_SUM',
    'GET_ALARM_TREND',
    'GET_USAGE'
], { prefix: 'dashBoard/' })
