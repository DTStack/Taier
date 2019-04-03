import { experimentFilesType, notebookFilesType, componentFilesType } from '../../consts/actionType/filesType'

const typeMap = {
    experiment: experimentFilesType,
    notebook: notebookFilesType,
    component: componentFilesType
}
export function initTreeNode (type, tree) {
    let actionType = typeMap[type];
    return {
        type: actionType.INIT_TREE,
        payload: tree
    }
}
export function replaceTreeNode (type, node) {
    let actionType = typeMap[type];
    return {
        type: actionType.REPLACE_TREE_NODE,
        payload: node
    }
}
export function updateTreeNode (type, node) {
    let actionType = typeMap[type];
    return {
        type: actionType.UPDATE_TREE_NODE,
        payload: node
    }
}
