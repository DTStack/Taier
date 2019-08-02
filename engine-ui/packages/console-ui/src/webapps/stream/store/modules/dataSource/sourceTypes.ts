import Api from '../../../api'
import { dataSourceAction } from './actionTypes'

// Action
export function getSourceTypes () {
    return (dispatch: any) => {
        Api.getDataSourceTypes().then((res: any) => {
            return dispatch({
                type: dataSourceAction.GET_DATA_SOURCE_TYPES,
                data: res.data || []
            })
        })
    }
}

export function sourceTypes (state: any = [], action: any) {
    switch (action.type) {
        case dataSourceAction.GET_DATA_SOURCE_TYPES: {
            return action.data
        }
        default:
            return state;
    }
}
