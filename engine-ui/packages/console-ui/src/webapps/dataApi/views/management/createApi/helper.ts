export function parseParamsPath (text: any) {
    text = text || '';
    const rexgep = new RegExp(/\{([-\w]+)\}/g);
    return (text.match(rexgep) || []).map((item: any) => {
        return item.replace(/(\{|\})/g, '');
    }) || [];
}
