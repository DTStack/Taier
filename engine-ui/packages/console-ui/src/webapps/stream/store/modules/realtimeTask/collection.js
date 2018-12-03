import { cloneDeep } from 'lodash';

import { store } from '../../index';
import { setCurrentPage } from './browser';
import { collectType, CAT_TYPE } from '../../../comm/const'
import ajax from '../../../api'

export const dataKey = 'ide_collection'

const initState = {
    currentStep: null,
    sourceMap: {
        type: undefined,
        port: undefined,
        table: [],
        sourceId: undefined,
        collectType: collectType.ALL,
        cat: [CAT_TYPE.INSERT, CAT_TYPE.UPDATE, CAT_TYPE.DELETE]
    },
    targetMap: {
        sourceId: undefined,
        topic: undefined
    }
}

function getCurrentPage () {
    const state = store.getState();
    const { realtimeTask } = state;
    const currentPage = realtimeTask.currentPage;
    return currentPage;
}

function setCurrentPageValue (dispatch, key, value, isDirty) {
    const page = cloneDeep(getCurrentPage());
    page[key] = value;
    if (typeof isDirty == 'boolean') {
        page.notSynced = isDirty
    }
    dispatch(setCurrentPage(page));
}

function initCurrentPage (dispatch) {
    const page = cloneDeep(getCurrentPage());
    dispatch(setCurrentPage({ ...page, ...initState }));
}

export const actions = {
    navtoStep (step) {
        return dispatch => {
            setCurrentPageValue(dispatch, 'currentStep', step)
        }
    },
    getDataSource () {
        return dispatch => {
            ajax.getStreamDataSourceList()
                .then(res => {
                    let data = []
                    if (res.code === 1) {
                        data = res.data
                    }
                    setCurrentPageValue(dispatch, 'dataSourceList', data);
                });
        }
    },
    /**
     * 获取实时采集task初始化信息
     * @param {Int} taskId
     */
    initCollectionTask (taskId) {
        return dispatch => {
            const page = getCurrentPage();
            /**
             * 假如已经存在这个属性，则说明当前的task不是第一次打开，所以采用原来的数据
             */
            if (typeof page.currentStep != 'undefined') {
                return;
            }
            initCurrentPage(dispatch);
            if (page.taskVersions && page.taskVersions.length) {
                setCurrentPageValue(dispatch, 'isEdit', true);
            }
            ajax.getRealtimeJobData({
                taskId
            }).then((res) => {
                if (res.data) {
                    if (res.data.sourceMap.journalName) {
                        res.data.sourceMap.collectType = collectType.FILE;
                    } else if (res.data.sourceMap.timestamp) {
                        res.data.sourceMap.collectType = collectType.TIME;
                    } else {
                        res.data.sourceMap.collectType = collectType.ALL;
                    }
                    dispatch(actions.updateSourceMap(res.data.sourceMap, false, true));
                    dispatch(actions.updateTargetMap(res.data.targetMap, false, true));
                    setCurrentPageValue(dispatch, 'currentStep', 2);
                } else {
                    setCurrentPageValue(dispatch, 'currentStep', 0);
                }
            })
        }
    },

    updateSourceMap (params = {}, clear, notDirty) {
        return dispatch => {
            const page = getCurrentPage();
            let { sourceMap } = page;
            if (clear) {
                sourceMap = {
                    ...initState.sourceMap,
                    type: sourceMap.type
                };
            }
            setCurrentPageValue(dispatch, 'sourceMap',
                cloneDeep({
                    ...sourceMap,
                    ...params
                }),
                !notDirty
            )
        }
    },

    updateTargetMap (params = {}, clear, notDirty) {
        return dispatch => {
            const page = getCurrentPage();
            let { targetMap } = page;
            if (clear) {
                targetMap = initState.targetMap;
            }
            setCurrentPageValue(dispatch, 'targetMap',
                cloneDeep({
                    ...targetMap,
                    ...params
                }),
                !notDirty
            )
        }
    }
}
