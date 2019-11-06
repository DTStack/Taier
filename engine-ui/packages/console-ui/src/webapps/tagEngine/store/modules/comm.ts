import mc from 'mirror-creator';

// 公共actionTypes
const commAction = mc([
    'SET_SEARCH_TASK_VISIBLE', // 获取当前用户信息
    'REQ_LOADING',
    'REQ_SUCCESS',
    'REQ_ERROR'
], { prefix: 'comm/' })

// Actions
export const showSeach = (boolFlag: any) => {
    return {
        type: commAction.SET_SEARCH_TASK_VISIBLE,
        data: boolFlag
    }
}

// Reducers
// 全局搜索框
export const visibleSearchTask = (state = false, action: any) => {
    switch (action.type) {
        case commAction.SET_SEARCH_TASK_VISIBLE:
            return action.data;
        default:
            return state;
    }
}

// 请状态
export function req (state = 'success', action: any) {
    switch (action.type) {
        case commAction.REQ_LOADING:
            return 'loading';
        case commAction.REQ_SUCCESS:
            return 'success';
        case commAction.REQ_ERROR:
            return 'error';
        default:
            return state;
    }
}
