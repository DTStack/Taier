import { dataSourceActionType } from '../../consts/dataSourceActionType';
import API from '../../api/dataSource';

export const dataSourceActions = {
    getDataSources (params) {
        return dispatch => {
            dispatch({
                type: dataSourceActionType.CHANGE_LOADING
            });
            API.getDataSources(params).then((res) => {
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
    getDataSourcesType (params) {
        return dispatch => {
            API.getDataSourcesType(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_TYPE,
                        payload: res.data
                    });
                }
            });
        }
    },
    getDataSourcesList (params) {
        return dispatch => {
            API.getDataSourcesList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_DATA_SOURCES_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getTagDataSourcesList (params) {
        return dispatch => {
            API.getTagDataSourcesList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataSourceActionType.GET_TAG_DATA_SOURCES_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getDataSourcesTable (params) {
        return dispatch => {
            API.getDataSourcesTable(params).then((res) => {
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
        return dispatch => {
            dispatch({
                type: dataSourceActionType.RESET_DATA_SOURCES_TABLE
            });
        }
    },
    getDataSourcesColumn (params) {
        return dispatch => {
            API.getDataSourcesColumn(params).then((res) => {
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
        return dispatch => {
            dispatch({
                type: dataSourceActionType.RESET_DATA_SOURCES_COLUMN
            });
        }
    },
    getDataSourcesPreview (params) {
        return dispatch => {
            API.getDataSourcesPreview(params).then((res) => {
                if (res.code === 1) {
                    let { columnList, dataList } = res.data;
                    res.data.dataList = dataList.map((arr, i) => {
                        let o = {};
                        arr.forEach((item, j) => {
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
