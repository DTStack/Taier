import mc from 'mirror-creator';

const commonActionType = mc([
    'GET_USER_LIST',
    'GET_ALL_MENU_LIST',
    'GET_PERIOD_TYPE',
    'GET_NOTIFY_TYPE'
], { prefix: 'common/' })

export default commonActionType;
