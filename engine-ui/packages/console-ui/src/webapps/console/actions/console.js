import consoleApi from '../api/console'
import consoleActions from '../consts/consoleActions'

// Action
export function getUser () {
    return (dispatch) => {
        consoleApi.getTenantList({ pageSize: 9999, currentPage: 1 }).then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: consoleActions.SET_USER_LIST,
                    data: res.data.data
                })
            }
        })
    }
}
