import { dataSourceActionType } from '../../../consts/dataSourceActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    sourceQuery: [],
    sourceType: [],
    sourceList: [],
    tagSourceList: [],
    sourceTable: [],
    sourceColumn: [],
    sourcePart: [],
    sourcePreview: {}
}

export default function dataSource (state = initialState, action) {
    const { type, payload } = action;
    switch (type) {
        case dataSourceActionType.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES: {
            const clone = cloneDeep(state);
            clone.sourceQuery = payload;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_TYPE: {
            const clone = cloneDeep(state);
            clone.sourceType = payload;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_LIST: {
            const clone = cloneDeep(state);
            clone.sourceList = payload;
            return clone;
        }

        case dataSourceActionType.GET_TAG_DATA_SOURCES_LIST: {
            const clone = cloneDeep(state);
            clone.tagSourceList = payload;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_TABLE: {
            const clone = cloneDeep(state);
            clone.sourceTable = payload;
            return clone;
        }

        case dataSourceActionType.RESET_DATA_SOURCES_TABLE: {
            const clone = cloneDeep(state);
            clone.sourceTable = [];
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_COLUMN: {
            const clone = cloneDeep(state);
            clone.sourceColumn = payload;
            return clone;
        }

        case dataSourceActionType.RESET_DATA_SOURCES_COLUMN: {
            const clone = cloneDeep(state);
            clone.sourceColumn = [];
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_PREVIEW: {
            const clone = cloneDeep(state);
            clone.sourcePreview = payload;
            return clone;
        }

        default:
            return state;
    }
}
