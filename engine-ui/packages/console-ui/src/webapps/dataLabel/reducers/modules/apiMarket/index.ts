import { apiMarketActionType } from '../../../consts/apiMarketActionType';
import { cloneDeep } from 'lodash';

const initialState: any = {

    apiCatalogue: [],
    apiList: [],
    api: {

    },
    apiCallInfo: {

    }

}

export default function apiMarket (state = initialState, action: any) {
    const { type, payload } = action;
    switch (type) {
        case apiMarketActionType.GET_CATALOGUE: {
            const clone = cloneDeep(state);
            clone.apiCatalogue = payload
            return clone;
        }
        case apiMarketActionType.GET_API_MARKET_LIST: {
            const clone = cloneDeep(state);
            clone.apiList = payload
            return clone;
        }
        case apiMarketActionType.GET_MARKET_API_DETAIL: {
            const clone = cloneDeep(state);
            clone.api[payload.apiId] = payload;
            return clone
        }
        case apiMarketActionType.GET_API_EXT_INFO: {
            const clone = cloneDeep(state);
            clone.apiCallInfo[payload.apiId] = payload;
            return clone
        }

        default:
            return state;
    }
}
