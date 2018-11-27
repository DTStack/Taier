export function createLinkMark (attrs) {
    return `#link#${JSON.stringify(attrs)}#link#`
}
export function getLinkMark (value) {
    const linkRegexp = /#link#(.+)#link#/g;
    let result = [];
    let indexObj;
    indexObj = linkRegexp.exec(value);

    while (indexObj) {
        const node = document.createElement('a');
        const attrs = JSON.parse(indexObj[1]);
        const keyAndValues = Object.entries(attrs);
        for (let [_key, _value] of keyAndValues) {
            node.setAttribute(_key, _value)
        }
        node.className = 'editor_custom_link'
        node.innerHTML = 'logDownload';
        result.push({
            start: indexObj.index,
            end: indexObj.index + indexObj[0].length,
            node: node
        })
        indexObj = linkRegexp.exec(value);
    }
    return result;
}
