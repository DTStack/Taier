import { combineReducers } from 'redux';
import { cloneDeep } from 'lodash';

import { dataSourceListAction, collectionAction } from './actionTypes'


// 缓存数据源列表
const dataSourceList = (state = [], action) => {
    switch (action.type) {
        case dataSourceListAction.LOAD_DATASOURCE: {
            const dataSource = action.payload;
            return dataSource;
        }

        case dataSourceListAction.RESET_DATASOURCE:
            return [];

        default: return state;
    }
};

const currentStep = (state = { step: 0 }, action) => {// 缓存数据同步当前操作界面
    switch (action.type) {
        case collectionAction.INIT_CURRENT_STEP: {
            const clone = cloneDeep(state);

            clone.step = 0;
            return clone;
        }

        case collectionAction.SET_CURRENT_STEP: {
            const clone = cloneDeep(state);

            clone.step = action.payload;
            return clone;
        }

        case collectionAction.GET_DATASYNC_SAVED: {
            const { currentStep } = action.payload;
            return currentStep;
        }

        default: return state;
    }
}

export const collectionReducer = combineReducers({
    dataSourceList,
    currentStep,
});
