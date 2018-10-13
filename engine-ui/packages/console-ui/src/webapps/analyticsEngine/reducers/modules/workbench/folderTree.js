import { cloneDeep } from 'lodash';
import workbenchAction from '../../../consts/workbenchActionType';
import { replaceTreeNode, removeTreeNode, mergeTreeNodes } from 'funcs'

export default function folderTree(state = {}, action) {
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