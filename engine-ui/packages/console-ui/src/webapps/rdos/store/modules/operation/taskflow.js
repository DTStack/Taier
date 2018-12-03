import localDb from 'utils/localDb'
import { taskFlowAction, graphAction } from './actionTypes'

// Actions
export function getTaskFlow () {
    return { type: taskFlowAction.GET_TASK_FLOW }
}

export function setTaskFlow (value) {
    return {
        type: taskFlowAction.SET_TASK_FLOW,
        data: value
    }
}

export function setTaskFlowType (value) {
    return {
        type: taskFlowAction.SET_TASK_FLOW_TYPE,
        data: value
    }
}

// Actions for graph
export function udpateGraphStatus (value) {
    return {
        type: graphAction.UPDATE_GRAPH_STATUS,
        data: value
    }
}

// Reducer
export function taskFlow (state = '', action) {
    switch (action.type) {
        case taskFlowAction.SET_TASK_FLOW: {
            return action.data
        }
        default:
            return state;
    }
}

export function graphStatus (state = 'initial', action) {
    switch (action.type) {
        case graphAction.UPDATE_GRAPH_STATUS: {
            return action.data
        }
        default:
            return state;
    }
}
