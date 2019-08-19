import assign from 'object-assign';

import {
    modalAction
} from './actionType';
import { ENGINE_SOURCE_TYPE } from '../../../comm/const';
const initModalState: any = {
    createTask: false,
    editTask: false,
    cloneTask: false,
    cloneToWorkflow: false,
    upload: false,
    createFolder: false,
    createScript: false,
    createFn: false,
    cateType: '',
    defaultData: undefined,
    isCoverUpload: undefined,
    resViewModal: false,
    fnViewModal: false,
    resId: undefined,
    fnId: undefined,
    moveFnData: undefined,
    taskType: '',
    workFlowLists: [],
    confirmSaveVisible: false,
    showPublish: false,
    theReqIsEnd: true,
    editModalKey: null,
    engineType: ENGINE_SOURCE_TYPE.HADOOP // 引擎类型 默认Hadoop
};

export const modalShowReducer = (state = initModalState, action: any) => {
    switch (action.type) {
        case modalAction.SET_ENGINE_TYPE:
            return assign({}, state, {
                engineType: action.payload
            });
        case modalAction.TOGGLE_CREATE_TASK:
            return assign({}, state, {
                createTask: !state.createTask
            });

        case modalAction.TOGGLE_CLONE_TASK:
            return assign({}, state, {
                cloneTask: !state.cloneTask
            });
        case modalAction.TOGGLE_CLONE_TO_WORKFLOW:
            return assign({}, state, {
                cloneToWorkflow: !state.cloneToWorkflow
            });
        case modalAction.GET_WORKFLOW_LIST:
            return assign({}, state, {
                workFlowLists: action.payload
            });
        case modalAction.TOGGLE_CREATE_SCRIPT:
            return assign({}, state, {
                createScript: !state.createScript
            });

        case modalAction.TOGGLE_EDIT_TASK:
            return assign({}, state, {
                editTask: !state.editTask
            });

        case modalAction.TOGGLE_UPLOAD:
            return assign({}, state, {
                upload: !state.upload,
                isCoverUpload: action.payload && action.payload.isCoverUpload
            });

        case modalAction.TOGGLE_CREATE_FOLDER:
            return assign({}, state, {
                createFolder: !state.createFolder
            }, action.payload ? { cateType: action.payload } : {});

        case modalAction.TOGGLE_CREATE_FN:
            return assign({}, state, {
                createFn: !state.createFn
            });

        case modalAction.TOGGLE_MOVE_FN:
            const isVisible = typeof action.payload !== 'undefined';

            return assign({}, state, {
                moveFnData: isVisible ? {
                    isVisible: true,
                    originFn: action.payload
                } : undefined
            });

        case modalAction.SET_MODAL_DEFAULT:
            return assign({}, state, {
                defaultData: action.payload
            });

        case modalAction.EMPTY_MODAL_DEFAULT:
            return assign({}, state, {
                defaultData: undefined
            });

        case modalAction.SHOW_FNVIEW_MODAL:
            return assign({}, state, {
                fnViewModal: true,
                fnId: action.payload
            });

        case modalAction.HIDE_FNVIEW_MODAL:
            return assign({}, state, {
                fnViewModal: false,
                fnId: undefined
            });

        case modalAction.SHOW_RESVIEW_MODAL: {
            return assign({}, state, {
                resViewModal: true,
                resId: action.payload
            });
        }

        case modalAction.HIDE_RESVIEW_MODAL: {
            return assign({}, state, {
                resViewModal: false,
                resId: undefined
            });
        }
        case modalAction.SET_MODAL_KEY: {
            return assign({}, state, {
                editModalKey: action.payload
            });
        }
        // 保存提交
        case modalAction.TOGGLE_SAVE_MODAL:
            return assign({}, state, {
                confirmSaveVisible: action.payload
            });
        case modalAction.TOGGLE_PUBLISH_MODAL:
            return assign({}, state, {
                showPublish: action.payload
            });
        case modalAction.IS_SAVE_FINISH:
            return assign({}, state, {
                theReqIsEnd: action.payload
            });
        default:
            return state;
    }
};
