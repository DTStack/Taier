import mc from 'mirror-creator';

const baseType: any = [
    'ADD_TAB', // 添加tab
    'DELETE_TAB', // 删除tab
    'DELETE_OTHER_TAB', // 删除其他tab
    'CHANGE_TAB', // 更改tab内容
    'CHANGE_TAB_SLIENT', // 静默更改tab内容
    'DELETE_ALL_TAB', // 删除所有tab
    'SET_CURRENT_TAB' // 设置当前的tab
]

export const experimentTabType = mc(
    [
        ...baseType,
        'CHANGE_TASK_STATUS'
    ],
    { prefix: 'experiment/' }
);
export const notebookTabType = mc(baseType, { prefix: 'notebook/' });
