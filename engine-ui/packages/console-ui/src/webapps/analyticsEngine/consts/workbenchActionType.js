import mc from 'mirror-creator';

const workbenchActionType = mc([

    // Tab
    'SWITCH_TAB', // SwitchTab
    'CLOSE_TAB', // 关闭Tab
    'OPEN_TAB', // 打开Tab

    // Catalogue
    'LOAD_CATALOGUE_DATA', // 加载目录数据
    'REMOVE_CATALOGUE_TREE_NODE', // 移除节点
    'UPDATE_CATALOGUE_TREE_NODE', // 更新节点
    'MERGE_CATALOGUE_TREE', // 合并节点

    // MainBench
    'OPEN_SQL_QUERY', // 打开SQL查询
    'CREATE_TABLE', // 创建表
    'CREATE_TABLE', // 创建表
    'OPEN_TABLE', // 查看表
    'OPEN_DATABASE', // 查看数据库
    'OPEN_CREATE_DATABASE', // 创建数据库
    'CREATE_DATABASE', // 创建数据库
    'CREATE_DATA_MAP', // 创建DataMap
    'NEW_TABLE_INFO_CHANGE',//新建表表单值更改
    'NEXT_STEP',//下一步
    'LAST_STEP',//上一步
    'OPEN_DATA_MAP', // 查看DataMap
    'NEW_TABLE_SAVED',//保存新表
], { prefix: 'workbench/' })

export default workbenchActionType;