import mc from 'mirror-creator';

const commonActionType = mc([
    'GET_USER_LIST', // 获取当前租户下的所有用户
    'GET_ALL_DICT',
    'SET_SYS_PARAMS',
    'CHANGE_SIDERBAR_KEY' // 更改siderbar
], { prefix: 'common/' })

export default commonActionType;
