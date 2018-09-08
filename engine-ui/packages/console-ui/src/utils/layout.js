/**
 * 树形布局计算
 */

 export function getGeoByParent(parent, node) {

    const getX = function(currentNode) {
        if (currentNode.index === 1 && currentNode.count === 1) {
            return parent.x;
        } else {
            const rowWidth = currentNode.count * currentNode.width + (currentNode.count-1) * currentNode.margin;
            const boundX = (parent.x) - Math.round(rowWidth/2);
            return currentNode.index === 1 && currentNode.count > 1 ? 
            boundX : boundX + currentNode.index * currentNode.width + (currentNode.index-1) * currentNode.margin;
        }
    }

    const getY = function(currentNode) {
        const space = parent.height + currentNode.margin; 
        return currentNode.level >= 0 ? parent.y + space : parent.y - space;
        // if (currentNode.level === 0 && currentNode.count === 1) {
        //     return parent.y;
        // } else {
        //     const l = Math.abs(currentNode.level);
        // }
    }

    node.x = getX(node);
    node.y = getY(node);

    console.log('getGeoByParent:', parent, node);

    return node;
 }

 export const getRowWidth = (node) => {
    const rowWidth = node.count * node.width + (node.count-1) * node.margin;
    return rowWidth;
}

export const getRowHeight = (node) => {
    const rowHeight = node.level * node.height + (node.level-1) * node.margin;
    return rowHeight;
}

/**
 * 统计节点信息
 */
export const getNodeCount = (node) => {
    let count = 1;
    let level = 0, maxLevel = 0;
    const loop = (node, level) => {

        level = level + 1;
        const children = node.subTaskVOS;
        
        if (children && children.length > 0) {
            for (let j = 0; j < children.length; j++) {
                loop(children[j], level);
            }
        } else {
            count++;
            maxLevel = level > maxLevel ? level : maxLevel;
        }
    }

    loop(node, level);

    const res = {
        count,
        level: maxLevel
    }

    console.log('getNodeCount:', res)
    return res;
}