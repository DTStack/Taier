import mc from 'mirror-creator';


export const commAction = mc([
    'GET_TASK_TYPES',
], {prefix: 'offline/comm/'});

export const modalAction = mc([
    'TOGGLE_CREATE_TASK',
    'TOGGLE_EDIT_TASK',
    'TOGGLE_UPLOAD',
    'TOGGLE_CREATE_FOLDER',
    'TOGGLE_CREATE_FN',
    'TOGGLE_MOVE_FN',
    'SET_MODAL_DEFAULT',
    'EMPTY_MODAL_DEFAULT',
    'SHOW_FNVIEW_MODAL',
    'HIDE_FNVIEW_MODAL',
    'SHOW_RESVIEW_MODAL',
    'HIDE_RESVIEW_MODAL',
    'TOGGLE_CREATE_SCRIPT',
], {prefix: 'offline/modal/'});

export const taskTreeAction = mc([
    'RESET_TASK_TREE',
    'LOAD_FOLDER_CONTENT',
    'ADD_FOLDER_CHILD',
    'DEL_OFFLINE_TASK',
    'DEL_OFFLINE_FOLDER',
    'EDIT_FOLDER_CHILD',
    'EDIT_FOLDER_CHILD_FIELDS',
    'MERGE_FOLDER_CONTENT',
], {prefix: 'offline/taskTree/'});

export const resTreeAction = mc([
    'RESET_RES_TREE',
    'LOAD_FOLDER_CONTENT',
    'ADD_FOLDER_CHILD',
    'DEL_OFFLINE_RES',
    'DEL_OFFLINE_FOLDER',
    'EDIT_FOLDER_CHILD'
], {prefix: 'offline/resTree/'});

export const fnTreeAction = mc([
    'RESET_FUC_TREE',
    'LOAD_FOLDER_CONTENT',
    'ADD_FOLDER_CHILD',
    'DEL_OFFLINE_FOLDER',
    'DEL_OFFLINE_FN',
    'EDIT_FOLDER_CHILD'
], {prefix: 'offline/fnTree/'});

export const sysFnTreeActon = mc([
    'RESET_SYSFUC_TREE',
    'LOAD_FOLDER_CONTENT',
    'ADD_FOLDER_CHILD',
    'DEL_OFFLINE_FOLDER'
], {prefix: 'offline/sysFnTree/'});

export const scriptTreeAction = mc([
    'RESET_SCRIPT_TREE',
    'LOAD_FOLDER_CONTENT',
    'ADD_FOLDER_CHILD',
    'DEL_SCRIPT',
    'DEL_OFFLINE_FOLDER',
    'EDIT_FOLDER_CHILD',
    'EDIT_FOLDER_CHILD_FIELDS',
    'MERGE_FOLDER_CONTENT',
], { prefix: 'offline/scriptTree/' });

export const tableTreeAction = mc([
    'RESET_TABLE_TREE',
    'LOAD_FOLDER_CONTENT',
    'ADD_FOLDER_CHILD',
    'DEL_TABLE',
    'DEL_OFFLINE_FOLDER',
    'EDIT_FOLDER_CHILD'
], { prefix: 'offline/tableTree/' });

export const workbenchAction = mc([
    'LOAD_TASK_DETAIL',
    'OPEN_TASK_TAB',
    'CLOSE_TASK_TAB',
    'UPDATE_TASK_TAB',
    'CLOSE_ALL_TABS',
    'CLOSE_OTHER_TABS',
    'CHANGE_SCHEDULE_CONF',
    'CHANGE_SCHEDULE_STATUS',
    'CHANGE_TASK_SUBMITSTATUS',
    'ADD_VOS',
    'DEL_VOS',
    'SET_TASK_FIELDS_VALUE',
    'SET_TASK_FIELDS_VALUE_SILENT',
    'SET_TASK_SQL_FIELD_VALUE',
    'SET_CURRENT_TAB_NEW',
    'SET_CURRENT_TAB_SAVED',
    'MAKE_TAB_DIRTY',
    'MAKE_TAB_CLEAN',
    'LOAD_TASK_CUSTOM_PARAMS',
    'SAVE_DATASYNC_TO_TAB'
], {prefix: 'offline/workbench/'});

export const dataSourceListAction = mc([
    'LOAD_DATASOURCE',
    'RESET_DATASOURCE'
], {prefix: 'offline/dataSync/dataSourceList/'});

export const sourceMapAction = mc([
    'DATA_SOURCE_CHANGE',
    'DATA_SOURCEMAP_CHANGE',
    'SOURCE_TABLE_COLUMN_CHANGE',
    'ADD_SOURCE_KEYROW',
    'REMOVE_SOURCE_KEYROW',
    'ADD_BATCH_SOURCE_KEYROW',
    'DATA_SOURCE_ADD',
    'DATA_SOURCE_DELETE',
], {prefix: 'offline/dataSync/sourceMap/'});

export const targetMapAction = mc([
    'DATA_SOURCE_TARGET_CHANGE',
    'DATA_TARGETMAP_CHANGE',
    'TARGET_TABLE_COLUMN_CHANGE',
    'ADD_TARGET_KEYROW',
    'ADD_BATCH_TARGET_KEYROW',
    'EDIT_TARGET_KEYROW',
    'REMOVE_TARGET_KEYROW',
], {prefix: 'offline/dataSync/targetMap/'});

export const keyMapAction = mc([
    'ADD_LINKED_KEYS',
    'DEL_LINKED_KEYS',
    'SET_ROW_MAP',
    'SET_NAME_MAP',
    'RESET_LINKED_KEYS',
    'EDIT_KEYMAP_TARGET',
    'REMOVE_KEYMAP',
], {prefix: 'offline/dataSync/keyMap/'});

export const settingAction = mc([
    'CHANGE_CHANNEL_SETTING',
    'CHANGE_CHANNEL_FIELDS',
], {prefix: 'offline/dataSync/setting'});

export const dataSyncAction = mc([
    'INIT_JOBDATA',
    'RESET_KEYMAP',
    'SET_CURRENT_STEP',
    'INIT_CURRENT_STEP',
    'RESET_SOURCE_MAP',
    'RESET_TARGET_MAP',
    'SET_TABID',
    'RESET_TABID',
    'GET_DATASYNC_SAVED'
], {prefix: 'offline/dataSync/'});
