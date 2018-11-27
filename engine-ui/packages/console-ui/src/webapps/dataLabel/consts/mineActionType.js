import mc from 'mirror-creator';

export const mineActionType = mc([
    'GET_APPLYING_LIST', // 获取正在审批的api
    'GET_APPLYED_LIST'// 获取已审批的api
], { prefix: 'mine/' })
