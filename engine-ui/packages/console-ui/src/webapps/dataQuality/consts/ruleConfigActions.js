import mc from 'mirror-creator';

export const ruleConfigActions = mc([
    'CHANGE_LOADING',
    'GET_RULE_LIST',
    'GET_CHECK_DETAIL',
    'CHANGE_PARAMS'
], { prefix: 'ruleConfig/' });
