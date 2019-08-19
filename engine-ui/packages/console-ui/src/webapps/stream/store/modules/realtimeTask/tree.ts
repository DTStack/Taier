import { cloneDeep, assign } from 'lodash'

import Api from '../../../api'
import { replaceTreeNode, removeTreeNode, mergeTreeNodes } from 'funcs'

import { treeAction } from './actionTypes'

/* eslint-disable */
const defaultReqParams: any = { isGetFile: true, nodePid: 0 }

// ACTIONS
export function removeAndUpdate(removeTarget: any) {
    return (dispatch: any) => {
        dispatch(getRealtimeTree({ 
            id: removeTarget.parentId,
            catalogueType: removeTarget.catalogueType
        }))
        return dispatch({
            type: treeAction.REMOVE_REALTIME_TREE_NODE,
            data: removeTarget
        })
    }
}

export function getRealtimeTree(node: any) {
    const newParams: any = {
        nodePid: node.id,
        catalogueType: node.catalogueType
    } || defaultReqParams;
    newParams.isGetFile = true;
    return (dispatch: any) => {
        return Api.getCatalogues(newParams).then((res: any) => {
            const arrData = res.data;
            dispatch({
                type: treeAction.GET_REALTIME_TREE,
                data: arrData
            });
            return arrData;
        })
    }
}

export function removeRealtimeTree(removeTarget: any) {
    return {
        type: treeAction.REMOVE_REALTIME_TREE_NODE,
        data: removeTarget
    }
}

export function updateRealtimeTree(tree: any) {
    return {
        type: treeAction.UPDATE_REALTIME_TREE,
        data: tree,
    }
}
export function mergeRealtimeTree(tree: any) {
    return {
        type: treeAction.MERGE_REALTIME_TREE,
        data: tree,
    }
}

export function updateRealtimeTreeNode(node: any) {
    return {
        type: treeAction.UPDATE_REALTIME_TREE_NODE,
        data: node,
    }
}

export function realtimeTree(state: any = {}, action: any) {
    switch (action.type) {
    case treeAction.GET_REALTIME_TREE: {
        if (Object.keys(state).length === 0) {
            return action.data
        }
        const updated = cloneDeep(state)
        if (action.data) {
            replaceTreeNode(updated, action.data)
        }
        return updated
    }
    case treeAction.UPDATE_REALTIME_TREE_NODE: {
        const updated = cloneDeep(state)
        replaceTreeNode(updated, action.data)
        return updated
    }
    case treeAction.MERGE_REALTIME_TREE: {
        const updated = cloneDeep(state)
        mergeTreeNodes(updated, action.data)
        return updated
    }
    case treeAction.UPDATE_REALTIME_TREE: {
        const tree = assign({}, action.data)
        return tree
    }
    case treeAction.REMOVE_REALTIME_TREE_NODE: {
        const removed: any = [cloneDeep(state)]
        if (action.data) {
            removeTreeNode(removed, action.data)
        }
        return removed[0]
    }
    case treeAction.RESET_TREE:{
        return {};
    }
    default:
        return state
    }
}

/* eslint-disable */

