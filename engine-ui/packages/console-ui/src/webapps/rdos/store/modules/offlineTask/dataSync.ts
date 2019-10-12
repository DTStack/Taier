import { combineReducers } from 'redux';
import assign from 'object-assign';
import { cloneDeep, isObject } from 'lodash';
import { message } from 'antd';

import utils from 'utils';

import {
    dataSourceListAction,
    sourceMapAction,
    targetMapAction,
    keyMapAction,
    settingAction,
    dataSyncAction
} from './actionType';

import { RDB_TYPE_ARRAY } from '../../../comm/const';

function isFieldMatch (source: any, target: any) {
    if (isObject(source as any) && isObject(target as any)) {
        const sourceField = source.key || source.index;
        const tagetField = target.key || target.index;
        return sourceField === tagetField;
    } else if (isObject(source as any) && !isObject(target as any)) {
        const sourceVal = source.key || source.index
        return sourceVal === target
    } else if (!isObject(source as any) && isObject(target as any)) {
        const targetVal = target.key || target.index
        return source === targetVal
    } else {
        return source === target
    }
}

// 缓存数据源列表
const tabId = (state: any = {}, action: any) => {
    switch (action.type) {
        case dataSyncAction.SET_TABID: {
            const tabId = action.payload;
            return tabId;
        }

        case dataSyncAction.RESET_TABID:
            return {};

        case dataSyncAction.GET_DATASYNC_SAVED: {
            const { tabId } = action.payload;
            return tabId;
        }

        default: return state;
    }
};

// 缓存数据源列表
const dataSourceList = (state: any = [], action: any) => {
    switch (action.type) {
        case dataSourceListAction.LOAD_DATASOURCE: {
            const dataSource = action.payload;
            return dataSource;
        }

        case dataSourceListAction.RESET_DATASOURCE:
            return [];

        case dataSyncAction.GET_DATASYNC_SAVED: {
            const { dataSourceList } = action.payload;
            return dataSourceList;
        }

        default: return state;
    }
};

export const sourceMap = (state: any = {}, action: any) => {
    switch (action.type) {
        case dataSyncAction.INIT_JOBDATA: {
            if (action.payload === null) return {};

            const { sourceMap } = action.payload;
            return sourceMap;
        }

        case dataSyncAction.GET_DATASYNC_SAVED: {
            const { sourceMap } = action.payload;
            return sourceMap;
        }

        case dataSyncAction.RESET_SOURCE_MAP: {
            return {};
        }

        case sourceMapAction.DATA_SOURCE_ADD: {
            const key = action.key;
            const clone = cloneDeep(state);
            clone.sourceList.push({
                name: null,
                sourceId: null,
                type: null,
                tables: [],
                key: key
            })
            return clone;
        }

        case sourceMapAction.DATA_SOURCE_DELETE: {
            const key = action.key;
            const clone = cloneDeep(state);
            clone.sourceList = clone.sourceList.filter(
                (source: any) => {
                    return source.key != key
                }
            )
            return clone;
        }

        case sourceMapAction.DATA_SOURCE_CHANGE: {
            const { type, id, dataName } = action.payload;
            const key = action.key;
            const clone = cloneDeep(state);
            if (typeof key != 'undefined') {
                for (let i in clone.sourceList) {
                    let source = clone.sourceList[i];
                    if (source.key == key) {
                        clone.sourceList[i] = {
                            name: dataName,
                            sourceId: id,
                            type: type,
                            tables: [],
                            key: key
                        }
                    }
                }
            } else {
                clone.sourceId = id;
                clone.name = dataName;
                clone.type = { type };
                clone.sourceList = [{
                    name: dataName,
                    sourceId: id,
                    type: type,
                    tables: [],
                    key: 'main'
                }]
            }
            if (type === 6) clone.column = [];

            return clone;
        }

        case sourceMapAction.DATA_SOURCEMAP_CHANGE: {
            const { sourceId, splitPK, src, table, extralConfig, extTable = {} } = action.payload;
            if (!src) return state;
            const { type } = src;
            const key = action.key;
            const clone = cloneDeep(state);

            clone.sourceId = +sourceId;
            clone.name = src.dataName;
            if (typeof extralConfig != 'undefined') {
                clone.extralConfig = extralConfig;
            }

            if (RDB_TYPE_ARRAY.indexOf(+type) !== -1) {
                clone.splitPK = splitPK;
                delete action.payload.splitPK;
            }

            delete action.payload.sourceId;
            clone.type = assign({}, action.payload, { type, splitPK });

            if (typeof key != 'undefined') {
                for (let i in clone.sourceList) {
                    let source = clone.sourceList[i];
                    if (key == source.key) {
                        if (key == 'main') {
                            source.tables = table
                        } else {
                            source.tables = extTable[key]
                        }
                    }
                }
            }

            return clone;
        }

        case sourceMapAction.DATA_SOURCEMAP_UPDATE: {
            return assign({}, state, action.payload);
        }

        case sourceMapAction.SOURCE_TABLE_COLUMN_CHANGE: {
            const colData = action.payload;

            return assign({}, state, {
                column: colData
            });
        }
        case sourceMapAction.SOURCE_TABLE_COPATE_CHANGE: {
            const copateData = action.payload;

            return assign({}, state, {
                copate: copateData
            });
        }

        // 编辑源字段
        case sourceMapAction.EDIT_SOURCE_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            if (colData) {
                clone.column[colData.index] = assign({}, clone.column[colData.index], colData.value)
            }
            return clone;
        }

        // 数据源 添加一行字段
        case sourceMapAction.ADD_SOURCE_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            let column: any;

            if (clone.column) {
                let name = '索引值'
                if (utils.checkExist(colData.index)) {
                    column = clone.column.find((o: any) => o.index == colData.index);
                } else if (utils.checkExist(colData.key)) {
                    name = '字段名'
                    column = clone.column.find((o: any) => o.key == colData.key);
                }
                if (utils.checkExist(column)) {
                    message.error(`添加失败：${name}不能重复`);
                } else {
                    clone.column = [...clone.column, colData]
                }
            } else {
                clone.column = [colData];
            }
            return clone;
        }

        case sourceMapAction.REPLACE_BATCH_SOURCE_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            if (colData && colData.length > 0) {
                clone.column = colData;
            }
            return clone;
        }

        case sourceMapAction.ADD_BATCH_SOURCE_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            if (colData && colData.length > 0) {
            // 不存在直接赋值
                if (!clone.column || clone.column.length === 0) {
                    clone.column = colData;
                } else { // 否则数据合并
                    const originArr: any = [...clone.column]
                    for (let i = 0; i < colData.length; i++) {
                        const col = colData[i]
                        let findOut = null;
                        if (col.index !== undefined) {
                            findOut = clone.column.find((c: any) => c.index == col.index)
                        } else if (col.key !== undefined) {
                            findOut = clone.column.find((c: any) => c.key == col.key)
                        }

                        if (!findOut) {
                            originArr.push(col)
                        }
                    }
                    clone.column = originArr;
                }
            }
            return clone;
        }

        // hdfs字段移除行
        case sourceMapAction.REMOVE_SOURCE_KEYROW: {
            const index = action.payload;
            const clone = cloneDeep(state);
            if (clone.column && clone.column.length > 0) {
                clone.column.splice(index, 1)
            }
            return clone;
        }

        default: return state;
    }
};

export const targetMap = (state: any = {}, action: any) => {
    switch (action.type) {
        case dataSyncAction.INIT_JOBDATA: {
            if (action.payload === null) return {};
            const { targetMap } = action.payload;
            return targetMap;
        }

        case dataSyncAction.RESET_TARGET_MAP: {
            return {};
        }

        case dataSyncAction.GET_DATASYNC_SAVED: {
            const { targetMap } = action.payload;
            return targetMap;
        }

        case targetMapAction.DATA_SOURCE_TARGET_CHANGE: {
            const { type, id, dataName } = action.payload;
            const clone = cloneDeep(state);

            clone.sourceId = id;
            clone.name = dataName;
            clone.type = { type };

            if (type === 6) clone.column = [];

            return clone;
        }

        /**
         * TODO 该方法现在的扩展性极差，传入的字段需要额外的转换，需要重构
         */
        case targetMapAction.DATA_TARGETMAP_CHANGE: {
            const {
                sourceId, src, rowkey, extralConfig //, havePartition
            } = action.payload;
            const clone = cloneDeep(state);

            if (sourceId) clone.sourceId = sourceId;
            if (rowkey) clone.type.rowkey = rowkey;
            if (typeof extralConfig != 'undefined') {
                clone.extralConfig = extralConfig;
            }

            // if (havePartition !== undefined) clone.type.havePartition = havePartition;

            const typeValues = cloneDeep(action.payload);
            // 在赋值给Type前，删除无用的字段
            delete typeValues.sourceId;
            delete typeValues.src;
            clone.type = assign(clone.type, typeValues);

            if (src) {
                if (src.dataName) clone.name = src.dataName;
                if (src.type) clone.type = assign(clone.type, src.type);
            }

            return clone;
        }

        case targetMapAction.TARGET_TABLE_COLUMN_CHANGE: {
            const colData = action.payload;

            return assign({}, state, {
                column: colData
            });
        }

        case targetMapAction.CHANGE_NATIVE_HIVE: {
            const isNativeHive = action.payload;

            return assign({}, state, {
                isNativeHive: isNativeHive
            });
        }

        // target keyrow 添加一行字段
        case targetMapAction.ADD_TARGET_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            let column: any;

            if (clone.column) {
                column = clone.column.find((o: any) => o.key == colData.key);
                if (column) {
                    message.error('添加失败：字段名不能重复');
                } else {
                    clone.column = [...clone.column, colData]
                }
            } else {
                clone.column = [colData];
            }

            return clone;
        }

        // 替换所有的字段
        case targetMapAction.REPLACE_BATCH_TARGET_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            if (colData && colData.length > 0) {
                clone.column = colData;
            }
            return clone;
        }

        // HDFS批量添加字段
        case targetMapAction.ADD_BATCH_TARGET_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            if (colData && colData.length > 0) {
            // 不存在直接赋值
                if (!clone.column || clone.column.length === 0) {
                    clone.column = colData;
                } else { // 否则数据合并
                    const originArr: any = [...clone.column]
                    for (let i = 0; i < colData.length; i++) {
                        const col = colData[i]
                        let findOut = clone.column.find((c: any) => c.key == col.key);
                        if (!findOut) {
                            originArr.push(col)
                        }
                    }
                    clone.column = originArr;
                }
            }
            return clone;
        }

        // 编辑HDFS目标字段
        case targetMapAction.EDIT_TARGET_KEYROW: {
            const colData = action.payload;
            const clone = cloneDeep(state);
            if (colData) {
                clone.column[colData.index] = colData.value
            }
            return clone;
        }

        // hdfs字段移除行
        case targetMapAction.REMOVE_TARGET_KEYROW: {
            const index = action.payload;
            const clone = cloneDeep(state);
            if (clone.column && clone.column.length > 0) {
                clone.column.splice(index, 1)
            }
            return clone;
        }

        default: return state;
    }
};

export const keymap = (state: any = { source: [], target: [] }, action: any) => {
    switch (action.type) {
        case dataSyncAction.INIT_JOBDATA: {
            if (action.payload === null) return { source: [], target: [] };
            const { keymap } = action.payload;
            return keymap;
        }

        case dataSyncAction.RESET_KEYMAP: {
            return { source: [], target: [] };
        }

        case dataSyncAction.GET_DATASYNC_SAVED: {
            const { keymap } = action.payload;
            return keymap;
        }

        case keyMapAction.ADD_LINKED_KEYS: {
            const map = action.payload;
            const clone = cloneDeep(state);
            const { source, target } = clone;

            const checkExist = (arr: any, item: any) => {
                let bl = false;

                for (let o of arr) {
                    if (isFieldMatch(o, item)) {
                        bl = true;
                        break;
                    }
                }

                return bl;
            }

            if (checkExist(source, map.source)) {
                return state;
            } else if (checkExist(target, map.target)) {
                return state;
            } else {
                clone.source = [...clone.source, map.source];
                clone.target = [...clone.target, map.target];
            }

            return clone;
        }

        case keyMapAction.DEL_LINKED_KEYS: {
            const map = action.payload;
            const clone = cloneDeep(state);
            const { source, target } = clone;
            const mapSource = map.source;
            const mapTarget = map.target;
            const newSource = source.filter((keyObj: any) => !isFieldMatch(keyObj, mapSource));
            const newTarget = target.filter((keyObj: any) => !isFieldMatch(keyObj, mapTarget));

            clone.source = newSource;
            clone.target = newTarget;

            return clone;
        }

        case keyMapAction.SET_ROW_MAP: {
            const { targetCol, sourceCol } = action.payload;
            let source: any = []; let target: any = [];

            sourceCol.forEach((o: any, i: any) => {
                if (targetCol[i]) {
                    source.push(o);
                    target.push(targetCol[i]);
                }
            });

            return { source, target };
        }

        case keyMapAction.SET_NAME_MAP: {
            let { targetCol, sourceCol } = action.payload;
            let source: any = []; let target: any = [];

            sourceCol.forEach((o: any, i: any) => {
                let name = o.key.toUpperCase();
                let idx = targetCol.findIndex((o: any) => {
                    const sourceName = o.key.toUpperCase();
                    return sourceName === name;
                })
                if (idx !== -1) {
                    source.push(o);
                    target.push(targetCol[idx]);
                }
            });

            return { source, target };
        }

        case keyMapAction.EDIT_KEYMAP_SOURCE: {
            const map = action.payload;
            const { old, replace } = map
            const clone = cloneDeep(state);
            if (map) {
                const index = clone.source.findIndex((item: any) => isFieldMatch(item, old))
                if (index > -1) {
                    clone.source[index] = assign({}, clone.source[index], replace);
                    return clone;
                }
            }
            return state;
        }

        case keyMapAction.EDIT_KEYMAP_TARGET: {
            const map = action.payload;
            const { old, replace } = map
            const clone = cloneDeep(state);
            if (map) {
                const index = clone.target.findIndex((item: any) => isFieldMatch(item, old))
                if (index > -1) {
                    clone.target[index] = replace;
                    return clone;
                }
            }
            return state;
        }

        // 移除
        case keyMapAction.REMOVE_KEYMAP: {
            const map = action.payload;
            const { source, target } = map
            const clone = cloneDeep(state);
            if (source) {
                const index = clone.source.findIndex((item: any) => isFieldMatch(item, source))
                if (index > -1) {
                    clone.source.splice(index, 1)
                    clone.target.splice(index, 1)
                    return clone;
                }
            } else if (target) {
                const index = clone.target.findIndex((item: any) => isFieldMatch(item, target))
                if (index > -1) {
                    clone.source.splice(index, 1)
                    clone.target.splice(index, 1)
                    return clone;
                }
            }
            return state;
        }

        case keyMapAction.RESET_LINKED_KEYS:
            return { source: [], target: [] };

        default: return state;
    }
};

const setting = (state: any = { speed: -1, channel: 1, record: 100, isSaveDirty: false }, action: any) => {
    switch (action.type) {
        case dataSyncAction.INIT_JOBDATA: {
            if (action.payload === null) return { speed: -1, channel: 1, record: 100 };
            const { setting } = action.payload;
            return setting;
        }

        case settingAction.CHANGE_CHANNEL_SETTING: {
            const setting = action.payload;
            return setting;
        }

        case settingAction.CHANGE_CHANNEL_FIELDS: {
            const newSetting = assign(state, action.payload)
            return newSetting;
        }

        case dataSyncAction.GET_DATASYNC_SAVED: {
            const { setting } = action.payload;
            return setting;
        }

        default: return state;
    }
};

const currentStep = (state: any = { step: 0 }, action: any) => { // 缓存数据同步当前操作界面
    switch (action.type) {
        case dataSyncAction.INIT_CURRENT_STEP: {
            const clone = cloneDeep(state);

            clone.step = 0;
            return clone;
        }

        case dataSyncAction.SET_CURRENT_STEP: {
            const clone = cloneDeep(state);

            clone.step = action.payload;
            return clone;
        }

        case dataSyncAction.GET_DATASYNC_SAVED: {
            const { currentStep } = action.payload;
            return currentStep;
        }

        default: return state;
    }
}

export const dataSyncReducer = combineReducers({
    tabId,
    dataSourceList,
    sourceMap,
    targetMap,
    keymap,
    setting,
    currentStep
});
