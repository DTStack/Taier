import Api from '../../../api'
import { resAction } from './actionTypes'

/* eslint-disable */

// Action
export function getResources() {
    return (dispatch: any) => {
        Api.getResList().then((res: any) => {
            return dispatch({
                type: resAction.GET_RESOURCE,
                data: res.data
            })
        })
    }
}

export function addRes(res: any) {
    return (dispatch: any) => {
        return dispatch({
            type: resAction.ADD_RESOURCE,
            data: res
        })
    }
}

export function removeRes(res: any, index: any) {
    return (dispatch: any) => {
        Api.deleteRes({
            resourceId: res.id,
            resourceUrl: res.url
        }).then((res: any) => {
            if (res.code === 1) {
                return dispatch({
                    type: resAction.REMOVE_RESOURCE,
                    data: index
                })
            }
        })
    }
}

// Reducers
export function resources(state:any = [], action: any) {
    switch (action.type) {
    case resAction.GET_RESOURCE:
        return action.data;
    case resAction.REMOVE_RESOURCE:
        state.splice(action.data, 1)
        let newState: any = [...state]
        return newState;
    case resAction.ADD_RESOURCE:
        let afterAdded: any = [...state, action.data]
        return afterAdded;
    default:
        return state;
    }
}
/* eslint-disable */
