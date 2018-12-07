export function getApiMarketValue (key, apiMarket, apiId) {
    const api = apiMarket && apiMarket.api && apiMarket.api[apiId];

    if (api) {
        return api[key];
    } else {
        return null;
    }
}
