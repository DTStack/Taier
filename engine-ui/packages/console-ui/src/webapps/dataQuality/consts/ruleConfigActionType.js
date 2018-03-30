import mc from 'mirror-creator';

export const ruleConfigActionType = mc([
    'CHANGE_LOADING',
    'GET_MONITOR_LIST',
    'GET_RULE_FUNCTION',
    'GET_MONITOR_TABLE_COLUMN',
    'CHANGE_PARAMS',
    'GET_MONITOR_RULE',
    'GET_MONITOR_DETAIL',
    'GET_REMOTE_TRIGGER'
], { prefix: 'ruleConfig/' });
