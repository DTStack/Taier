import UserApi from '../api/user'
import userActions from '../consts/userActions'

// Action
export function getUser () {
    return (dispatch: any) => {
        UserApi.test()
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
    }
}
