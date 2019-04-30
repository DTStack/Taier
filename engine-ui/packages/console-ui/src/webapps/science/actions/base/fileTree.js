import { experimentFilesType, notebookFilesType, componentFilesType } from '../../consts/actionType/filesType'
import { message } from 'antd';
import api from '../../api';
import { siderBarType } from '../../consts';

const typeMap = {
    [siderBarType.experiment]: experimentFilesType,
    [siderBarType.notebook]: notebookFilesType,
    [siderBarType.component]: componentFilesType
}
export function initTreeNode (type, tree) {
    let actionType = typeMap[type] || {};
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
export function initLoadTreeNode () {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.fileTree.loadTreeData({ isGetFile: true, nodePid: 0 });
            if (res && res.code == 1 && res.data) {
                for (let i = 0; i < res.data.children.length; i++) {
                    const tree = res.data.children[i];
                    const treeType = tree.catalogueType;
                    dispatch(initTreeNode(treeType, tree.children));
                }
            }
        })
    }
}
export function loadTreeData (type, nodePid) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.fileTree.loadTreeData({ isGetFile: true, nodePid, catalogueType: type });
            if (res && res.code == 1) {
                dispatch(updateTreeNode(type, res.data));
            }
            resolve();
            // resolve();
        })
    }
}
export function addFolder (type, nodeName, nodePid) {
    return dispatch => {
        return new Promise(async (resolve) => {
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
export function updateFolder (id, type, nodeName, nodePid) {
    return dispatch => {
        return new Promise(async (resolve) => {
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
