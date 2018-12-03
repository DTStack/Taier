import { dataSourceActions as ACTION_TYPE } from '../../consts/dataSourceActions';
import API from '../../api/dataSource';

export const dataSourceActions = {
    getDataSources (params) {
        return dispatch => {
            dispatch({
                type: ACTION_TYPE.CHANGE_LOADING
            });
            API.getDataSources(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_DATA_SOURCES,
                        payload: res.data
                    });
                }
                dispatch({
                    type: ACTION_TYPE.CHANGE_LOADING
                });
            });
        }
    },
    getDataSourcesType (params) {
        return dispatch => {
            API.getDataSourcesType(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_DATA_SOURCES_TYPE,
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
                        type: ACTION_TYPE.GET_DATA_SOURCES_LIST,
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
                        type: ACTION_TYPE.GET_DATA_SOURCES_TABLE,
                        payload: res.data
                    });
                } else {
                    dispatch({
                        type: ACTION_TYPE.GET_DATA_SOURCES_TABLE,
                        payload: []
                    });
                }
            });
        }
    },
    getDataSourcesColumn (params) {
        return dispatch => {
            API.getDataSourcesColumn(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_DATA_SOURCES_COLUMN,
                        payload: res.data
                    });
                }
            });
        }
    },
    getDataSourcesPart (params) {
        return dispatch => {
            API.getDataSourcesPart(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_DATA_SOURCES_PART,
                        payload: res.data
                    });
                }
            });
        }
    },
    resetDataSourcesPart () {
        return dispatch => {
            dispatch({
                type: ACTION_TYPE.RESET_DATA_SOURCES_PART
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
                        type: ACTION_TYPE.GET_DATA_SOURCES_PREVIEW,
                        payload: res.data
                    });
                }
            });
        }
    }
}
