import API from 'dt-common/src/api'
import { userActions } from '../consts/consoleActions'

// Action
export function getTenantList () {
    return (dispatch: any) => {
        API.getFullTenants().then((res: any) => {
            if (res.success) {
                return dispatch({
                    type: userActions.SET_TENANT_LIST,
                    data: res.data || []
                })
            }
        })
    }
}
