import mc from 'mirror-creator';

export const apiManageActionType = mc([
    'GET_ALL_API_LIST',
    'CHANGE_DISABLE_TIP',
    'CHNAGE_CODE_CLICK'
], { prefix: 'apiManage/' })
