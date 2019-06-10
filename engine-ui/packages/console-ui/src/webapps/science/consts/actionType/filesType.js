import mc from 'mirror-creator';

const baseType = [
    'REPLACE_TREE_NODE', // 直接替换节点
    'UPDATE_TREE_NODE', // 更新节点信息，和第一层子节点信息
    'INIT_TREE', // 初始化树
    'CLEAR_TREE',
    'UPDATE_EXPANDEDKEYS' // 展开的keys
]
export const experimentFilesType = mc(baseType, { prefix: 'experiment/' });
export const componentFilesType = mc(baseType, { prefix: 'component/' });
export const notebookFilesType = mc(baseType, { prefix: 'notebook/' });
