import { dataCheckActions as ACTION_TYPE } from '../../../consts/dataCheckActions';
import { cloneDeep } from 'lodash';

const initialState = {
    loading: false,
    lists: [],
}

export default function dataCheck(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  
        case ACTION_TYPE.CHANGE_LOADING: {
            const clone = cloneDeep(state);
            const { loading } = clone;
            clone.loading = !loading;
            return clone;
        }

        case ACTION_TYPE.GET_LIST: {
            const clone = cloneDeep(state);
            const { lists } = clone;
            clone.lists = payload;
            return clone;
        }

        default:
            return state;
    }
}