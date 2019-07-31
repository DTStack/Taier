import mc from 'mirror-creator';

export const dataCheckActionType = mc([
    'CHANGE_LOADING',
    'GET_LIST',
    'GET_CHECK_DETAIL',
    'CHANGE_PARAMS',
    'GET_SOURCE_PART',
    'RESET_SOURCE_PART'
], { prefix: 'dataCheck/' });
