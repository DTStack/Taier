import { combineReducers } from 'redux';
import assign from 'object-assign';
import { cloneDeep, isEqual } from 'lodash';
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
import { isRDB } from '../../../comm';

// 缓存数据源列表
const tabId = (state = {}, action) => {
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
const dataSourceList = (state = [], action) => {
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

const sourceMap = (state = {}, action) => {
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
            (source) => {
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
        const { sourceId, splitPK, src, table, extTable = {} } = action.payload;
        if (!src) return state;

        const { type } = src;
        const key = action.key;
        const clone = cloneDeep(state);

        clone.sourceId = +sourceId;
        clone.name = src.dataName;

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
            clone.column[colData.index] = colData.value
        }
        return clone;
    }

    // 数据源 添加一行字段
    case sourceMapAction.ADD_SOURCE_KEYROW: {
        const colData = action.payload;
        const clone = cloneDeep(state);
        let column;

        if (clone.column) {
            let name = '索引值'
            if (utils.checkExist(colData.index)) {
                column = clone.column.find(o => o.index == colData.index);
            } else if (utils.checkExist(colData.key)) {
                name = '字段名'
                column = clone.column.find(o => o.key == colData.key);
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
                const originArr = [...clone.column]
                for (let i = 0; i < colData.length; i++) {
                    const col = colData[i]
                    let findOut = null;
                    if (col.index !== undefined) {
                        findOut = clone.column.find(c => c.index == col.index)
                    } else if (col.key !== undefined) {
                        findOut = clone.column.find(c => c.key == col.key)
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

const targetMap = (state = {}, action) => {
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

    case targetMapAction.DATA_TARGETMAP_CHANGE: {
        const {
            sourceId, preSql, src,
            table, postSql, writeMode,
            rowkey
        } = action.payload;
        const clone = cloneDeep(state);

        if (sourceId) clone.sourceId = sourceId;
        if (rowkey) clone.type.rowkey = rowkey;

        if (src) {
            const { type } = src;
            clone.name = src.dataName;

            // 在赋值给Type前，删除无用的字段
            delete action.payload.sourceId;
            delete action.payload.src;

            clone.type = assign(action.payload, { type });
        }

        return clone;
    }

    case targetMapAction.TARGET_TABLE_COLUMN_CHANGE: {
        const colData = action.payload;

        return assign({}, state, {
            column: colData
        });
    }

    // target keyrow 添加一行字段
    case targetMapAction.ADD_TARGET_KEYROW: {
        const colData = action.payload;
        const clone = cloneDeep(state);
        let column;

        if (clone.column) {
            column = clone.column.find(o => o.key == colData.key);
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
                const originArr = [...clone.column]
                for (let i = 0; i < colData.length; i++) {
                    const col = colData[i]
                    let findOut = clone.column.find(c => c.key == col.key);
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

const keymap = (state = { source: [], target: [] }, action) => {
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

        const checkExist = (arr, item) => {
            let bl = false;

            for (let o of arr) {
                if (isEqual(o, item)) {
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
        const newSource = source.filter(key_obj => !isEqual(key_obj, mapSource));
        const newTarget = target.filter(key_obj => !isEqual(key_obj, mapTarget));

        clone.source = newSource;
        clone.target = newTarget;

        return clone;
    }

    case keyMapAction.SET_ROW_MAP: {
        const { targetCol, sourceCol } = action.payload;
        let source = []; let target = [];

        sourceCol.forEach((o, i) => {
            if (targetCol[i]) {
                source.push(o);
                target.push(targetCol[i]);
            }
        });

        return { source, target };
    }

    case keyMapAction.SET_NAME_MAP: {
        let { targetCol, sourceCol, sourceSrcType, targetSrcType } = action.payload;
        let source = []; let target = [];

        let targetNameCol = targetCol.map(o => o.key);

        sourceCol.forEach((o, i) => {
            let name = o.key;
            let idx = targetNameCol.indexOf(name);

            if (idx !== -1) {
                // const sourceName = isRDB(sourceSrcType) ? o.key : o;
                // const targetName = isRDB(targetSrcType) ? name : targetCol[idx]
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
            const index = clone.source.findIndex((item) => isEqual(item, old))
            if (index > 0) {
                clone.source[index] = replace;
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
            const index = clone.target.findIndex((item) => isEqual(item, old))
            if (index > 0) {
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
            const index = clone.source.findIndex((item) => isEqual(item, source))
            if (index > -1) {
                clone.source.splice(index, 1)
                clone.target.splice(index, 1)
                return clone;
            }
        } else if (target) {
            const index = clone.target.findIndex((item) => isEqual(item, target))
            console.log('removeKeyMap:', target, index)
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

const setting = (state = { speed: 1, channel: 1, record: 100, isSaveDirty: false }, action) => {
    switch (action.type) {
    case dataSyncAction.INIT_JOBDATA: {
        if (action.payload === null) return { speed: 1, channel: 1, record: 100 };
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

const currentStep = (state = { step: 0 }, action) => { // 缓存数据同步当前操作界面
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
