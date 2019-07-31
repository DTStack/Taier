import { userActions } from '../../consts/consoleActions'
import { cloneDeep } from 'lodash'
const defaultState: any = {
    tenantList: []
}
export default function (state = defaultState, action: any) {
    switch (action.type) {
        case userActions.SET_TENANT_LIST: {
            const list = action.data;
            const newState = cloneDeep(state)
            newState.tenantList = list;
            return newState
        }
        default:
            return state
    }
}
