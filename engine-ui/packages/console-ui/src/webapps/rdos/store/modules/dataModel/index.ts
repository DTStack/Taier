import { combineReducers } from 'redux';
import { dataModelActions } from './actionTypes';

const subjectFields = function (state: any = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case dataModelActions.GET_SUBJECT_FIELDS: {
            return payload
        }
        default: {
            return state;
        }
    }
}

const modelLevels = function (state: any = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case dataModelActions.GET_MODEL_LEVELS: {
            return payload
        }
        default: {
            return state;
        }
    }
}

const incrementCounts = function (state: any = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case dataModelActions.GET_INCREMENT_COUNTS: {
            return payload
        }
        default: {
            return state;
        }
    }
}

const freshFrequencies = function (state: any = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case dataModelActions.GET_FRESH_FREQUENCIES: {
            return payload
        }
        default: {
            return state;
        }
    }
}

export const dataModel = combineReducers({
    modelLevels,
    subjectFields,
    incrementCounts,
    freshFrequencies
});
