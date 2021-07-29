import api from '../api'

export const workbenchActions = (dispatch?: any) => {
    return {
        openTaskInDev: (data: any) => {
            // 项目类型
            api.getYWAppType({
                id: data.id
            }).then((res: any) => {
                if (res.code === 1) {
                    console.log('data: ', data);
                }
            });
            let path2 = `http://dev.insight.dtstack.cn/batch/#/offline/task?taskId=${data.id}&pid=${data.projectId}`
            window.open(path2)
        }
    }
}
