import consoleActions from '../../consts/consoleActions'
import { cloneDeep } from 'lodash'
const defaultState = {
    userList: []
}
export default function (state = defaultState, action) {
    switch (action.type) {
    case consoleActions.SET_USER_LIST: {
        const list = action.data;
        const newState = cloneDeep(state)
        newState.userList = list;
        return newState
    }
    default:
        return state
    }
}
