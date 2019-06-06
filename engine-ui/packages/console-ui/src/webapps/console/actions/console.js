import consoleApi from '../api/console'
import { userActions, clusterActions } from '../consts/consoleActions'

// Action
export function getTenantList () {
    return (dispatch) => {
        consoleApi.getTenantList().then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: userActions.SET_TENANT_LIST,
                    data: res.data || []
                })
            }
        })
    }
}
export function updateEngineList (fields) {
    return {
        type: clusterActions.UPDATE_ENGINE_LIST,
        data: fields
    }
}
export function updateHadoopComponentList (fields) {
    return {
        type: clusterActions.UPDATE_HADOOP_COMPONENT_LIST,
        data: fields
    }
}
export function updateLibraComponentList (fields) {
    return {
        type: clusterActions.UPDATE_LIBRA_COMPONENT_LIST,
        data: fields
    }
}
