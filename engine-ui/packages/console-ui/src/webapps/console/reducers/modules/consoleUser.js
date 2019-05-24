import { userActions, clusterActions } from '../../consts/consoleActions'
import { cloneDeep } from 'lodash'
const defaultState = {
    userList: [],
    engineList: [] // 集群下engine列表
}
export default function (state = defaultState, action) {
    switch (action.type) {
        case userActions.SET_USER_LIST: {
            const list = action.data;
            const newState = cloneDeep(state)
            newState.userList = list;
            return newState
        }
        case clusterActions.UPDATE_ENGINE_LIST: {
            const list = action.data;
            const newState = cloneDeep(state);
            newState.engineList = list;
            return newState
        }
        default:
            return state
    }
}
