import UserApi from '../api/user'
import userActions from '../consts/userActions'

// Action
export function getUser () {
    return (dispatch) => {
        UserApi.getLoginedUser().then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: userActions.GET_USER,
                    data: res.data
                })
            }
        })
    }
}

export function updateUser (fields) {
    return {
        type: userActions.UPDATE_USER,
        data: fields
    }
}

export function getUserList (fields) {
    return (dispatch) => {
        UserApi.getUserList(fields).then(res => {
            if (res.code === 1) {
                dispatch({
                    type: userActions.GET_USER_LIST,
                    data: res.data
                })
            }
        })
    }
}
