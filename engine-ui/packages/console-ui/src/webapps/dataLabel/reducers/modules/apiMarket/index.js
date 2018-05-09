import { apiMarketActionType } from '../../../consts/apiMarketActionType';
import { cloneDeep } from 'lodash';

const initialState = {
   
    apiCatalogue: [],
    apiList:[],
    api:{

    },
    apiCallInfo:{

    }

}

export default function apiMarket(state = initialState, action) {
    const { type, payload } = action;
    switch (type) {  
        case apiMarketActionType.GET_CATALOGUE: {
            const clone = cloneDeep(state);
            clone.apiCatalogue=payload
            return clone;
        }
        case apiMarketActionType.GET_API_MARKET_LIST:{
            const clone = cloneDeep(state);
            clone.apiList=payload
            return clone;
        }
        case apiMarketActionType.GET_MARKET_API_DETAIL:{
            const clone=cloneDeep(state);
            clone.api[payload.tagId]=payload;
            return clone
        }
        case apiMarketActionType.GET_API_EXT_INFO:{
            const clone=cloneDeep(state);
            clone.apiCallInfo[payload.tagId]=payload;
            return clone
        }


        default:
            return state;
    }
}