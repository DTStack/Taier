import mc from 'mirror-creator';

export const keyMapActionType = mc([
    'ADD_LINKED_KEYS',
    'DEL_LINKED_KEYS',
    'SET_EDIT_MAP',
    'SET_ROW_MAP',
    'SET_NAME_MAP',
    'RESET_LINKED_KEYS',
    'EDIT_KEYMAP_TARGET',
    'REMOVE_KEYMAP'
], { prefix: 'dataSource/keyMap/' });
