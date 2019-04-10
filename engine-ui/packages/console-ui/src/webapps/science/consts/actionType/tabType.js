import mc from 'mirror-creator';

const baseType = [
    'ADD_TAB', // 添加tab
    'DELETE_TAB', // 删除tab
    'DELETE_OTHER_TAB', // 删除其他tab
    'CHANGE_TAB', // 更改tab内容
    'DELETE_ALL_TAB', // 删除所有tab
    'SET_CURRENT_TAB' // 设置当前的tab
]

export const experimentTabType = mc(baseType, { prefix: 'experiment/' });
export const notebookTabType = mc(baseType, { prefix: 'notebook/' });
