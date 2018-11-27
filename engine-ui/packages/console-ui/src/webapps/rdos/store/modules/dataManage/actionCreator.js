import { tableAction, logAction, cataloguesAction } from './actionType';
import { message } from 'antd';
import tableMaApi from '../../../api/dataManage';
import ajax from '../../../api';

/**
 * @description
 * @param {any} resData
 * @returns tableData
 */
function formatTableData (resData) {
    let tableData = { ...resData.table };
    tableData.desc = tableData.tableDesc;

    tableData.columns = resData.column.map(o => {
        let result = Object.assign(o, {
            uuid: o.name,
            isSaved: true // 前端用来区分是否已保存
        });

        return result;
    });
    tableData.partition_keys = resData.partition.map(o => {
        let result = Object.assign(o, {
            uuid: o.name,
            isSaved: true
        });

        return result;
    });

    return tableData;
}

export default {
    searchTable (params) {
        return dispatch => {
            tableMaApi.newSearchTable(params).then(res => {
                if (res.code === 1) {
                    dispatch(this.loadTableList(res.data))
                }
            })
        }
    },

    getTableDetail (params) {
        return dispatch => {
            tableMaApi.getTable(params).then(res => {
                if (res.code === 1) {
                    let tableData = formatTableData(res.data);
                    dispatch(this.loadTableDetail(tableData));
                }
            })
        }
    },

    loadTableDetail (payload) {
        return {
            type: tableAction.LOAD_TABLE_DETAIL,
            payload
        }
    },

    loadTableList (payload) {
        return {
            type: tableAction.LOAD_TABLE_LIST,
            payload
        }
    },

    modifyDesc (params) {
        return {
            type: tableAction.MODIFY_DESC,
            payload: params
        }
    },

    addRow (params) {
        return {
            type: tableAction.ADD_ROW,
            payload: params
        }
    },

    delRow (params) {
        return {
            type: tableAction.DEL_ROW,
            payload: params
        }
    },

    replaceRow (params) {
        return {
            type: tableAction.REPLACE_ROW,
            payload: params
        }
    },

    moveRow (params) {
        return {
            type: tableAction.MOVE_ROW,
            payload: params
        }
    },
    saveTable (params) {
        return dispatch => {
            tableMaApi.saveTable(params).then(res => {
                if (res.code === 1) {
                    message.success('保存成功')
                    dispatch(this.saveStatus(res.code))
                }
            })
        }
    },
    getUsers (params) {
        return dispatch => {
            ajax.getProjectUsers(params).then(res => {
                if (res.code === 1) {
                    dispatch(this.getProjectUsersDataSuc(res.data))
                }
            })
        }
    },
    getProjectUsersDataSuc (params) {
        return {
            type: logAction.GET_USERS_SUC,
            payload: params
        }
    },
    saveStatus (code) {
        return {
            type: tableAction.SAVE_TABLE,
            payload: code
        }
    },
    getCatalogues (params) {
        return dispatch => {
            tableMaApi.getDataCatalogues(params).then(res => {
                if (res.code === 1) {
                    dispatch(this.getDataCatalogues(res.data))
                }
            })
        }
    },
    getDataCatalogues (data) {
        return {
            type: cataloguesAction.DATA_CATALOGUES,
            payload: data
        }
    }
}
