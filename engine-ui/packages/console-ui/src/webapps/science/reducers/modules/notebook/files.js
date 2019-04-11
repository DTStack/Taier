
import { notebookFilesType } from '../../../consts/actionType/filesType';
import { updateTreeNode, replaceTreeNode } from '../../helper';
function files (state = [{
    id: 1,
    name: 'folder1',
    type: 'folder',
    children: [{
        id: 11,
        name: 'file1',
        type: 'file'
    }, {
        id: 12,
        name: 'file2',
        type: 'file'
    }]
}], action) {
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
