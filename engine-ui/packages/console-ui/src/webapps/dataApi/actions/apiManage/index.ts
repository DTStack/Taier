import { apiManageActionType as ACTION_TYPE } from '../../consts/apiManageActionType';
import API from '../../api/apiManage';

export const apiManageActions: any = {

    // 获取市场api列表
    getAllApiList (params: any) {
        return (dispatch: any) => {
            return API.getAllApiList(params).then((res: any) => {
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
    // 获取安全组列表
    getSecuritySimpleList (params: any) {
        return (dispatch: any) => {
            return API.getSecuritySimpleList(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_SECURITY_LIST,
                        payload: res.data
                    });
                    return res;
                }
            });
        }
    },
    // 获取数据源信息
    getDataSourceByBaseInfo (params: any) {
        return (dispatch: any) => {
            return API.getDataSourceByBaseInfo(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 删除api
    deleteApi (params: any) {
        return (dispatch: any) => {
            return API.deleteApi(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 开启api
    openApi (apiId: any) {
        return (dispatch: any) => {
            return API.createApi({ apiId: apiId }).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 关闭api
    closeApi (apiId: any) {
        return (dispatch: any) => {
            return API.updateApiStatus({ apiId: apiId, apiStatus: 1 }).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取排行
    getApiCallUserRankList (params: any) {
        return (dispatch: any) => {
            return API.getApiCallUserRankList(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取api订购状态
    getApiUserApplyList (params: any) {
        return (dispatch: any) => {
            return API.getApiUserApplyList(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 更新节点
    updateCatalogue (params: any) {
        return (dispatch: any) => {
            return API.updateCatalogue(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 删除节点
    deleteCatalogue (params: any) {
        return (dispatch: any) => {
            return API.deleteCatalogue(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 添加节点
    addCatalogue (params: any) {
        return (dispatch: any) => {
            return API.addCatalogue(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 新增api
    createApi (params: any) {
        return (dispatch: any) => {
            return API.createApi(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 保存Api
    saveOrUpdateApiInfo (params: any) {
        return (dispatch: any) => {
            return API.saveOrUpdateApiInfo(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 更新api
    updateApi (params: any) {
        return (dispatch: any) => {
            return API.updateApi(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 根据数据源获取数据表
    tablelist (params: any) {
        return (dispatch: any) => {
            return API.tablelist(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 根据表名获取表
    tablecolumn (params: any) {
        return (dispatch: any) => {
            return API.tablecolumn(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 数据预览
    previewData (params: any) {
        return (dispatch: any) => {
            return API.previewData(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 获取api详细信息
    getApiInfo (params: any) {
        return (dispatch: any) => {
            return API.getApiInfo(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 查看api错误信息
    getApiCallErrorInfo (params: any) {
        return (dispatch: any) => {
            return API.getApiCallErrorInfoForManager(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // sql格式化
    sqlFormat (params: any) {
        return (dispatch: any) => {
            return API.sqlformat(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // sql解析
    sqlParser (params: any) {
        return (dispatch: any) => {
            return API.sqlParser(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // api测试
    apiTest (params: any) {
        return (dispatch: any) => {
            return API.apiTest(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    },
    // 是否点击代码文案
    clickCode () {
        return {
            type: ACTION_TYPE.CHNAGE_CODE_CLICK,
            payload: true
        }
    }
}
