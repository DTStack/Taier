import { cloneDeep } from 'lodash';

import { store } from '../../index';
import { setCurrentPage } from './browser';
import { collectType, CAT_TYPE } from '../../../comm/const'
import ajax from '../../../api'

export const dataKey = 'ide_collection'

export interface SettingMap {
    speed?: number;
    channel?: number;
    isSaveDirty?: boolean;
    sourceId?: number;
    tableName?: string;
    lifeDay?: number;
}

const initState: any = {
    currentStep: null,
    sourceMap: {
        type: undefined,
        port: undefined,
        table: [],
        sourceId: undefined,
        collectType: collectType.ALL,
        cat: [CAT_TYPE.INSERT, CAT_TYPE.UPDATE, CAT_TYPE.DELETE],
        pavingData: true
    },
    targetMap: {
        sourceId: undefined,
        topic: undefined
    },
    settingMap: { // 通道控制
        speed: '-1',
        channel: 1
    }
}

function getCurrentPage () {
    const state = store.getState();
    const { realtimeTask } = state;
    const currentPage = realtimeTask.currentPage;
    return currentPage;
}

function setCurrentPageValue (dispatch: any, key: any, value: any, isDirty?: any) {
    const page = cloneDeep(getCurrentPage());
    page[key] = value;
    if (typeof isDirty == 'boolean') {
        page.notSynced = isDirty
    }
    dispatch(setCurrentPage(page));
}

function initCurrentPage (dispatch: any) {
    const page = cloneDeep(getCurrentPage());
    dispatch(setCurrentPage({ ...page, ...initState }));
}

function exchangeDistributeTable (distributeTable: any) {
    if (!distributeTable) {
        return [];
    }
    const KeyAndValue = Object.entries(distributeTable);
    return KeyAndValue.map(([name, tables]) => {
        return {
            name,
            tables,
            isSaved: true
        }
    })
}

export const actions: any = {
    navtoStep (step: any) {
        return (dispatch: any) => {
            setCurrentPageValue(dispatch, 'currentStep', step)
        }
    },
    getDataSource () {
        return (dispatch: any) => {
            ajax.getStreamDataSourceList()
                .then((res: any) => {
                    let data: any = []
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
    initCollectionTask (taskId: any) {
        return (dispatch: any) => {
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
            }).then((res: any) => {
                if (res.data) {
                    const { sourceMap } = res.data;
                    sourceMap.pavingData = !!sourceMap.pavingData;
                    if (sourceMap.journalName) {
                        sourceMap.collectType = collectType.FILE;
                    } else if (sourceMap.timestamp) {
                        sourceMap.collectType = collectType.TIME;
                    } else {
                        sourceMap.collectType = collectType.ALL;
                    }
                    if (sourceMap.distributeTable) {
                        sourceMap.distributeTable = exchangeDistributeTable(sourceMap.distributeTable);
                        sourceMap.multipleTable = true;
                    }
                    dispatch(actions.updateSourceMap(res.data.sourceMap, false, true));
                    dispatch(actions.updateTargetMap(res.data.targetMap, false, true));
                    dispatch(actions.updateChannelControlMap(res.data.setting, false, true));
                    setCurrentPageValue(dispatch, 'currentStep', 3);
                } else {
                    setCurrentPageValue(dispatch, 'currentStep', 0);
                }
            })
        }
    },

    updateSourceMap (params: any = {}, clear: any, notDirty: any) {
        return (dispatch: any) => {
            const page = getCurrentPage();
            let { sourceMap = {} } = page;
            if (clear) {
                sourceMap = {
                    ...initState.sourceMap,
                    type: sourceMap.type
                };
                setCurrentPageValue(dispatch, 'targetMap', cloneDeep(initState.targetMap));
            }
            if (params.distributeTable) {
                let tables = params.distributeTable.reduce((prevValue: any, item: any) => {
                    return prevValue.concat(item.tables);
                }, []);
                params.table = tables;
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

    updateTargetMap (params = {}, clear: any, notDirty: boolean) {
        return (dispatch: any) => {
            const page = getCurrentPage();
            let { targetMap = {} } = page;
            if (clear) {
                targetMap = initState.targetMap;
            }
            setCurrentPageValue(dispatch, 'targetMap',
                cloneDeep({
                    ...targetMap,
                    ...params
                }),
                notDirty ? undefined : true
            )
        }
    },

    updateChannelControlMap (params: SettingMap = {}, clear: any, notDirty: any) {
        return (dispatch: any) => {
            const page = getCurrentPage();
            let { settingMap = {} } = page;
            if (clear) {
                settingMap = initState.settingMap;
            }
            if (params.isSaveDirty == false) {
                params.sourceId = undefined;
                params.tableName = undefined;
                params.lifeDay = undefined;
            } else if (params.isSaveDirty) {
                params.lifeDay = 90;
            }
            setCurrentPageValue(dispatch, 'settingMap',
                cloneDeep({
                    ...settingMap,
                    ...params
                }),
                !notDirty
            )
        }
    }
}
