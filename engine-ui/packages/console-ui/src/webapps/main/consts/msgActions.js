import mc from 'mirror-creator';

const msgActions = mc([
    'UPDATE_MSG' // 更新当前消息信息
], { prefix: 'message/' })

export default msgActions;
