import Api from '../api/operation'
import { operationActions } from '../consts/operationActions'

export function getProjectList (params: any) {
    return (dispatch: any) => {
        Api.getProjectList(params).then((res: any) => {
            if (res.code === 1) {
                dispatch({
                    type: operationActions.GET_PROJECT_LIST,
                    data: res?.data ?? []
                })
            }
        })
    }
}

export function getPersonList () {
    return (dispatch: any) => {
        Api.getPersonInCharge().then((res: any) => {
            if (res.code === 1) {
                dispatch({
                    type: operationActions.GET_PERSON_LIST,
                    data: res?.data ?? []
                })
            }
        });
    }
}
