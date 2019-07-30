import { dataSourceActionType } from '../../consts/dataSourceActionType';
import API from '../../api/dataSource';

export const dataSourceActions: any = {
    getDataSources(params: any) {
        return (dispatch: any) => {
            dispatch({
                type: dataSourceActionType.CHANGE_LOADING
            });
            API.getDataSources(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES,
                        payload: res.data
                    });
                }
                dispatch({
                    type: dataSourceActionType.CHANGE_LOADING
                });
            });
        }
    },
    getDataSourcesType(params: any) {
        return (dispatch: any) => {
            API.getDataSourcesType(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_TYPE,
                        payload: res.data
                    });
                }
            });
        }
    },
    getDataSourcesList(params: any) {
        return (dispatch: any) => {
            API.getDataSourcesList(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getTagDataSourcesList(params: any) {
        return (dispatch: any) => {
            API.getTagDataSourcesList(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_TAG_DATA_SOURCES_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getDataSourcesTable(params: any) {
        return (dispatch: any) => {
            API.getDataSourcesTable(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_TABLE,
                        payload: res.data
                    });
                } else {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_TABLE,
                        payload: []
                    });
                }
            });
        }
    },
    resetDataSourcesTable () {
        return (dispatch: any) => {
            dispatch({
                type: dataSourceActionType.RESET_DATA_SOURCES_TABLE
            });
        }
    },
    getDataSourcesColumn(params: any) {
        return (dispatch: any) => {
            API.getDataSourcesColumn(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_COLUMN,
                        payload: res.data
                    });
                }
            });
        }
    },
    resetDataSourcesColumn () {
        return (dispatch: any) => {
            dispatch({
                type: dataSourceActionType.RESET_DATA_SOURCES_COLUMN
            });
        }
    },
    getDataSourcesPreview(params: any) {
        return (dispatch: any) => {
            API.getDataSourcesPreview(params).then((res: any) => {
                if (res.code === 1) {
                    let { columnList, dataList } = res.data;
                    res.data.dataList = dataList.map((arr: any, i: any) => {
                        let o: any = {};
                        arr.forEach((item: any, j: any) => {
                            o.key = i;
                            o[columnList[j]] = item;
                        })
                        return o;
                    });
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_PREVIEW,
                        payload: res.data
                    });
                }
            });
        }
    }
}
