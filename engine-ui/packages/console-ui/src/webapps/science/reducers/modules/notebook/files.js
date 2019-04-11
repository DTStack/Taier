
import { notebookFilesType } from '../../../consts/actionType/filesType';
import { updateTreeNode, replaceTreeNode } from '../../helper';
function files (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case notebookFilesType.UPDATE_TREE_NODE: {
            return updateTreeNode(state, payload) || state;
        }
        case notebookFilesType.REPLACE_TREE_NODE: {
            return replaceTreeNode(state, payload) || state;
        }
        case notebookFilesType.INIT_TREE: {
            return payload;
        }
        default: {
            return state;
        }
    }
}

export default files;
