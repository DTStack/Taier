import API from '../api';
import moment from 'moment';
import workbenchAction from '../consts/workbenchActionType';
// import { CATALOGUE_} from '../consts';

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


export function onCreateDb() {

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
 * 保存新建表数据
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