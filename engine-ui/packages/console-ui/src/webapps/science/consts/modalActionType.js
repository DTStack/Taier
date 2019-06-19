import mc from 'mirror-creator';

const modalAction = mc([
    'UPDATE_MODAL', //
    'RESET_MODAL' // 重置
], { prefix: 'modal/' })

export default modalAction;
