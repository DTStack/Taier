import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    topRecords: [],
    alarmTrend: [],
    alarmSum: {},
    usage: {}
}

export default function dashBoard(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  
        default:
            return state;
    }
}