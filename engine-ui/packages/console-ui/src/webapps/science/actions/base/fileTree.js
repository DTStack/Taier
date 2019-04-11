import { experimentFilesType, notebookFilesType, componentFilesType } from '../../consts/actionType/filesType'
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
            let res;
            switch (type) {
                case siderBarType.notebook: {
                    res = await api.fileTree.loadTreeData({ isGetFile: true, nodePid });
                    break;
                }
                case siderBarType.experiment: {
                    res = await api.fileTree.loadTreeData({ isGetFile: true, nodePid });
                    break;
                }
                case siderBarType.component: {
                    res = await api.fileTree.loadTreeData({ isGetFile: true, nodePid });
                    break;
                }
            }
            if (res && res.code == 1) {
                dispatch(replaceTreeNode(type, res.data));
            }
            setTimeout(resolve, 2000);
            // resolve();
        })
    }
}
