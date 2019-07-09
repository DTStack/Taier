import consoleApi from '../api/console'
import { userActions } from '../consts/consoleActions'

// Action
export function getTenantList () {
    return (dispatch) => {
        consoleApi.getTenantList().then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: userActions.SET_TENANT_LIST,
                    data: res.data || []
                })
            }
        })
    }
}
