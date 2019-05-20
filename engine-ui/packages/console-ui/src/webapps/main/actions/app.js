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
    return (dispatch, getStore) => {
        const store = getStore();
        if (store.licenseApps.length === 0) {
            Api.getLicenseApp().then(res => {
                if (res.success) {
                    console.log('Licence', res.data)
                    return dispatch({
                        type: appActions.GET_LICENSE_APP,
                        data: [...res.data]
                    })
                }
                dispatch({
                    type: appActions.SET_LICENSE_LOADED
                })
            })
        }
    }
}
