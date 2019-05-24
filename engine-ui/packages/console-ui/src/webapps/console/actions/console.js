import consoleApi from '../api/console'
import { userActions, clusterActions } from '../consts/consoleActions'

// Action
export function getUser () {
    return (dispatch) => {
        consoleApi.getTenantList({ pageSize: 9999, currentPage: 1 }).then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: userActions.SET_USER_LIST,
                    data: res.data.data
                })
            }
        })
    }
}

export function updateEngineList (fields) {
    return {
        type: clusterActions.UPDATE_ENGINE_LIST,
        data: fields
    }
}
