import mc from 'mirror-creator';

export const workbenchAction = mc(
    [
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
        'SET_CURRENT_TAB_NEW',
        'SET_CURRENT_TAB_SAVED',
        'MAKE_TAB_DIRTY',
        'MAKE_TAB_CLEAN',
        'LOAD_TASK_CUSTOM_PARAMS',
        'SAVE_DATASYNC_TO_TAB',
        'INIT_WORKBENCH',
    ],
    { prefix: 'offline/workbench/' }
);
