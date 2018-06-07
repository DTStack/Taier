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
], { prefix: 'realtimeTask/browser/' })

export const modalAction = mc([
    'ADD_TASK_VISIBLE',
    'ADD_RES_VISIBLE',
    
    'EDIT_TASK_VISIBLE',
    'ADD_TASK_CATA_VISIBLE',
    'EDIT_TASK_CATA_VISIBLE',
    'ADD_RES_CATA_VISIBLE',
    'EDIT_RES_CATA_VISIBLE',
    'ADD_FUNC_CATA_VISIBLE',
    'EDIT_FUNC_CATA_VISIBLE',
    'UPDATE_MODAL_ACTION',
    'MODAL_HIDDEN',
], { prefix: 'realtimeTask/modal/' })

export const resAction = mc([
    'GET_RESOURCE',
    'REMOVE_RESOURCE',
    'ADD_RESOURCE',
], { prefix: 'realtimeTask/res/' })

export const treeAction = mc([
    'GET_REALTIME_TREE',
    'UPDATE_REALTIME_TREE_NODE',
    'UPDATE_REALTIME_TREE',
    'REMOVE_REALTIME_TREE_NODE',
], { prefix: 'realtimeTask/tree/' })
