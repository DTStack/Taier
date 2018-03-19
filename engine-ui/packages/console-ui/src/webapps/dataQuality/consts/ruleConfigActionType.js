import mc from 'mirror-creator';

export const ruleConfigActionType = mc([
    'CHANGE_LOADING',
    'GET_MONITOR_LIST',
    'GET_RULE_FUNCTION',
    'CHANGE_PARAMS',
    'GET_MONITOR_RULE',
    'GET_MONITOR_DETAIL'
], { prefix: 'ruleConfig/' });
