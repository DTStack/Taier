import moment from 'moment';

import workbenchAction from '../../consts/workbenchActionType';
import modalAction from '../../consts/modalActionType';
import { CATALOGUE_TYPE } from '../../consts';
import API from '../../api';

/**
 * 更新Modal对象
 * @param {Object} value 包含Modal对象的类型和数据
 */
export const updateModal = (value) => {
    return { type: modalAction.UPDATE_MODAL, data: value }
}

/**
 * 关闭页面Modal
 */
export const resetModal = () => {
    return { type: modalAction.RESET_MODAL }
}

/**
 * 切换Tab
 * @param {Integer} currentTab 
 */
export const switchTab = function(currentTab) {
    return {
        type: workbenchAction.SWITCH_TAB,
        payload: parseInt(currentTab, 10)
    }
}

/**
 * 关闭Tab
 * @param {Object} tabData Tab数据 
 */
export const openTab = function(tabData) {
    return {
        type: workbenchAction.OPEN_TAB,
        payload: tabData,
    }
}

/**
 * 关闭Tab
 * @param {*} currentTab 关闭的TabID
 */
export const closeTab = function(currentTab) {
    return {
        type: workbenchAction.CLOSE_TAB,
        payload: parseInt(currentTab, 10)
    }
}

/**
 * 关闭Tab
 * @param {*} type 关闭（其他-OHTERS，所有-ALL）
 */
export const closeTabs = function(type) {
    if (type === 'OHTERS') {
        return {
            type: workbenchAction.CLOSE_OTHERS,
        }
    } else if (type === 'ALL') {
        return {
            type: workbenchAction.CLOSE_ALL,
        }
    }
}

/**
 * 更新指定的Tab内容
 * @param {Object} tabData tab对象数据
 */
export const updateTab = function(tabData) {
}

/**
 * 触发SQL查询
 */
export function onSQLQuery(params) {

    return (dispatch, getStore) => {
        const { workbench } = getStore();
        console.log('onSqlQuery:', workbench);
        const { tabs } = workbench.mainBench;

        let sqlQueryTabIndex = 0;
        for (let i = 0; i < tabs.length; i++) {
            if (tabs[i].actionType === workbenchAction.OPEN_SQL_QUERY) {
                if (tabs[i].sqlQueryTabIndex > sqlQueryTabIndex) {
                    sqlQueryTabIndex = tabs[i].sqlQueryTabIndex;
                }
            }
        }
        const defaultSQLQueryTabData = {
            id: moment().valueOf(),
            name: `Query ${sqlQueryTabIndex + 1}`,
            sqlQueryTabIndex: sqlQueryTabIndex + 1,
            actionType: workbenchAction.OPEN_SQL_QUERY,
        }

        dispatch(openTab(defaultSQLQueryTabData));
    }
}

/**
 * 加载左侧树形目录数据
 */
export const loadCatalogue = function(params, fileType) {
    return async (dispatch) => {

        let res = {};
        // 获取表下的DataMap
        if (fileType === CATALOGUE_TYPE.TABLE) {
            res = await API.getDataMapsByTable({
                tableId: params.tableId,
                databaseId: params.databaseId,
            });
        // 获取数据库下的所有表
        } else if (fileType === CATALOGUE_TYPE.DATA_BASE) {
            res = await API.getTablesByDB({
                databaseId: params.databaseId,
            });
        } else {
            res = await API.getDatabases(params);
        }

        if (res.code === 1) {
            params.children = res.data;
            dispatch({
                type: workbenchAction.LOAD_CATALOGUE_DATA,
                payload: params,
            })
        }
    }
}
