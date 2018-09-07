/**
 * 树形布局计算
 */
 export function getTreeLayout(layoutBaseInfo, node) {
    const { 
        containerWidth, 
        containerHeight, 
        nodeHeight,
        nodeWidth,
        levelHeight,
        nodeMargin,
    } = layoutBaseInfo;

    // var W = containerWidth;
    // var x = Math.round(W/(node.count + 1)) * node.i - node.width/2;
    // var y = parent.y + (node.level * node.height + margin);
    const totalWidth = node.count * node.nodeWidth;
    const startX = Math.round(containerWidth/2) - Math.round(totalWidth/2); // 1/2 of container Width;
    const startY = Math.round(containerHeight/2); // 1/2 of container Height;

    var x = startX + node._index * nodeWidth + nodeMargin;
    var y = node.level * nodeHeight + levelHeight;

    return {
        x,
        y
    }
 }