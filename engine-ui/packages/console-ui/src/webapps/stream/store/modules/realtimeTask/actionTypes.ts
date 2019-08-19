import mc from 'mirror-creator';

export const browserAction = mc([
    'GET_PAGES',
    'NEW_PAGE',
    'CLOSE_PAGE',
    'UPDATE_PAGE',
    'CLEAR_PAGES',
    'CLOSE_OTHERS',
    'SET_CURRENT_PAGE',
    'GET_CURRENT_PAGE',
    'UPDATE_CURRENT_PAGE',
    'SET_INPUT_DATA',
    'GET_INPUT_DATA',
    'CLEAR_CURRENT_INPUT_DATA',
    'CLEAR_OTHER_INPUT_DATA',
    'CLEAR_ALL_INPUT_DATA',
    'SET_OUTPUT_DATA',
    'GET_OUTPUT_DATA',
    'CLEAR_CURRENT_OUTPUT_DATA',
    'CLEAR_OTHER_OUTPUT_DATA',
    'CLEAR_ALL_OUTPUT_DATA',
    'SET_DIMESION_DATA',
    'GET_DIMESION_DATA',
    'CLEAR_CURRENT_DIMESION_DATA',
    'CLEAR_OTHER_DIMESION_DATA',
    'CLEAR_ALL_DIMESION_DATA'
], { prefix: 'realtimeTask/browser/' })

export const modalAction = mc([
    'ADD_TASK_VISIBLE',
    'ADD_RES_VISIBLE',
    'ADD_TASK_CLONE_VISIBLE',
    'EDIT_TASK_VISIBLE',
    'ADD_TASK_CATA_VISIBLE',
    'EDIT_TASK_CATA_VISIBLE',
    'ADD_RES_CATA_VISIBLE',
    'EDIT_RES_CATA_VISIBLE',
    'ADD_FUNC_CATA_VISIBLE',
    'EDIT_FUNC_CATA_VISIBLE',
    'UPDATE_MODAL_ACTION',
    'MODAL_HIDDEN'
], { prefix: 'realtimeTask/modal/' })

export const resAction = mc([
    'GET_RESOURCE',
    'REMOVE_RESOURCE',
    'ADD_RESOURCE'
], { prefix: 'realtimeTask/res/' })

export const treeAction = mc([
    'GET_REALTIME_TREE',
    'UPDATE_REALTIME_TREE_NODE',
    'UPDATE_REALTIME_TREE',
    'REMOVE_REALTIME_TREE_NODE',
    'MERGE_REALTIME_TREE',
    'RESET_TREE'
], { prefix: 'realtimeTask/tree/' })

export const commAction = mc([
    'GET_TASK_TYPES',
    'GET_TASK_TYPE_FILTER',
    'CLOSE_RIGHT_PANEL'
], { prefix: 'realtimeTask/comm/' });

export const dataSourceListAction = mc([
    'LOAD_DATASOURCE',
    'RESET_DATASOURCE'
], { prefix: 'realtimeTask/collection/dataSourceList/' });

export const collectionAction = mc([
    'INIT_JOBDATA',
    'RESET_KEYMAP',
    'SET_CURRENT_STEP',
    'INIT_CURRENT_STEP',
    'RESET_SOURCE_MAP',
    'RESET_TARGET_MAP',
    'SET_TABID',
    'RESET_TABID',
    'GET_DATASYNC_SAVED'
], { prefix: 'realtimeTask/collection/' });
