import { notification, message } from 'antd';
import moment from 'moment';

import API from '../../api';
import workbenchAction from '../../consts/workbenchActionType';

import { resetModal, openTab, updateModal, closeTab } from './comm';


/**
 * 获取DataMap详情
 */
export function onGetTable(params) {

    return async dispatch => {

        const res = await API.createOrUpdateDB(params);
        if (res.code === 1) {
            const tableData = res.data;
            // 添加Action标记
            tableData.actionType = workbenchAction.OPEN_TABLE,
            dispatch(openTab(tableData));
        } else {
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
};
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
};


/**
 * 点击“新建表”
 * @param {导航数据} params 
 */
export function onCreateTable(params) {
    console.log(params)
    return (dispatch, getStore) => {
        const {workbench} = getStore();
        const { tabs } = workbench.mainBench;
        console.log(tabs)

        let createTableTabIndex = 0;
        for(let i = 0;i<tabs.length;i++){
            if(tabs[i].actionType === workbenchAction.CREATE_TABLE){
                createTableTabIndex = tabs[i].createTableTabIndex > createTableTabIndex?tabs[i].createTableTabIndex:createTableTabIndex
            }
        }

        const newCreateTableTabData = {
            id: moment().unix(),
            tabName: '新建表',
            createTableTabIndex: createTableTabIndex + 1,
            actionType: workbenchAction.CREATE_TABLE,
            databaseId: params ? params.id : undefined,
            tableItem: { databaseId: params ? params.id : undefined },
            currentStep: 0,
        }
        console.log(newCreateTableTabData)

        dispatch(openTab(newCreateTableTabData))
    }
};

export function onEditTable(params){
    console.log(params)
    return async (dispatch, getStore) => {
        const {workbench} = getStore();
        const { tabs } = workbench.mainBench;

        let editTableIndex = 0;
        for(let i = 0;i<tabs.length;i++){
            if(tabs[i].actionType === workbenchAction.OPEN_TABLE_EDITOR){
                editTableIndex = tabs[i].editTableIndex > editTableIndex?tabs[i].editTableIndex:editTableIndex
            }
        }
        //获取表详情
        const res = await API.getTableById({
            databaseId: params.databaseId,
            id: params.id
        })

        if(res.code === 1){
            res.data.columns.map((o,i)=>{
                o._fid = i
            })
            const newEditTableTabData = {
                id: moment().unix(),
                tabName: `编辑${params.tableName}`,
                editTableIndex: editTableIndex + 1,
                actionType: workbenchAction.OPEN_TABLE_EDITOR,
                tableDetail: res.data,
                databaseId: params.databaseId,
                tableId: params.id
            }

            dispatch(openTab(newEditTableTabData))
        }else{
            notification.error({
                message: '提示',
                description: res.message,
            });
        }

    }
}

/*
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
};

export function handleNextStep() {
    console.log('sdsdadsaNEXT')
    return (dispatch,getStore) => {
        let currentStep = 0;
        const { workbench } = getStore();
        const { tabs,currentTab } = workbench.mainBench;
        tabs.map(o=>{
            if(o.id === currentTab){
                currentStep = o.currentStep;
            }
        })

        if(currentStep === 2){
            //提交表单
        }else{
            return dispatch({
                type: workbenchAction.NEXT_STEP
            })
        }
    }
};

export function handleLastStep(){
    return (dispatch,getStore) => {
        const { workbench } = getStore();
        const { currentStep } = workbench.mainBench;

        return dispatch({
            type: workbenchAction.LAST_STEP
        })
    }
};

/**
 * 获取表详情
 */
export function onTableDetail(params){
    return async (dispatch,getStore)=>{
        const { workbench } = getStore();
        const {tabs} = workbench.mainBench;
        const res = await API.getTableById({
            databaseId: params.databaseId,
            id: params.id
        })
        if (res.code === 1){
            let tableDetailIndex = 0;
            for(let i = 0;i<tabs.length;i++){
                if(tabs[i].actionType === workbenchAction.OPEN_TABLE){
                    tableDetailIndex = tabs[i].tableDetailIndex > tableDetailIndex?tabs[i].tableDetailIndex:tableDetailIndex
                }
            }
            const tableDetail = {
                id: moment().unix(),
                tabName: `${params.tableName}详情`,
                tableDetailIndex: tableDetailIndex + 1,
                actionType: workbenchAction.OPEN_TABLE,
                tableDetail:res.data,
            }
            dispatch(openTab(tableDetail))
        }else{
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
};


/**
 * 存储新建表数据至服务端
 */
export function handleSave(){
    return async (dispatch,getStore) => {

        const { workbench } = getStore();
        const { tabs, currentTab } = workbench.mainBench;
        let params = {};
        tabs.map(o=>{
            if(o.id === currentTab){
                params = o.tableItem;
            }
        })
        if(params.lifeCycle === -1){
            params.lifeCycle = params.shortLisyCycle;
            delete params.lifeCycle;
        }
        params.columns.map(o=>{
            delete o._fid
        })

        params.partitions.map(o=>{
            delete o._fid
        })
        // params.databaseId = o.databaseId;

        const res = await API.createTable(params)
        if(res.code === 1){
            console.log('保存成功')
            return dispatch({
                type: workbenchAction.NEW_TABLE_SAVED,
                payload:res.data
            })
        }else{
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
};
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
};
/**
 * 保存表信息
 * @param {预留} param 
 */
export function saveTableInfo(param){
    return (dispatch,getStore)=>{
        const { workbench } = getStore();
        const { tabs, currentTab } = workbench.mainBench;
        let tableDetail = {};
        tabs.map(o=>{
            if(o.id === currentTab){
                tableDetail = o.tableDetail
            }
        })

        const {databaseId,tableName,tableDesc,lifeDay,columns,partitions} = tableDetail;
        columns.map(o=>{
            delete o._fid
        })


        const res = API.saveTableInfo({databaseId,tableName,tableDesc,lifeDay,columns,partitions});
        if(res.code === 1){
            return dispatch({
                type: workbenchAction.TABLE_INFO_MOTIFIED
            })
        }else{
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
}

/**
 * 保存新表/更新表之后跳转到详情
 * @param {databaseId, id}
 */
export function toTableDetail(params){

    return async (dispatch,getStore)=>{
        const { workbench } = getStore();
        const { tabs, currentTab } = workbench.mainBench;

        const res = await API.getTableById(params);
        if(res.code === 1){
            let tableDetailIndex = 0;
            for(let i = 0;i<tabs.length;i++){
                if(tabs[i].actionType === workbenchAction.OPEN_TABLE){
                    tableDetailIndex = tabs[i].tableDetailIndex > tableDetailIndex?tabs[i].tableDetailIndex:tableDetailIndex
                }
            }
            let newTabData = {
                id: moment().unix(),
                tabName: `${res.data.tableName}详情`,
                tableDetailIndex: tableDetailIndex + 1,
                actionType: workbenchAction.OPEN_TABLE,
                tableDetail:res.data,
            }
            // tabData = newTabData;

            dispatch(closeTab(currentTab))
            dispatch(openTab(newTabData));
        }else{
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
}