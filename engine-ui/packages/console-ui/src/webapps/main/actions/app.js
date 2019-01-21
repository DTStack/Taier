import appActions from 'main/consts/appActions'
import Api from 'main/api'
export function updateApp (fields) {
    return {
        type: appActions.UPDATE_APP,
        data: fields
    }
}
// licenseAction
export function getLicenseApp () {
    return (dispatch) => {
        Api.getLicenseApp().then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: appActions.GET_LICENSE_APP,
                    data: res.data
                })
            }
        })
    }
}
