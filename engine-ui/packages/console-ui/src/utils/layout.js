/**
 * 树形布局计算
 */

 const startX = 200, startY = 200;

 export function getTreeNodeLayout(startX, startY, node, parent) {
    // var W = containerWidth;
    // var x = Math.round(W/(node.count + 1)) * node.i - node.width/2;
    // var y = parent.y + (node.level * node.height + margin);

    const getX = function(relativeX, currentNode) {
        const rowWidth = currentNode.count * node.width + (currentNode.count-1) * currentNode.margin;
        const boundX = relativeX - Math.round(rowWidth/2);
        return boundX + currentNode.index * currentNode.width + (currentNode.index-1) * currentNode.margin;
    }

    const getY = function(relativeY, currentNode) {
        const l = Math.abs(currentNode.level);
        const y = l * currentNode.height + l * currentNode.margin;
        return currentNode.level >= 0 ? relativeY + y : relativeY - y;
    }

    let x = 0, y = 0;

    // 如果有父节点的话
    if (parent) {
        // x = node.index * node.width + (node.index-1) * node.margin ;
        // x = Math.round(parent.width/(node.count + 1)) * node.index - node.width/2;
        // y = node.level * node.height + node.level * node.margin + parent.margin;
        x = getX(parent.x, node);
        y = getY(parent.y, node);
    
    } else {
        x = getX(startX, node);
        y = getY(startY, node);
    }
    node.x = x;
    node.y = y;
    return node;
 }