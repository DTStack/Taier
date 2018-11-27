import Api from '../../../api'
import { dataSourceAction } from './actionTypes'

// Action
export function getSourceTypes () {
    return (dispatch) => {
        Api.getDataSourceTypes().then((res) => {
            return dispatch({
                type: dataSourceAction.GET_DATA_SOURCE_TYPES,
                data: res.data || []
            })
        })
    }
}

export function sourceTypes (state = [], action) {
    switch (action.type) {
    case dataSourceAction.GET_DATA_SOURCE_TYPES: {
        return action.data
    }
    default:
        return state;
    }
}
