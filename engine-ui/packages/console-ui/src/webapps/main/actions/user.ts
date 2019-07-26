import UserApi from 'main/api/user'
import userActions from 'main/consts/userActions'

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

export function getInitUser () {
    const user = UserApi.getInitUser()

    return {
        type: userActions.GET_USER,
        data: user
    }
}

export function updateUser (fields) {
    return {
        type: userActions.UPDATE_USER,
        data: fields
    }
}
