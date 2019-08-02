import sourceAction from '../../consts/sourceActionType';
// import API from '../../api';
import { UPLOAD_STATUS } from '../../comm/const'
// import { message } from 'antd';
// const TIME_INTERVAL = 3600; // 1秒
/**
 *  Actions
 */
export const getTableList = (projectId: any) => {
    return (dispatch: any, getState: any) => {
        // API.getTablesByName({
        //     appointProjectId: projectId
        // }).then((res: any) => {
        //     if (res.code == 1) {
        //         let { data } = res;
        //         dispatch({
        //             type: sourceAction.SET_TABLE_LIST,
        //             payload: data.children,
        //             key: projectId
        //         })
        //     }
        // })
    }
}

export const getUploadStatus = (params: any, dispatch: any) => {
    // let timeId: any;
    let status = UPLOAD_STATUS.PROGRESSING;
    dispatch({
        type: sourceAction.UPDATE,
        payload: { ...params, status: status }
    })
    // const getStatus = async () => {
    //     const res = await API.getUploadStatus(params.queryParams);
    //     if (res.data === 'done') {
    //         message.success(`文件${params.fileName}上传成功!`);
    //         clearInterval(timeId);
    //         status = UPLOAD_STATUS.SUCCES;
    //         return dispatch(resetUploader());
    //     } else if (res.code > 1) {
    //         status = UPLOAD_STATUS.FAIL;
    //         clearInterval(timeId);
    //         setTimeout(() => {
    //             dispatch(resetUploader());
    //         }, TIME_INTERVAL)
    //     } else if (res.data === 'exist') {
    //         status = UPLOAD_STATUS.PROGRESSING;
    //     }
    //     dispatch({
    //         type: sourceAction.UPDATE,
    //         payload: { ...params, status: status }
    //     })
    // }
    // timeId = setInterval(getStatus, TIME_INTERVAL);
}

export const resetUploader = () => {
    return {
        type: sourceAction.RESET
    }
}
