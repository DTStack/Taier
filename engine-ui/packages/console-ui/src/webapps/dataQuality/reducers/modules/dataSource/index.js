import { dataSourceActionType } from '../../../consts/dataSourceActionType';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    tableLoading: false,
    sourceQuery: [],
    sourceType: [],
    sourceList: [],
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

        case dataSourceActionType.CHANGE_GETTABLE_LOADING: {
            const clone = cloneDeep(state);
            const { tableLoading } = clone;
            clone.tableLoading = !tableLoading;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES: {
            const clone = cloneDeep(state);
            const { sourceQuery } = clone;
            clone.sourceQuery = payload;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_TYPE: {
            const clone = cloneDeep(state);
            const { sourceType } = clone;
            clone.sourceType = payload;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_LIST: {
            const clone = cloneDeep(state);
            const { sourceList } = clone;
            clone.sourceList = payload;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_TABLE: {
            const clone = cloneDeep(state);
            const { sourceTable } = clone;
            clone.sourceTable = payload;
            return clone;
        }

        case dataSourceActionType.RESET_DATA_SOURCES_TABLE: {
            const clone = cloneDeep(state);
            const { sourceTable } = clone;
            clone.sourceTable = [];
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_COLUMN: {
            const clone = cloneDeep(state);
            const { sourceColumn } = clone;
            clone.sourceColumn = payload;
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_PART: {
            const clone = cloneDeep(state);
            const { sourcePart } = clone;
            clone.sourcePart = payload;
            return clone;
        }

        case dataSourceActionType.RESET_DATA_SOURCES_PART: {
            const clone = cloneDeep(state);
            const { sourcePart } = clone;
            clone.sourcePart = [];
            return clone;
        }

        case dataSourceActionType.GET_DATA_SOURCES_PREVIEW: {
            const clone = cloneDeep(state);
            const { sourcePreview } = clone;
            clone.sourcePreview = payload;
            return clone;
        }

        default:
            return state;
    }
}
