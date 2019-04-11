import { experimentFilesType, notebookFilesType, componentFilesType } from '../../consts/actionType/filesType'
import api from '../../api';
import { siderBarType } from '../../consts';

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
export function loadTreeData (type, nodeId) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res;
            switch (type) {
                case siderBarType.notebook: {
                    res = await api.notebook.loadTreeData({ nodeId });
                    break;
                }
                case siderBarType.experiment: {
                    res = await api.experiment.loadTreeData({ nodeId });
                    break;
                }
                case siderBarType.component: {
                    res = await api.component.loadTreeData({ nodeId });
                    break;
                }
            }
            if (res && res.code == 1) {
                dispatch(initTreeNode(type, res.data));
            }
            setTimeout(resolve, 2000);
            // resolve();
        })
    }
}
