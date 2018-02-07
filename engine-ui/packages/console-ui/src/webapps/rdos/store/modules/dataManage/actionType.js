import mc from 'mirror-creator';

export const tableAction = mc([
    'LOAD_TABLE_LIST',
    'LOAD_TABLE_DETAIL',
    'MODIFY_DESC',
    'ADD_ROW',
    'DEL_ROW',
    'REPLACE_ROW',
    'MOVE_ROW',
    'SAVE_TABLE'
], { prefix: 'datamanage/table/' });

export const logAction = mc([
    'GET_USERS_SUC'
], {prefix: 'datamanage/log/'});