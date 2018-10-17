import API from '../api';
import moment from 'moment';
import workbenchAction from '../consts/workbenchActionType';
import modalAction from '../consts/modalActionType';
import { notification } from 'antd';

// import { CATALOGUE_} from '../consts';

export const updateModal = (value) => {
    return { type: modalAction.UPDATE_MODAL, data: value }
}

export const resetModal = () => {
    return { type: modalAction.RESET_MODAL }
}


export function switchTab(currentTab) {
    return {
        type: workbenchAction.SWITCH_TAB,
        payload: parseInt(currentTab, 10)
    }
}

export function openTab(tabData) {
    return {
        type: workbenchAction.OPEN_TAB,
        payload: tabData,
    }
}

export function closeTab(currentTab) {
    return {
        type: workbenchAction.CLOSE_TAB,
        payload: parseInt(currentTab, 10)
    }
}

export function updateTab(tab) {

}

export function onCreateDB() {
    const modalValue = {
        visibleModal: workbenchAction.OPEN_CREATE_DATABASE,
        modalData: null,
    }
    return updateModal(modalValue);
}

export function onCreateDataMap() {

}

export function onCreateTable() {

}

export function onSQLQuery() {

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
            id: moment().unix(),
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
export function loadCatalogue(params) {
    return async (dispatch) => {

        const defaultParams = {
            nodePid: 0,
            level: 2,
        }
        const reqParams = params ? Object.assign(defaultParams, params) : defaultParams;
        const res = await API.loadCatalogue(reqParams);

        if (res.code === 1) {
            dispatch({
                type: workbenchAction.LOAD_CATALOGUE_DATA,
                payload: res.data,
            })
        }
    }
}

/**
 * 生成建表语句
 */
export function onGenerateCreateSQL(tableId) {
    console.log('onCreate:', tableId)

    return async dispatch => {

        const res = await API.getCreateSQL({
            tableId,
        });
    
        if (res.code === 1) {
            const modalValue = {
                visibleModal: workbenchAction.GENERATE_CREATE_SQL,
                modalData: res.data,
            }
            return dispatch(updateModal(modalValue))
        } else {
            notification.error({
                message: '提示',
                description: '生成建表语句失败！'
            });
        }
        return dispatch(resetModal());
    }
}