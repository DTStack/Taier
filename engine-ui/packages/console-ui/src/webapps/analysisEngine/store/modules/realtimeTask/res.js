import Api from '../../../api'
import { resAction } from './actionTypes'

/* eslint-disable */

// Action
export function getResources() {
    return dispatch => {
        Api.getResList().then(res => {
            return dispatch({
                type: resAction.GET_RESOURCE,
                data: res.data
            })
        })
    }
}

export function addRes(res) {
    return dispatch => {
        return dispatch({
            type: resAction.ADD_RESOURCE,
            data: res
        })
    }
}

export function removeRes(res, index) {
    return dispatch => {
        Api.deleteRes({
            resourceId: res.id,
            resourceUrl: res.url
        }).then(res => {
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
export function resources(state = [], action) {
    switch (action.type) {
    case resAction.GET_RESOURCE:
        return action.data;
    case resAction.REMOVE_RESOURCE:
        state.splice(action.data, 1)
        let newState = [...state]
        return newState;
    case resAction.ADD_RESOURCE:
        let afterAdded = [...state, action.data]
        return afterAdded;
    default:
        return state;
    }
}
/* eslint-disable */
