import UserApi from 'main/api/user'
import userActions from 'main/consts/userActions'

// Action
export function getUser () {
    return (dispatch: any) => {
        const user = UserApi.getLoginedUser();
        if (user) {
            return dispatch({
                type: userActions.GET_USER,
                data: user
            })
        }
    }
}

export function getInitUser () {
    const user = UserApi.getInitUser()

    return {
        type: userActions.GET_USER,
        data: user
    }
}

export function updateUser (fields: any) {
    return {
        type: userActions.UPDATE_USER,
        data: fields
    }
}
