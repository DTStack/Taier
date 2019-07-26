import mc from 'mirror-creator';
const appActions = mc([
    'UPDATE_APP', // 更新当前用户信息
    'GET_LICENSE_APP', // 获取具有license权限的app
    'SET_LICENSE_LOADED'
], { prefix: 'app/' })

export default appActions;
