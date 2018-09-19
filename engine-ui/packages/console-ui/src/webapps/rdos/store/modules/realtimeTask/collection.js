import { combineReducers } from 'redux';
import { cloneDeep } from 'lodash';

import { store } from '../../index';
import { setCurrentPage } from "./browser";
import { collect_type } from "../../../comm/const"
import ajax from "../../../api"

export const dataKey = "ide_collection"

const initState = {
    currentStep: 0,
    sourceMap: {
        table: [],
        sourceId: undefined,
        collectType: collect_type.ALL
    },
    targetMap: {
        sourceId:undefined,
        topic:undefined
    },
}

function getCurrentPage() {
    const state = store.getState();
    const { realtimeTask } = state;
    const currentPage = realtimeTask.currentPage;
    return currentPage;
}

function setCurrentPageValue(dispatch, key, value, isDirty) {
    const page = cloneDeep(getCurrentPage());
    page[key] = value;
    if (typeof isDirty == "boolean") {
        page.notSynced = isDirty
    }
    dispatch(setCurrentPage(page));
}

function initCurrentPage(dispatch) {
    const page = cloneDeep(getCurrentPage());
    dispatch(setCurrentPage({...page,...initState}));
}

export const actions = {
    navtoStep(step) {
        return dispatch=>{
            setCurrentPageValue(dispatch, "currentStep",step)
        }
    },
    getDataSource() {
        return dispatch => {
            ajax.getStreamDataSourceList()
                .then(res => {
                    let data = []
                    if (res.code === 1) {
                        data = res.data
                    }
                    setCurrentPageValue(dispatch, "dataSourceList", data);
                });
        }
    },
    /**
     * 获取实时采集task初始化信息
     * @param {Int} taskId 
     */
    initCollectionTask(taskId) {
        return dispatch => {
            const page = getCurrentPage();
            /**
             * 假如已经存在这个属性，则说明当前的task不是第一次打开，所以采用原来的数据
             */
            if (typeof page.currentStep!="undefined") {
                return;
            }
            initCurrentPage(dispatch);
            ajax.getRealtimeJobData({
                taskId
            }).then((res) => {
                if (res.data) {
                    updateSourceMap(res.data.sourceMap)
                    updateTargetMap(res.data.targetMap)
                    setCurrentPageValue(dispatch, "currentStep", 2);
                }
            })
        }
    },

    updateSourceMap(params = {}, clear) {
        return dispatch => {
            const page = getCurrentPage();
            let { sourceMap } = page;
            if (clear) {
                sourceMap = initState.sourceMap;
            }
            setCurrentPageValue(dispatch, "sourceMap",
                cloneDeep({
                    ...sourceMap,
                    ...params
                }),
                true
            )
        }
    },

    updateTargetMap(params = {}, clear) {
        return dispatch => {
            const page = getCurrentPage();
            let { targetMap } = page;
            if (clear) {
                targetMap = initState.targetMap;
            }
            setCurrentPageValue(dispatch, "targetMap",
                cloneDeep({
                    ...targetMap,
                    ...params
                }),
                true
            )
        }
    },
}


