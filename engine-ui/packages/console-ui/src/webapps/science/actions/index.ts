// import commonActionType from '../consts/commonActionType';
// import API from '../api';

const commonActions: any = {
    getUserList (params: any) {
        return (dispatch: any) => {
            // API.getUserList(params).then((res: any) => {
            //     if (res.code === 1) {
            //         dispatch({
            //             type: commonActionType.GET_USER_LIST,
            //             payload: res.data
            //         });
            //     }
            // });
        };
    },
    getAllDict (params: any) {
        return (dispatch: any) => {
            // API.getAllDict(params).then((res: any) => {
            //     if (res.code === 1) {
            //         dispatch({
            //             type: commonActionType.GET_ALL_DICT,
            //             payload: res.data
            //         });
            //     }
            // });
        };
    }
};

export default commonActions;
