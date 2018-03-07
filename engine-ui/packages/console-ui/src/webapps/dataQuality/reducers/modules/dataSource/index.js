import { dataSourceActions as ACTION_TYPE } from '../../../consts/dataSourceActions';
import { cloneDeep } from 'lodash';

const initialState = {
    sourceList: [],
    sourceTable: [],
    sourceColumn: [],
    sourcePart: [],
    sourcePreview: {}
}

export default function dataSource(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  
        case ACTION_TYPE.GET_DATA_SOURCES_TYPE: {
            const clone = cloneDeep(state);
            const { sourceList } = clone;
            clone.sourceList = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_TABLE: {
            const clone = cloneDeep(state);
            const { sourceTable } = clone;
            clone.sourceTable = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_COLUMN: {
            const clone = cloneDeep(state);
            const { sourceColumn } = clone;
            clone.sourceColumn = payload;
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_PART: {
            const clone = cloneDeep(state);
            const { sourcePart } = clone;
            clone.sourcePart = [payload];
            return clone;
        }

        case ACTION_TYPE.GET_DATA_SOURCES_PREVIEW: {
            const clone = cloneDeep(state);
            const { sourcePreview } = clone;
            clone.sourcePreview = payload;
            return clone;
        }

        default:
            return state;
    }
}