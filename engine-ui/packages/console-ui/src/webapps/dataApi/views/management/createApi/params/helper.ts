const DELIMITER = '~'
/**
 * 生成一个formItem的唯一key
 * @param {*} itemName formItem名字
 * @param {*} id id
 * @param {*} type 输入输出参数
 */
export function generateFormItemKey (itemName: any, id: any, type: any) {
    return `${itemName}${DELIMITER}${id}${DELIMITER}${type}`
}
/**
 * 解构key
 * @param {*} key formItemKey
 */
export function resolveFormItemKey (key: any) {
    let item = key.split(DELIMITER);
    if (!item || item.length != 3) {
        return null;
    }
    return {
        name: item[0],
        id: item[1],
        type: item[2]
    }
}
