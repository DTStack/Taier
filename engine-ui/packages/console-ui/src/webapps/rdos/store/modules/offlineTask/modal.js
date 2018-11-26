import assign from 'object-assign';

import {
    modalAction,
} from './actionType';

const initModalState = {
    createTask: false,
    editTask: false,
    cloneTask: false,
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
};

export const modalShowReducer = (state = initModalState, action) => {
    switch(action.type) {
        case modalAction.TOGGLE_CREATE_TASK:
            return assign({}, state, {
                createTask: !state.createTask,
            });
        
        case modalAction.TOGGLE_CLONE_TASK:
            return assign({}, state, {
                cloneTask: !state.cloneTask,
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
            }, action.payload ? {cateType: action.payload} : {});

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

        default:
            return state;
    }
};