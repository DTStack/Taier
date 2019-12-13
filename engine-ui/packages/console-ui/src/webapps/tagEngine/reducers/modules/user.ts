import mc from 'mirror-creator';
import Api from '../../api'

const userAction = mc([
    'GET_USER', // 获取当前用户信息
    'UPDATE_USER' // 更新当前用户信息
], { prefix: 'user/' })

// Action
export function getUser () {
    return (dispatch: any) => {
        Api.getLoginedUser({}).then((res: any) => {
            if (res.code === 1) {
                return dispatch({
                    type: userAction.UPDATE_USER,
                    data: res.data
                })
            }
        })
    }
}

export function updateUser (fields: any) {
    return {
        type: userAction.UPDATE_USER,
        data: fields
    }
}

export function user (state: any = {}, action: any) {
    switch (action.type) {
        case userAction.GET_USER:
            return action.data
        case userAction.UPDATE_USER: {
            if (action.data !== null) {
                return { ...state, ...action.data }
            }
            return state;
        }
        default:
            return state
    }
}
