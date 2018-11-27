import { cloneDeep } from 'lodash';
import workbenchAction from '../../../consts/workbenchActionType';
import { removeTreeNode, mergeTreeNodes } from 'funcs'

export const folderTreeRoot = {
    id: 0,
    name: 'treeRoot',
    level: 0,
    type: 'folder',
    children: []
};

const replaceTreeNode = function (treeNode, replace) {
    if (
        treeNode.id === parseInt(replace.id, 10) &&
        treeNode.type === replace.type
    ) {
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

export default function folderTree (state = folderTreeRoot, action) {
    const { type, payload } = action;
    switch (type) {
    case workbenchAction.LOAD_CATALOGUE_DATA: {
        if (Object.keys(state).length === 0) {
            return payload
        }
        const updated = cloneDeep(state)
        if (payload) {
            replaceTreeNode(updated, payload)
        }
        return updated
    }
    case workbenchAction.REMOVE_CATALOGUE_TREE_NODE: {
        const removed = [cloneDeep(state)]
        if (payload) {
            removeTreeNode(removed, action.data)
        }
        return removed[0]
    }
    case workbenchAction.UPDATE_CATALOGUE_TREE_NODE: {
        const updated = cloneDeep(state)
        replaceTreeNode(updated, payload)
        return updated
    }
    case workbenchAction.MERGE_CATALOGUE_TREE: {
        const updated = cloneDeep(state)
        mergeTreeNodes(updated, payload)
        return updated
    }
    default:
        return state;
    }
}
