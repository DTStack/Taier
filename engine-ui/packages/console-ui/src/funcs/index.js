import { debounce } from 'lodash';

/**
 * 存放一些零碎的公共方法
 */

// 请求防抖动
export function debounceEventHander(...args) {
    const debounced = debounce(...args)
    return function(e) {
        e.persist()
        return debounced(e)
    }
}

/**
 * 查找树中的某个节点
 */
export function findTreeNode(treeNode, node) {
    let result = ""
    if (treeNode.id === parseInt(node.id, 10)) {
        return treeNode;
    }
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            result = findTreeNode(children[i], node)
            if (result) return result
        }
    }
}

/**
 * 
 * @param {Array} treeNodes 树状节点数据
 * @param {Object} target 目标节点
 */
export function removeTreeNode(treeNodes, target) {
    for (let i = 0; i < treeNodes.length; i += 1) {
        if (treeNodes[i].id === target.id) {
            treeNodes.splice(i, 1) // remove节点
            break;
        }
        if (treeNodes[i].children) {
            removeTreeNode(treeNodes[i].children, target)
        }
    }
}

/**
 * 追加子节点
 */
export function appendTreeNode(treeNode, append, target) {
    const targetId = parseInt(target.id, 10)
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            appendTreeNode(children[i], append, target)
        }
    }
    if (treeNode.id === targetId && treeNode.children) {
        treeNode.children.push(append)
    }
}

/**
 * 遍历树形节点，用新节点替换老节点
*/
export function replaceTreeNode(treeNode, replace) {
    if (
        treeNode.id === parseInt(replace.id, 10) 
        // && 
        // treeNode.level === replace.level
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

/**
 * 打开新窗口
 * @param {*} url 
 * @param {*} target 
 */
export function openNewWindow(url, target) {
    window.open(url, target || '_blank')
}

/**
 * 检验改应用是否包含项目选项
 * @param {s} app 
 */
export function hasProject(app) {
    return app === 'rdos'
}