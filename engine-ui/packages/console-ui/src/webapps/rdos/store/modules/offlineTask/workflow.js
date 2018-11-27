import assign from 'object-assign';

import {
    workflowAction
} from './actionType';

const initState = {
    name: undefined, // 名称
    data: undefined, // 任务流数据
    taskType: undefined, // 任务类型
    node: undefined, // 任务节点
    currentNode: undefined,
    status: undefined // 状态
}

export const workflowReducer = (state = initState, action) => {
    switch (action.type) {
    case workflowAction.UPDATE:
        return assign({}, state, action.payload);
    case workflowAction.RESET:
        return initState;
    case workflowAction.CLONE:
        return initState;
    default:
        return state;
    }
};
