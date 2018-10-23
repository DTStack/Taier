import { notification, message } from 'antd';

import API from '../../api';
import workbenchAction from '../../consts/workbenchActionType';

import { resetModal, openTab, updateModal } from './comm';


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
};

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
};


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