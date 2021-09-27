import mc from 'mirror-creator';

const userActions = mc([
    'GET_USER', // 获取当前用户信息
    'UPDATE_USER', // 更新当前用户信息
    'GET_NOT_PROJECT_USERS', // 非项目用户
    'GET_PROJECT_USERS', // 项目用户列表
    'GET_USER_LIST'
], { prefix: 'user/' })

export default userActions;
