import { experimentFilesType, notebookFilesType, componentFilesType, resourceFilesType } from '../../consts/actionType/filesType'
import { message } from 'antd';
import api from '../../api';
import { siderBarType } from '../../consts';

const typeMap: any = {
    [siderBarType.experiment]: experimentFilesType,
    [siderBarType.notebook]: notebookFilesType,
    [siderBarType.component]: componentFilesType,
    [siderBarType.resource]: resourceFilesType
}
export function initTreeNode (type: any, tree: any) {
    let actionType = typeMap[type] || {};
    return {
        type: actionType.INIT_TREE,
        payload: tree
    }
}
export function replaceTreeNode (type: any, node: any) {
    let actionType = typeMap[type];
    return {
        type: actionType.REPLACE_TREE_NODE,
        payload: node
    }
}
export function updateTreeNode (type: any, node: any) {
    let actionType = typeMap[type];
    return {
        type: actionType.UPDATE_TREE_NODE,
        payload: node
    }
}
export function initLoadTreeNode () {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.fileTree.loadTreeData({ isGetFile: true, nodePid: 0 });
            if (res && res.code == 1 && res.data) {
                for (let i = 0; i < res.data.children.length; i++) {
                    const tree = res.data.children[i];
                    const treeType = tree.catalogueType;
                    if (typeMap[treeType]) {
                        dispatch(initTreeNode(treeType, tree.children));
                        if (treeType == siderBarType.notebook || treeType == siderBarType.experiment) {
                            if (tree.children && tree.children.length) {
                                dispatch(updateExpandedKeys(treeType, tree.children[0].key))
                                dispatch(loadTreeData(treeType, tree.children[0].id));
                            }
                        }
                    }
                }
            }
        })
    }
}
export function loadTreeData (type: any, nodePid: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.fileTree.loadTreeData({ isGetFile: true, nodePid, catalogueType: type });
            if (res && res.code == 1) {
                dispatch(updateTreeNode(type, res.data));
            }
            resolve();
        })
    }
}
export function addFolder (type: any, nodeName: any, nodePid: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.fileTree.addFolder({
                nodeName,
                nodePid
            });
            if (res && res.code == 1) {
                message.success('添加成功');
                dispatch(loadTreeData(type, nodePid))
                resolve(res)
            }
        })
    }
}
export function updateFolder (id: any, type: any, nodeName: any, nodePid: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.fileTree.updateFolder({
                nodeName,
                nodePid,
                id
            });
            if (res && res.code == 1) {
                message.success('修改成功');
                dispatch(loadTreeData(type, nodePid))
                resolve(res)
            }
        })
    }
}

export function updateExpandedKeys (type: any, keys: any) {
    let actionType = typeMap[type];
    return {
        type: actionType.UPDATE_EXPANDEDKEYS,
        payload: keys
    }
}

export function removeExpandedkey (type: any, key: any) {
    let actionType = typeMap[type];
    return {
        type: actionType.REMOVE_EXPANDEDKEYS,
        payload: key
    }
}
