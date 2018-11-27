import appActions from 'main/consts/appActions'

export function updateApp (fields) {
    return {
        type: appActions.UPDATE_APP,
        data: fields
    }
}
