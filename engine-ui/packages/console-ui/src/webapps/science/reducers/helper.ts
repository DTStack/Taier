import localDb from 'utils/localDb';
import { cloneDeep } from 'lodash';

/**
 * 替换节点，但是保留children信息
 * 这样，既可以根据服务端排序，也能保留深层次的节点信息
 * @param {*} origin 当前树
 * @param {*} replaceNode 新节点
 */
export function updateTreeNode (origin: any, replaceNode: any) {
    for (let i = 0; i < origin.length; i++) {
        const node = origin[i];
        if (node.id == replaceNode.id) {
            /**
             * 继承子节点信息
             */
            if (replaceNode.children && replaceNode.children.length) {
                let tmpMap: any = {};
                (node.children || []).forEach((childNode: any) => {
                    tmpMap[childNode.id] = childNode;
                });
                replaceNode = cloneDeep(replaceNode);
                replaceNode.children = replaceNode.children.map((childNode: any) => {
                    if (tmpMap[childNode.id]) {
                        return {
                            ...childNode,
                            children: tmpMap[childNode.id].children
                        }
                    }
                    return tmpMap[childNode.id] || childNode;
                });
            }
            origin = [...origin];
            origin.splice(i, 1, replaceNode)
            return origin;
        } else if (node.children) {
            const newChildren = updateTreeNode(node.children, replaceNode);
            if (newChildren) {
                origin = [...origin];
                origin.splice(i, 1, {
                    ...node,
                    children: newChildren
                })
                return origin;
            }
        }
    }
    return null;
}
/**
 * 覆盖树节点
 * @param {*} origin 当前树
 * @param {*} replaceNode 新节点
 */
export function replaceTreeNode (origin: any, replaceNode: any) {
    for (let i = 0; i < origin.length; i++) {
        const node = origin[i];
        if (node.id == replaceNode.id) {
            origin.splice(i, 1, replaceNode)
            return [...origin];
        } else if (node.children) {
            const tree = replaceTreeNode(node.children, replaceNode);
            if (tree) {
                return [...origin];
            }
        }
    }
    return null;
}

export function saveReducer (key: any, reducer: any) {
    return function (state = localDb.get(key), action: any) {
        const newState = reducer(state, action);
        localDb.set(key, newState);
        return newState;
    }
}
