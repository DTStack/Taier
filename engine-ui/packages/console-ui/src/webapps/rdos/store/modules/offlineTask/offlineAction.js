import { message } from 'antd'
import { isEmpty } from 'lodash'
import { hashHistory } from 'react-router'

import ajax from '../../../api'
import { MENU_TYPE } from '../../../comm/const'

import {
    modalAction,
    sourceMapAction,
    targetMapAction,
    keyMapAction,
    dataSyncAction,
    workbenchAction,
    taskTreeAction,
    resTreeAction,
    fnTreeAction,
    editorAction,
    sysFnTreeActon,
    scriptTreeAction,
} from './actionType';


// keyMap模块
export const keyMapActions = (dispatch) => {

    return {
        addLinkedKeys: (params) => {
            dispatch({
                type: keyMapAction.ADD_LINKED_KEYS,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        delLinkedKeys: (params) => {
            dispatch({
                type: keyMapAction.DEL_LINKED_KEYS,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        handleTargetMapChange(srcmap) {
            dispatch({
                type: targetMapAction.DATA_TARGETMAP_CHANGE,
                payload: srcmap
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        setRowMap: (params) => {
            dispatch({
                type: keyMapAction.SET_ROW_MAP,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        setNameMap: (params) => {
            dispatch({
                type: keyMapAction.SET_NAME_MAP,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        resetLinkedKeys: () => {
            dispatch({
                type: keyMapAction.RESET_LINKED_KEYS
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        addSourceKeyRow(params) {
            dispatch({
                type: sourceMapAction.ADD_SOURCE_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        addBatchSourceKeyRow(params) {
            dispatch({
                type: sourceMapAction.ADD_BATCH_SOURCE_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        editSourceKeyRow(params) {
            // TODO, 如果需要编辑源列
        },
        removeSourceKeyRow(source, index) {
            dispatch({
                type: sourceMapAction.REMOVE_SOURCE_KEYROW,
                payload: index
            });
            dispatch({
                type: keyMapAction.REMOVE_KEYMAP,
                payload: { source },
            });
        },
        addTargetKeyRow(params) {
            dispatch({
                type: targetMapAction.ADD_TARGET_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        editKeyMapTarget(params) {
            dispatch({
                type: keyMapAction.EDIT_KEYMAP_TARGET,
                payload: params
            });
        },
        editTargetKeyRow(params) {
            dispatch({
                type: targetMapAction.EDIT_TARGET_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },
        addBatchTargetKeyRow(params) {
            dispatch({
                type: targetMapAction.ADD_BATCH_TARGET_KEYROW,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        },

        removeTargetKeyRow(target, index) {
            dispatch({
                type: targetMapAction.REMOVE_TARGET_KEYROW,
                payload: index
            });
            dispatch({
                type: keyMapAction.REMOVE_KEYMAP,
                payload: { target },
            });
        },
    }
};

// workbenchActions
export const workbenchActions = (dispatch) => {

    return {

        updateTabData: (data) => {
            dispatch({
                type: workbenchAction.UPDATE_TASK_TAB,
                payload: data
            });
        },

        openTaskInDev: (id) => {
            ajax.getOfflineTaskDetail({
                id: id
            }).then(res => {
                if(res.code === 1) {
                    dispatch({
                        type: workbenchAction.LOAD_TASK_DETAIL,
                        payload: res.data
                    });
                    dispatch({
                        type: workbenchAction.OPEN_TASK_TAB,
                        payload: id
                    });
                    hashHistory.push('/offline/task');
                }
            });
        },

        openTab: function(data) {
            const { id, tabs, currentTab, treeType, lockInfo } = data

            if (tabs.map(o => o.id).indexOf(id) === -1) {
                const succCallBack = (res) => {
                    if (res.code === 1) {
                        dispatch({
                            type: workbenchAction.LOAD_TASK_DETAIL,
                            payload: res.data
                        });
                    }
                }
                if (treeType === MENU_TYPE.TASK_DEV) { // 任务类型
                    ajax.getOfflineTaskDetail({
                        id: id,
                    }).then(succCallBack);
                } else if (treeType === MENU_TYPE.SCRIPT) { // 脚本类型
                    ajax.getScriptById({
                        id: id,
                    }).then(succCallBack);
                }
            }
            else {
                id !== currentTab && dispatch({
                    type: workbenchAction.OPEN_TASK_TAB,
                    payload: id
                });
            }
            dispatch({
                type: editorAction.SET_SELECTION_CONTENT,
                data: '',
            })
        },

        loadTreeNode: (nodePid, type) => {
            ajax.getOfflineCatelogue({
                isGetFile: !!1,
                nodePid,
                catalogueType: type,
            }).then(res => {
                if (res.code === 1) {
                    let { data } = res;
                    let action;

                    switch (type) {
                        case MENU_TYPE.TASK:
                        case MENU_TYPE.TASK_DEV:
                            action = taskTreeAction;
                            break;
                        case MENU_TYPE.RESOURCE:
                            action = resTreeAction;
                            break;
                        case MENU_TYPE.FUNCTION:
                        case MENU_TYPE.COSTOMFUC:
                            action = fnTreeAction;
                            break;
                        case MENU_TYPE.SYSFUC:
                            action = sysFnTreeActon;
                            break;
                        case MENU_TYPE.SCRIPT:
                            action = scriptTreeAction;
                            break;
                        default:
                            action = taskTreeAction;
                    }

                    data.children && dispatch({
                        type: action.LOAD_FOLDER_CONTENT,
                        payload: data
                    });
                }
            });
        },

        delOfflineTask(params, nodePid, type) {
            ajax.delOfflineTask(params)
                .then(res => {
                    if (res.code == 1) {
                        message.info('删除成功');
                        dispatch({
                            type: taskTreeAction.DEL_OFFLINE_TASK,
                            payload: {
                                id: res.data,
                                parentId: nodePid
                            }
                        });
                        dispatch({
                            type: workbenchAction.CLOSE_TASK_TAB,
                            payload: res.data
                        });
                    }
                });
        },

        delOfflineScript(params, nodePid, type) {
            ajax.deleteScript(params)
            .then(res => {
                if (res.code == 1) {
                    message.info('删除成功');
                    dispatch({
                        type: scriptTreeAction.DEL_SCRIPT,
                        payload: {
                            id: params.scriptId,
                            parentId: nodePid
                        }
                    });
                    dispatch({
                        type: workbenchAction.CLOSE_TASK_TAB,
                        payload: params.scriptId
                    });
                }
            });
        },

        delOfflineFolder(params, nodePid, cateType) {
            ajax.delOfflineFolder(params)
            .then(res => {
                if (res.code === 1) {
                    let action;

                    switch (cateType) {
                        case MENU_TYPE.TASK:
                        case MENU_TYPE.TASK_DEV:
                            action = taskTreeAction;
                            break;
                        case MENU_TYPE.RESOURCE:
                            action = resTreeAction;
                            break;
                        case MENU_TYPE.FUNCTION:
                        case MENU_TYPE.COSTOMFUC:
                            action = fnTreeAction;
                            break;
                        case MENU_TYPE.SCRIPT:
                            action = scriptTreeAction;
                            break;
                        default:
                            action = taskTreeAction;
                    }

                    dispatch({
                        type: action.DEL_OFFLINE_FOLDER,
                        payload: {
                            id: params.id,
                            parentId: nodePid
                        }
                    })
                }
            })
        },

        loadTaskParams() {
            ajax.getCustomParams()
            .then(res => {
                if (res.code === 1) {
                    dispatch({
                        type: workbenchAction.LOAD_TASK_CUSTOM_PARAMS,
                        payload: res.data
                    })
                }
            })
        },

        setModalDefault(data) {
            dispatch({
                type: modalAction.SET_MODAL_DEFAULT,
                payload: data
            })
        },

        toggleCreateFolder: function (type) {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FOLDER,
                payload: type
            });
        },

        toggleCreateTask: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_TASK
            });
        },

        toggleCreateScript: function() {
            dispatch({
                type: modalAction.TOGGLE_CREATE_SCRIPT
            });
        },

        toggleCreateFn: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FN
            });
        },

        toggleMoveFn: function (params) {
            dispatch({
                type: modalAction.TOGGLE_MOVE_FN,
                payload: params
            });
        },

        toggleUpload: function () {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD
            });
        },

        toggleCoverUpload: function () {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD,
                payload: {
                    isCoverUpload: true,
                }
            });
        },

        delOfflineRes(params, nodePid) {
            ajax.delOfflineRes(params)
                .then(res => {
                    if (res.code === 1) {
                        dispatch({
                            type: resTreeAction.DEL_OFFLINE_RES,
                            payload: {
                                id: params.resourceId,
                                parentId: nodePid
                            }
                        })
                    }
                })
        },

        delOfflineFn(params, nodePid) {
            ajax.delOfflineFn(params)
                .then(res => {
                    if (res.code === 1) {
                        dispatch({
                            type: fnTreeAction.DEL_OFFLINE_FN,
                            payload: {
                                id: params.functionId,
                                parentId: nodePid
                            }
                        })
                    }
                })
        },

        showFnViewModal(id) {
            dispatch({
                type: modalAction.SHOW_FNVIEW_MODAL,
                payload: id
            })
        },
        
        showResViewModal(id) {
            dispatch({
                type: modalAction.SHOW_RESVIEW_MODAL,
                payload: id
            });
        },
    }
}
