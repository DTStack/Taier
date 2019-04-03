/**
 * 替换节点，但是保留children信息
 * 这样，既可以根据服务端排序，也能保留深层次的节点信息
 * @param {*} origin 当前树
 * @param {*} replaceNode 新节点
 */
export function updateTreeNode (origin, replaceNode) {
    for (let i = 0; i < origin.length; i++) {
        const node = origin[i];
        if (node.id == replaceNode.id) {
            /**
             * 继承子节点信息
             */
            if (replaceNode.children && replaceNode.children.length) {
                let tmpMap = {};
                (node.children || []).forEach((childNode) => {
                    tmpMap[childNode.id] = childNode;
                });
                replaceNode = replaceNode.children.map((childNode) => {
                    return tmpMap[childNode.id] || childNode;
                });
            }
            return [...origin].splice(i, 1, replaceNode)
        } else if (node.children) {
            const tree = updateTreeNode(node.children, replaceNode);
            if (tree) {
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
export function replaceTreeNode (origin, replaceNode) {

}
