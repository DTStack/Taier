import mc from 'mirror-creator';

const commonActionType = mc([
    'GET_USER_LIST', // 获取当前租户下的所有用户
    'GET_ALL_DICT',
    'GET_ALL_MENU_LIST',//获取所有菜单
], { prefix: 'common/' })

export default commonActionType;