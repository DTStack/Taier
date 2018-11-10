import { notification, message } from 'antd';
import moment from 'moment';

import API from '../../api';
import { CATALOGUE_TYPE } from '../../consts';
import workbenchAction from '../../consts/workbenchActionType';

import gloablActions from '../index';
import { resetModal, openTab, updateModal, closeTab, loadCatalogue } from './comm';

/**
 * 生成建表语句
 */
export function onGenerateCreateSQL({ tableId, databaseId }) {

    return async dispatch => {
        const res = await API.getCreateSQL({
            tableId,
            databaseId,
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
        const name = params && params.name ? params.name + ' - ' : '';

        const newCreateTableTabData = {
            id: moment().unix(),
            tabName: `${name} 新建表 ${createTableTabIndex + 1}`,
            createTableTabIndex: createTableTabIndex + 1,
            actionType: workbenchAction.CREATE_TABLE,
            databaseId: params ? params.id : undefined,
            tableItem: {
                databaseId: params ? params.id : undefined,
                compactionSize: '1024',
                type: 0,
                sortScope: 0,
                lifeCycle: 90,
                autoLoadMerge: 0,
                levelThreshold: '4,3',
                preserveSegments: 0,
                allowCompactionDays:0,
                blockSize: 1024,
                compactType: 0,
                partitions: {
                    partitionType:0,
                    partConfig: undefined,
                    columns: [],
                },
                bucketInfo: {
                    bucketNumber: undefined,
                    infos: []
                }
             },
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
        if(params.partitions.partitionType !== 0 && !params.partitions.partConfig){
            notification.error({
                message: '提示',
                description: params.partitions.partitionType === 1?'分区数量不能为空':params.partitions.partitionType === 2?'分区范围不能为空':'分区名称不能为空'
            })
            return;
        }
        if(!params.bucketInfo.bucketNumber && (params.bucketInfo.infos && params.bucketInfo.infos.length !== 0)){
            notification.error({
                message: '提示',
                description: '分桶数量不能为空'
            })
            return;
        }
        let stopFlag = false;
        params.columns.map(o=>{
            if(!o.name || o.name === '' || !o.type){
                notification.error({
                    message: '提示',
                    description: '字段名称与字段类型不可为空'
                })
                stopFlag = true;
            }
        })
        if(stopFlag)    return;
        else    stopFlag = false;

        params.partitions.columns.map(o=>{
            if(o.name === '' || o.type === ''){
                notification.error({
                    message: '提示',
                    description: '分区名称与分区类型不可为空'
                })
                stopFlag = true;
            }
        })

        if(stopFlag)    return;
        else    stopFlag = false;

        if(params.type === 1){
            params.location = params.location
        }

        if(params.lifeCycle === -1){
            params.lifeCycle = params.shortLisyCycle;
            // delete params.shortLisyCycle;
        }
        params.partConfig = params.partitions.partConfig;
        params.partitionType = params.partitions.partitionType;
        params.partitions = params.partitions.columns;



        const res = await API.createTable(params)
        if(res.code === 1){
            console.log('保存成功');
            const data = res.data;
            // 重新加载Table列表
            dispatch(gloablActions.getAllTable());
            // 重新Reload数据库下的表左侧目录
            dispatch(loadCatalogue({
                id: data.databaseId,
            }, CATALOGUE_TYPE.DATA_BASE));
            return dispatch({
                type: workbenchAction.NEW_TABLE_SAVED,
                payload: data
            })
        }else{
            params.partitions = {
                partConfig: params.partConfig,
                partitionType: params.partitionType,
                columns: params.partitions
            }
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
    return async (dispatch,getStore)=>{
        const { workbench } = getStore();
        const { tabs, currentTab } = workbench.mainBench;
        let tableDetail = {};
        tabs.map(o=>{
            if(o.id === currentTab){
                tableDetail = o.tableDetail
            }
        })

        console.log(tableDetail)
        let {databaseId,tableName,tableDesc,lifeDay,columns,partitions, id} = tableDetail;
        let stopFlag = false;
        columns.map(o=>{
            if((!o.name || o.name === '' || !o.type) && !stopFlag){
                notification.error({
                    message: '提示',
                    description: '字段名称与字段类型不可为空'
                })
                stopFlag = true;
            }
        })
        if(stopFlag) return;

        lifeDay = lifeDay === -1? tableDetail.shortLisyCycle:lifeDay;

        let flag = [];
        columns.map(o=>{
            if(o.isNew){
                flag.push({
                    comment: o.comment,
                    dictionary: o.dictionary,
                    invert: o.invert,
                    name: o.name,
                    sortColumn: o.sortColumn,
                    type: o.type,
                    precision: o.precision || null,
                    scale: o.scale || null,
                })
            }
        })
        console.log(tableDetail)


        const res = await API.saveTableInfo({databaseId,tableName,tableDesc,lifeDay,columns:flag,partitions,id});
        if(res.code === 1){
            message.success('修改成功')
            dispatch(handleCancel());
            dispatch(onEditTable({databaseId: tableDetail.databaseId,id:tableDetail.id, tableName}))
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

export function handleCancel(){
    return (dispatch,getStore)=>{
        const {currentTab} = getStore().workbench.mainBench;
        
        dispatch(closeTab(currentTab))
    }
}