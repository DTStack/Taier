import assign from 'object-assign';
import { cloneDeep } from 'lodash';

import {
    taskTreeAction,
    resTreeAction,
    sparkFnTreeAction,
    libraFnTreeAction,
    libraSysFnTreeActon,
    fnTreeAction,
    sysFnTreeActon,
    scriptTreeAction,
    tableTreeAction
} from './actionType';

import { replaceTreeNode } from 'funcs'

/**
 * @description 加载文件夹内容
 *
 * @param {any} action
 * @param {any} state
 * @returns immutable state
 */
function loadFolderContent (action, state) {
    const data = action.payload;
    const id = data.id;
    const level = data.level;
    let clone = cloneDeep(state);
    let loop = (arr) => {
        arr.forEach((node, i) => {
            if (node.id === id &&
                node.level === level &&
                (node.type === 'folder' ||
                node.type === 'flow')
            ) {
                node.children = data.children;
                node._hasLoaded = true;
            } else {
                loop(node.children || []);
            }
        });
    };

    loop([clone]);
    return clone;
}

function sortByName (arr) {
    arr.sort(function (a, b) {
        return a.name.localeCompare(b.name);
    })
}

/**
 * @description 新增文件夹内容
 *
 * @param {any} action
 * @param {any} state
 * @returns immutable state
 */
function addFolderChild (action, state) {
    const data = action.payload;
    const { parentId } = data;

    let clone = cloneDeep(state);
    let loop = (arr) => {
        arr.forEach((node, i) => {
            if (node.id === parentId) {
                if (node.children === null || node.children === undefined) node.children = [];
                let fileIndex = 0;

                for (let i = 0; i <= node.children.length - 1; i++) {
                    if (node.children[i].type === 'file') {
                        fileIndex = i;
                        break;
                    }
                }
                node.children.splice(fileIndex, 0, data);
                // Sort children by name
                sortByName(node.children);
            } else {
                loop(node.children || []);
            }
        });
    };

    loop([clone]);
    return clone;
}

function deleteFolderChild (action, state) {
    const { id, parentId } = action.payload;
    let clone = cloneDeep(state);
    let loop = (arr) => {
        arr.forEach((node, i) => {
            if (node.id === parentId) {
                if (node.children === null) node.children = [];
                node.children = node.children.filter(o => {
                    return o.id !== id;
                });
            } else {
                loop(node.children || []);
            }
        });
    };

    loop([clone]);
    return clone;
}

export const clearTreeData = (dispatch) => {
    return dispatch => {
        dispatch({
            type: scriptTreeAction.RESET_SCRIPT_TREE,
            payload: {}
        });
    }
}

export const taskTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case taskTreeAction.RESET_TASK_TREE:
            return assign({}, action.payload);

        case taskTreeAction.LOAD_FOLDER_CONTENT: {
            return loadFolderContent(action, state);
        }

        case taskTreeAction.ADD_FOLDER_CHILD: {
            return addFolderChild(action, state);
        }

        case taskTreeAction.DEL_OFFLINE_TASK: {
            return deleteFolderChild(action, state);
        }

        case taskTreeAction.DEL_OFFLINE_FOLDER: {
            return deleteFolderChild(action, state);
        }

        case taskTreeAction.EDIT_FOLDER_CHILD: {
            let payload = assign({}, action.payload, { parentId: action.payload.originPid });
            return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
        }
        case taskTreeAction.EDIT_FOLDER_CHILD_FIELDS: {
            const updated = cloneDeep(state)
            replaceTreeNode(updated, action.payload)
            return updated;
        }
        case taskTreeAction.MERGE_FOLDER_CONTENT: {
            const origin = action.payload;
            if (origin) return origin;
            break;
        }
        default:
            return state;
    }
};

export const resourceTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case resTreeAction.RESET_RES_TREE:
            return assign({}, action.payload);

        case resTreeAction.LOAD_FOLDER_CONTENT: {
            return loadFolderContent(action, state);
        }

        case resTreeAction.ADD_FOLDER_CHILD: {
            return addFolderChild(action, state);
        }

        case resTreeAction.DEL_OFFLINE_RES: {
            return deleteFolderChild(action, state);
        }

        case resTreeAction.DEL_OFFLINE_FOLDER: {
            return deleteFolderChild(action, state);
        }

        case resTreeAction.EDIT_FOLDER_CHILD: {
            let payload = assign({}, action.payload, { parentId: action.payload.originPid });
            return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
        }
        default:
            return state;
    }
};

export const sparkFnTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case sparkFnTreeAction.GET_SPARK_ROOT:
            return assign({}, state, action.payload)
        case sparkFnTreeAction.LOAD_FOLDER_CONTENT:
            return assign({}, state, action.payload)
        default:
            return state;
    }
}

export const libraFnTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case libraFnTreeAction.GET_LIBRA_ROOT:
            return assign({}, state, action.payload)
        case libraFnTreeAction.LOAD_FOLDER_CONTENT:
            return assign({}, state, action.payload)
        default:
            return state;
    }
}

export const libraSysFnTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case libraSysFnTreeActon.RESET_SYSFUC_TREE:
            return assign({}, state, action.payload);

        case libraSysFnTreeActon.LOAD_FOLDER_CONTENT: {
            return loadFolderContent(action, state);
        }
        default:
            return state;
    }
};

export const functionTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case fnTreeAction.RESET_FUC_TREE:
            return assign({}, state, action.payload);

        case fnTreeAction.LOAD_FOLDER_CONTENT: {
            return loadFolderContent(action, state);
        }

        case fnTreeAction.ADD_FOLDER_CHILD: {
            return addFolderChild(action, state);
        }

        case fnTreeAction.DEL_OFFLINE_FOLDER: {
            return deleteFolderChild(action, state);
        }

        case fnTreeAction.DEL_OFFLINE_FN: {
            return deleteFolderChild(action, state);
        }

        case fnTreeAction.EDIT_FOLDER_CHILD: {
            let payload = assign({}, action.payload, { parentId: action.payload.originPid });
            return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
        }

        default:
            return state;
    }
};

export const sysFunctionTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case sysFnTreeActon.RESET_SYSFUC_TREE:
            return assign({}, state, action.payload);

        case sysFnTreeActon.LOAD_FOLDER_CONTENT: {
            return loadFolderContent(action, state);
        }

        case sysFnTreeActon.ADD_FOLDER_CHILD: {
            return addFolderChild(action, state);
        }

        case sysFnTreeActon.DEL_OFFLINE_FOLDER: {
            return deleteFolderChild(action, state);
        }

        default:
            return state;
    }
};

export const scriptTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case scriptTreeAction.RESET_SCRIPT_TREE:
            return assign({}, action.payload);

        case scriptTreeAction.LOAD_FOLDER_CONTENT: {
            return loadFolderContent(action, state);
        }

        case scriptTreeAction.ADD_FOLDER_CHILD: {
            return addFolderChild(action, state);
        }

        case scriptTreeAction.DEL_SCRIPT: {
            return deleteFolderChild(action, state);
        }

        case scriptTreeAction.DEL_OFFLINE_FOLDER: {
            return deleteFolderChild(action, state);
        }

        case scriptTreeAction.EDIT_FOLDER_CHILD: {
            let payload = assign({}, action.payload, { parentId: action.payload.originPid });
            return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
        }

        case scriptTreeAction.EDIT_FOLDER_CHILD_FIELDS: {
            const updated = cloneDeep(state)
            replaceTreeNode(updated, action.payload)
            return updated;
        }

        case scriptTreeAction.MERGE_FOLDER_CONTENT: {
            const origin = action.payload;
            if (origin) return origin;
            break;
        }

        default:
            return state;
    }
};

export const tableTreeReducer = (state = {}, action) => {
    switch (action.type) {
        case tableTreeAction.RESET_TABLE_TREE:
            return assign({}, action.payload);

        case tableTreeAction.LOAD_FOLDER_CONTENT: {
            return loadFolderContent(action, state);
        }

        case tableTreeAction.ADD_FOLDER_CHILD: {
            return addFolderChild(action, state);
        }

        case tableTreeAction.DEL_TABLE: {
            return deleteFolderChild(action, state);
        }

        case tableTreeAction.DEL_OFFLINE_FOLDER: {
            return deleteFolderChild(action, state);
        }

        case tableTreeAction.EDIT_FOLDER_CHILD: {
            let payload = assign({}, action.payload, { parentId: action.payload.originPid });
            return addFolderChild(action, deleteFolderChild({ payload: payload }, state));
        }

        default:
            return state;
    }
};
