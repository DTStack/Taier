import mc from 'mirror-creator';

export const tagConfigActionType = mc([
    'CHANGE_LOADING',
    'GET_RULE_TAG_LIST',
    'GET_REGISTERED_TAG_LIST',
    'GET_ALL_IDENTIFY_COLUMN'

], { prefix: 'tagConfig/' });
