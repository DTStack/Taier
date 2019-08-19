import { dataSourceActions as ACTION_TYPE } from '../../../consts/dataSourceActions';
import { cloneDeep } from 'lodash';

const initialState: any = {
    loading: false,
    sourceQuery: [],
    sourceType: [],
    sourceList: [],
    sourceTable: [],
    sourceColumn: [],
    sourcePart: [],
    sourcePreview: {}
}

export default function dataSource (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case ACTION_TYPE.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES: {
            const clone = cloneDeep(state);
            clone.sourceQuery = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_TYPE: {
            const clone = cloneDeep(state);
            clone.sourceType = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_LIST: {
            const clone = cloneDeep(state);
            clone.sourceList = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_TABLE: {
            const clone = cloneDeep(state);
            clone.sourceTable = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_COLUMN: {
            const clone = cloneDeep(state);
            clone.sourceColumn = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_PART: {
            const clone = cloneDeep(state);
            clone.sourcePart = payload.children;
            return clone;
        }

        case ACTION_TYPE.RESET_DATA_SOURCES_PART: {
            const clone = cloneDeep(state);
            clone.sourcePart = [];
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_PREVIEW: {
            const clone = cloneDeep(state);
            clone.sourcePreview = payload;
            return clone;
        }

        default:
            return state;
    }
}
