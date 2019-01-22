export function parseParamsPath (text) {
    text = text || '';
    const rexgep = new RegExp(/\{([-\w]+)\}/g);
    return (text.match(rexgep) || []).map((item) => {
        return item.replace(/(\{|\})/g, '');
    }) || [];
}
