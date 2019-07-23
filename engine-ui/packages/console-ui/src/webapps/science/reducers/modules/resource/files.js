
import { resourceFilesType } from '../../../consts/actionType/filesType';
import { updateTreeNode, replaceTreeNode } from '../../helper';
function files (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case resourceFilesType.UPDATE_TREE_NODE: {
            return updateTreeNode(state, payload) || state;
        }
        case resourceFilesType.REPLACE_TREE_NODE: {
            return replaceTreeNode(state, payload) || state;
        }
        case resourceFilesType.INIT_TREE: {
            return payload;
        }
        case resourceFilesType.CLEAR_TREE: {
            return [];
        }
        default: {
            return state;
        }
    }
}

export default files;
