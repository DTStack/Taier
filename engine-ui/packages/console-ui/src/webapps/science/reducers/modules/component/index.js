import { combineReducers } from 'redux';

import { componentFilesType } from '../../../consts/actionType/filesType';
import { updateTreeNode, replaceTreeNode } from '../../helper';
function files (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case componentFilesType.UPDATE_TREE_NODE: {
            return updateTreeNode(state, payload) || state;
        }
        case componentFilesType.REPLACE_TREE_NODE: {
            return replaceTreeNode(state, payload) || state;
        }
        case componentFilesType.INIT_TREE: {
            return payload;
        }
        default: {
            return state;
        }
    }
}

export default combineReducers({
    files
});
