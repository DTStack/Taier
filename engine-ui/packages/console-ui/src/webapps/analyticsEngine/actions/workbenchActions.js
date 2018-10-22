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

export function onCreateTable(params) {
    return (dispatch, getStore) => {
        const {workbench} = getStore();
        const { tabs } = workbench.mainBench;

        let createTableTabIndex = 0;
        for(let i = 0;i<tabs.length;i++){
            if(tabs[i].actionType === workbenchAction.CREATE_TABLE){
                createTableTabIndex = tabs[i].createTableTabIndex > createTableTabIndex?tabs[i].createTableTabIndex:createTableTabIndex
            }
        }

        const newCreateTableTabData = {
            id: moment().unix(),
            name: '新建表',
            createTableTabIndex: createTableTabIndex + 1,
            actionType: workbenchAction.CREATE_TABLE,
        }
        console.log(newCreateTableTabData)

        dispatch(openTab(newCreateTableTabData))
    }
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

        console.log(defaultSQLQueryTabData)
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
 * 保存新建表数据到storage
 */
export function saveNewTableData(params) {
    console.log(params)
    return (dispatch)=>{
        dispatch({
            type: workbenchAction.NEW_TABLE_INFO_CHANGE,
            payload: params
        })
    }
}

export function handleNextStep() {
    console.log('sdsdadsaNEXT')
    return (dispatch,getStore) => {
        const { workbench } = getStore();
        const { currentStep } = workbench.mainBench;
        if(currentStep === 3){
            //提交表单
        }else{
            return dispatch({
                type: workbenchAction.NEXT_STEP
            })
        }
    }
}

export function handleLastStep(){
    return (dispatch,getStore) => {
        const { workbench } = getStore();
        const { currentStep } = workbench.mainBench;

        return dispatch({
            type: workbenchAction.LAST_STEP
        })
    }
}

/**
 * 获取表详情
 */
export function getTableDetail(){
    return async (dispatch,getStore)=>{
        const { workbench } = getStore();
        const res = await API.getTableDetail({
            tableId,
        })
        if (res.code === 1){
            return dispatch({
                type: workbenchAction.GET_TABLE_DETAIL,
                payload: res.data
            })
        }
    }
}



/**
 * 生成建表语句
 */
export function onGenerateCreateSQL(tableId) {

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

/**
 * 存储新建表数据至服务端
 */
export function handleSave(){
    return (dispatch,getStore) => {

        const { workbench } = getStore();
        const { newanalyEngineTableDataList, currentTab } = workbench.mainBench;

        const res = API.saveNewTable(newanalyEngineTableDataList[`tableItem${currentTab}`])
        if(res.code === 1){
            return dispatch({
                type: workbenchAction.NEW_TABLE_SAVED
            })
        }
    }
}
/**
 * 编辑表页-保存编辑状态
 * @param {更改的参数对象} params 
 */
export function saveEditTableInfo(params){
    return dispatch => {
        return dispatch({
            type: workbenchAction.SAVE_EDITTABLE_INFO,
            payload: params
        })
    }
}
/**
 * 保存表信息
 * @param {预留} param 
 */
export function saveTableInfo(param){
    return (dispatch,getStore)=>{
        const { workbench } = getStore();
        const { editTableInfoList, currentTab } = workbench.mainBench;
    
        const params = editTableInfoList[`tableInfo${currentTab}`]
        const res = API.saveTableInfo(params);
        
        if(res.code === 1){
            return dispatch({
                type: workbenchAction.TABLE_INFO_MOTIFIED
            })
        }
    }
}