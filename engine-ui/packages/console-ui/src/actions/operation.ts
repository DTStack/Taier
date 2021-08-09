import Api from '../api/operation'
import { operationActions } from '../consts/operationActions'
import { hashHistory } from 'react-router'

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

export const workbenchActions = (dispatch?: any) => {
    return {
        openTaskInDev: (data: any) => {
            // 项目类型
            Api.getYWAppType({
                id: data.id
            }).then((res: any) => {
                if (res.code === 1) {
                    console.log('data: ', data);
                }
            });
            // let path2 = `http://dev.insight.dtstack.cn/batch/#/offline/task?taskId=${data.id}&pid=${data.projectId}`
            // window.open(path2)
            if (data.appType == '10') {
                if (data.taskType == 18) {
                    let path2 = `http://${window.location.hostname}:8099/easy-index/index-define?taskId=${data.id}`;
                    window.open(path2)
                } else if (data.taskType == 7) {
                    hashHistory.push({ pathname: '/operation/dependence', query: { id: data.id } });
                }
            }
        }
    }
}
