import mc from 'mirror-creator';

export const apiMarketActionType = mc([
    'GET_CATALOGUE',
    'GET_API_MARKET_LIST',
    'GET_MARKET_API_DETAIL',
    'GET_API_EXT_INFO'
], { prefix: 'apiMarket/' })
