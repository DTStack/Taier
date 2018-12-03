import { apiManageActionType as ACTION_TYPE } from '../../consts/apiManageActionType';
import API from '../../api/apiManage';

export const apiManageActions = {

    // 获取市场api列表
    getAllApiList (params) {
        return (dispatch) => {
            return API.getAllApiList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_ALL_API_LIST,
                        payload: res.data.data
                    });
                    return res;
                }
            });
        }
    },
    // 获取数据源信息
    getDataSourceByBaseInfo (params) {
        return (dispatch) => {
            return API.getDataSourceByBaseInfo(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 删除api
    deleteApi (params) {
        return (dispatch) => {
            return API.deleteApi(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 开启api
    openApi (apiId) {
        return (dispatch) => {
            return API.updateApiStatus({ apiId: apiId, apiStatus: 0 }).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 关闭api
    closeApi (apiId) {
        return (dispatch) => {
            return API.updateApiStatus({ apiId: apiId, apiStatus: 1 }).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取排行
    getApiCallUserRankList (params) {
        return (dispatch) => {
            return API.getApiCallUserRankList(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取api订购状态
    getApiUserApplyList (params) {
        return (dispatch) => {
            return API.getApiUserApplyList(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 更新节点
    updateCatalogue (params) {
        return (dispatch) => {
            return API.updateCatalogue(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 删除节点
    deleteCatalogue (params) {
        return (dispatch) => {
            return API.deleteCatalogue(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 添加节点
    addCatalogue (params) {
        return (dispatch) => {
            return API.addCatalogue(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 新增api
    createApi (params) {
        return (dispatch) => {
            return API.createApi(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 更新api
    updateApi (params) {
        return (dispatch) => {
            return API.updateApi(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 根据数据源获取数据表
    tablelist (params) {
        return (dispatch) => {
            return API.tablelist(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 根据表名获取表
    tablecolumn (params) {
        return (dispatch) => {
            return API.tablecolumn(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 数据预览
    previewData (params) {
        return (dispatch) => {
            return API.previewData(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取api详细信息
    getApiInfo (params) {
        return (dispatch) => {
            return API.getApiInfo(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api错误信息
    getApiCallErrorInfo (params) {
        return (dispatch) => {
            return API.getApiCallErrorInfoForManager(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    }
}
