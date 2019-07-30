/**
 * 树形布局计算
 */

/**
 * 根据父节点，计算当前节点的坐标
 * @param {*} relativeNode
 * @param {*} node
 */
export function getGeoByRelativeNode (relativeNode: any, node: any) {
    const getX = function (currentNode: any) {
        if (currentNode.index === 1 && currentNode.count === 1) {
            return relativeNode.x;
        } else if (currentNode.count > 1) {
            let rowWidth = currentNode.count * currentNode.width + (currentNode.count - 1) * currentNode.margin;
            const boundX = (relativeNode.x + Math.round(relativeNode.width / 2)) - Math.round(rowWidth / 2);
            if (currentNode.index === 1) { return boundX };
            return boundX + (currentNode.index - 1) * currentNode.width + (currentNode.index - 1) * currentNode.margin;
        }
    }

    const getY = function (currentNode: any) {
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

export function getGeoByStartPoint (origin: any, node: any) {
    const { x: startX, y: startY } = origin;

    node.x = startX;
    node.y = startY;

    // 计算 坐标 X
    const isEven = node.count % 2 === 0;
    const halfNode = Math.round(node.width / 2) + Math.round(node.margin / 2);
    const middle = Math.round(node.count / 2);
    const index = Math.round(node.index - middle);
    let i = Math.abs(index);

    let distance = (i * node.width + i * node.margin);
    if (index > 0) {
        if (isEven) {
            distance = distance - halfNode;
        }
        node.x = startX + distance;
    } else if (index <= 0) {
        if (isEven) {
            distance = distance + halfNode;
        }
        node.x = startX - distance;
    }

    // 计算坐标 Y
    const l = Math.abs(node.level);
    const distanceY = (l * node.height + l * node.margin);
    if (node.level > 0) {
        node.y = startY + distanceY;
    } else if (node.level < 0) {
        node.y = startY - distanceY;
    }
    return node;
}

export const getNodeHeight = (node: any) => {
    const l = Math.abs(node.level);
    const rowHeight = (l + 1) * node.height + l * node.margin;
    return rowHeight;
}

export const getNodeWidth = (node: any) => {
    const rowWidth = node.count * node.width + (node.count - 1) * node.margin;
    return rowWidth;
}

export const getRowWidth = (currentNode: any) => {
    return currentNode.count * currentNode.width + (currentNode.count - 1) * currentNode.margin;
}

export const getParentNodeRelativeGeoX = (node: any) => {
    let geoX = 10;
    if (node.index === 1 && node.count === 1) {
        geoX = node.x;
    } else if (node.rowWidth) {
        geoX = Math.round(node.rowWidth / 2);
    } else {
        geoX = Math.round(getRowWidth(node) / 2);
    }
    return geoX;
}

/**
 * 统计节点信息
 */
export const getNodeLevelAndCount = (node: any, childrenField: any) => {
    let count = 1;
    let maxLevel = 1;

    const getReturn = function () {
        return {
            maxCount: count,
            maxLevel: maxLevel
        }
    };

    if (!node) {
        return getReturn()
    }

    const getMaxLevel = (node: any) => {
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

    return getReturn();
}

/**
 * 统计节点信息
 */
export const getRowCountOfSameLevel = (data: any, currentNode: any) => {
    let count = 0;
    let index = 0;

    const getReturn = function () {
        return {
            count,
            index
        }
    };

    if (!currentNode) {
        return getReturn();
    }

    const { id, level } = currentNode;
    for (let i = 0; i < data.length; i++) {
        const source = data[i].source;
        if (source && source.level === level) {
            count++;
            if (source.id === id && index === 0) {
                index = i;
            }
        }
    }

    return getReturn()
}

export const getNodeIndexAndCount = (node: any, currentNode: any, childrenField: any) => {
    let count = 0;
    let level = 0;
    let index = 0;

    const loop = (node: any, l: number) => {
        const children = node[childrenField || 'subTaskVOS'];
        if (children) {
            for (let j = 0; j < children.length; j++) {
                const o = children[j];
                const lev = l + 1;
                const sameLevel = lev === Math.abs(currentNode.level);
                if (sameLevel) {
                    count++;
                    if (currentNode.id === o.id) {
                        index = count;
                    }
                } else {
                    loop(o, lev);
                }
            }
        }
    }

    loop(node, level);

    return {
        count,
        index
    };
}

// 先计算 level。 maxLevel , maxCount
// 计算 index, count
// 根据 maxLevel， maxCount 计算宽高，根据 level, index , relativeNode计算 x, y;
