import mc from 'mirror-creator';
const appActions = mc([
    'UPDATE_APP' // 更新当前用户信息
], { prefix: 'app/' })

export default appActions;
