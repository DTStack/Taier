import { tableAction, logAction, cataloguesAction, desensitizationAction } from './actionType';
import { message } from 'antd';
import tableMaApi from '../../../api/dataManage';
import ajax from '../../../api';

/**
 * @description
 * @param {any} resData
 * @returns tableData
 */
function formatTableData (resData: any) {
    let tableData: any = { ...resData.table };
    tableData.desc = tableData.tableDesc;

    tableData.columns = resData.column.map((o: any) => {
        let result = Object.assign(o, {
            uuid: o.name,
            isSaved: true // 前端用来区分是否已保存
        });

        return result;
    });
    tableData.partition_keys = resData.partition.map((o: any) => {
        let result = Object.assign(o, {
            uuid: o.name,
            isSaved: true
        });

        return result;
    });

    return tableData;
}

export default {
    searchTable (params: any) {
        return (dispatch: any) => {
            tableMaApi.newSearchTable(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch(this.loadTableList(res.data))
                }
            })
        }
    },

    getTableDetail (params: any) {
        return (dispatch: any) => {
            tableMaApi.getTable(params).then((res: any) => {
                if (res.code === 1) {
                    let tableData = formatTableData(res.data);
                    dispatch(this.loadTableDetail(tableData));
                }
            })
        }
    },

    loadTableDetail (payload: any) {
        return {
            type: tableAction.LOAD_TABLE_DETAIL,
            payload
        }
    },

    loadTableList (payload: any) {
        return {
            type: tableAction.LOAD_TABLE_LIST,
            payload
        }
    },

    modifyDesc (params: any) {
        return {
            type: tableAction.MODIFY_DESC,
            payload: params
        }
    },

    addRow (params: any) {
        return {
            type: tableAction.ADD_ROW,
            payload: params
        }
    },

    delRow (params: any) {
        return {
            type: tableAction.DEL_ROW,
            payload: params
        }
    },

    replaceRow (params: any) {
        return {
            type: tableAction.REPLACE_ROW,
            payload: params
        }
    },

    moveRow (params: any) {
        return {
            type: tableAction.MOVE_ROW,
            payload: params
        }
    },
    saveTable (params: any) {
        return (dispatch: any) => {
            tableMaApi.saveTable(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('保存成功')
                    dispatch(this.saveStatus(res.code))
                }
            })
        }
    },
    getUsers (params: any) {
        return (dispatch: any) => {
            ajax.getProjectUsers(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch(this.getProjectUsersDataSuc(res.data))
                }
            })
        }
    },
    getProjectUsersDataSuc (params: any) {
        return {
            type: logAction.GET_USERS_SUC,
            payload: params
        }
    },
    saveStatus (code: any) {
        return {
            type: tableAction.SAVE_TABLE,
            payload: code
        }
    },
    getCatalogues (params: any) {
        return (dispatch: any) => {
            tableMaApi.getDataCatalogues(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch(this.getDataCatalogues(res.data))
                }
            })
        }
    },
    getDataCatalogues (data: any) {
        return {
            type: cataloguesAction.DATA_CATALOGUES,
            payload: data
        }
    },
    getdesRulesList (params: any) {
        return (dispatch: any) => {
            tableMaApi.getdesRulesList(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch(this.getdesRulesListData(res.data))
                }
            })
        }
    },
    getdesRulesListData (data: any) {
        return {
            type: desensitizationAction.GET_DESENSITIZATION_RULES,
            payload: data
        }
    }
}
