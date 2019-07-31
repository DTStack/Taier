export function getApiMarketValue (key: any, apiMarket: any, apiId: any) {
    const api = apiMarket && apiMarket.api && apiMarket.api[apiId];

    if (api) {
        return api[key];
    } else {
        return null;
    }
}
