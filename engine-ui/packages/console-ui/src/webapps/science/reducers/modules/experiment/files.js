
import { experimentFilesType } from '../../../consts/actionType/filesType';
import { updateTreeNode, replaceTreeNode } from '../../helper';
function files (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case experimentFilesType.UPDATE_TREE_NODE: {
            return updateTreeNode(state, payload) || state;
        }
        case experimentFilesType.REPLACE_TREE_NODE: {
            return replaceTreeNode(state, payload) || state;
        }
    }
}

export default files;
