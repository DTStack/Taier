import UserApi from '../api/user'
import userActions from '../consts/userActions'

// Action
export function getUser () {
    return (dispatch: any) => {
        UserApi.getLoginedUser().then((res: any) => {
            if (res.code === 1) {
                return dispatch({
                    type: userActions.GET_USER,
                    data: res.data
                })
            }
        })
    }
}

export function updateUser (fields: any) {
    return {
        type: userActions.UPDATE_USER,
        data: fields
    }
}

export function getUserList (fields: any) {
    return (dispatch: any) => {
        UserApi.getUserList(fields).then((res: any) => {
            if (res.code === 1) {
                dispatch({
                    type: userActions.GET_USER_LIST,
                    data: res.data
                })
            }
        })
    }
}
