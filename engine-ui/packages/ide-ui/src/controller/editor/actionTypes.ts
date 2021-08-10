import mc from 'mirror-creator'

export const editorAction = mc([
    'RESET_CONSOLE',
    'APPEND_CONSOLE_LOG',
    'SET_CONSOLE_LOG',
    'UPDATE_RESULTS',
    'DELETE_RESULT',
    'GET_TAB',
    'SET_TAB',
    'SET_SELECTION_CONTENT',
    'ADD_LOADING_TAB', // 添加指定的的tab loading
    'REMOVE_LOADING_TAB', // 移除指定的tab loading
    'REMOVE_ALL_LOAING_TAB', // 移除所有tab loading
    'UPDATE_OPTIONS', // 更新编辑器选项
    'SHOW_RIGHT_PANE', // 显示右侧面板
    'SHOW_TABLE_TIP_PANE', // 打开表提示面板
    'SHOW_SYNTAX_HELP_PANE', // 打开语法提示面板
    'UPDATE_SYNTAX_PANE' // 更新帮助面板状态
], { prefix: 'editor/' })
