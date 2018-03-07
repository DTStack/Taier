import mc from 'mirror-creator';

export const dataCheckActions = mc([
    'CHANGE_LOADING',
    'GET_LIST',
    'GET_CHECK_DETAIL',
    'CHANGE_PARAMS'
], { prefix: 'dataCheck/' });
