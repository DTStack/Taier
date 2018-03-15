import mc from 'mirror-creator';

export const ruleConfigActionType = mc([
    'CHANGE_LOADING',
    'GET_RULE_LIST',
    'GET_RULE_FUNCTION',
    'CHANGE_PARAMS'
], { prefix: 'ruleConfig/' });
