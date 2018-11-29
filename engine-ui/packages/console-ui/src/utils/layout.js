/**
 * 树形布局计算
 */

/**
 * 根据父节点，计算当前节点的坐标
 * @param {*} relativeNode
 * @param {*} node
 */
export function getGeoByRelativeNode (relativeNode, node) {
    const getX = function (currentNode) {
        if (currentNode.index === 1 && currentNode.count === 1) {
            return relativeNode.x;
        } else if (currentNode.count > 1) {
            const rowWidth = currentNode.count * currentNode.width + (currentNode.count - 1) * currentNode.margin;
            const boundX = (relativeNode.x + Math.round(relativeNode.width / 2)) - Math.round(rowWidth / 2);
            if (currentNode.index === 1) { return boundX };
            return boundX + (currentNode.index - 1) * currentNode.width + (currentNode.index - 1) * currentNode.margin;
        }
    }

    const getY = function (currentNode) {
        if (
            (currentNode.level === 0 && currentNode.count === 1) ||
            (currentNode.level === relativeNode.level)
        ) {
            return relativeNode.y;
        } else {
            if (currentNode.level > relativeNode.level) {
                const space = relativeNode.height + currentNode.margin;
                return relativeNode.y + space;
            } else {
                const space = currentNode.height + currentNode.margin;
                return relativeNode.y - space;
            }
        }
    }

    node.x = getX(node);
    node.y = getY(node);
    return node;
}

export const getNodeWidth = (node) => {
    const rowWidth = (node.count - 1) * node.width + (node.count - 1) * node.margin;
    return rowWidth;
}

export const getNodeHeight = (node) => {
    const l = Math.abs(node.level);
    const rowHeight = (l + 1) * node.height + l * node.margin;
    return rowHeight;
}

/**
 * 统计节点信息
 */
export const getNodeLevelAndCount = (node, childrenField) => {
    let count = 1;
    let maxLevel = 0;
    const getMaxLevel = (node) => {
        let max = 0;

        const children = node[childrenField || 'subTaskVOS'];
        if (children && children.length > 0) {
            for (let j = 0; j < children.length; j++) {
                const l = getMaxLevel(children[j]);
                max = l > max ? l : max;
            }
        } else {
            count++;
        }
        return max + 1;
    }

    maxLevel = getMaxLevel(node);

    const res = {
        count,
        level: maxLevel
    }

    return res;
}
