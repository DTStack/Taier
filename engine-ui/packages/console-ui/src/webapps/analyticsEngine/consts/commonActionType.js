import mc from 'mirror-creator';

const commonActionType = mc([
    'GET_USER_LIST', // 获取当前租户下的所有用户
    'GET_ALL_DICT',
], { prefix: 'common/' })

export default commonActionType;