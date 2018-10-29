import mc from 'mirror-creator';

const commonActionType = mc([
    'GET_USER_LIST', // 获取当前租户下的所有用户
    'GET_ALL_DICT',
    'GET_TABLE_LIST', // 获取所有有权限的表
], { prefix: 'common/' })

export default commonActionType;