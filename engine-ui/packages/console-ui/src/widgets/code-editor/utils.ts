import moment from 'moment';
export function createLinkMark (attrs: any) {
    return `#link#${JSON.stringify(attrs)}#link#`
}
export function getLinkMark (value: any) {
    const linkRegexp = /#link#(.+)#link#/g;
    let result: any = [];
    let indexObj: any;
    indexObj = linkRegexp.exec(value);

    while (indexObj) {
        const node = document.createElement('a');
        const attrs = JSON.parse(indexObj[1]);
        const keyAndValues: any = Object.entries(attrs);
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
/**
 * 标记颜色
 * @param {string} text 标记文本
 * @param {string} type 标记类型
 */
export function createLogMark (text = '', type = 'info') {
    return `#log<${type}>log#${text}#log<${type}>log#`
}
export function getLogMark (value: any) {
    let result: any = [];
    function match () {
        const logRegexp = new RegExp(`#log<(\\w*)>log#((.|\r\n|\n)*?)#log<(\\w*)>log#`, 'g');
        let indexObj: any;
        indexObj = logRegexp.exec(value);
        /**
         * 循环正则来匹配相应格式字段
         */
        while (indexObj) {
            let text = indexObj[0].replace(/\r\n/g, '\n');
            /**
             * 包含标记字段的数组
             */
            let textArr = text.split('\n');
            let content = indexObj[2].replace(/\r\n/g, '\n');
            /**
             * 不包含标记字段的数组
             */
            let contentArr = content.split('\n');

            /**
             * 创建一个node，便于拷贝
             */
            const node = document.createElement('span');
            let type = indexObj[1];
            node.className = `c-editor--log__${type}`
            /**
             * 当前index偏移量
             */
            let offset = 0;

            for (let i = 0; i < textArr.length; i++) {
                let textItem = textArr[i];
                let contentItem = contentArr[i];
                let cloneNode: any = node.cloneNode(false);// 浅拷贝
                cloneNode.innerText = contentItem;
                result.push({
                    start: indexObj.index + offset,
                    end: indexObj.index + offset + textItem.length,
                    node: cloneNode
                })
                offset = offset + textItem.length + 1;
            }
            indexObj = logRegexp.exec(value);
        }
    }
    match();
    return result;
}
/**
 * dtlog日志构造器
 * @param {string} log 日志内容
 * @param {string} type 日志类型
 */
export function createLog (log: string, type = '') {
    let now = moment().format('HH:mm:ss');
    if (process.env.NODE_ENV == 'test') {
        now = 'test'
    }
    return `[${now}] <${type}> ${log}`
}
export function createTitle (title = '') {
    const baseLength = 15;
    const offsetLength = Math.floor(1.5 * title.length / 2);
    let arr = new Array(Math.max(baseLength - offsetLength, 5));
    const wraptext = arr.join('=');
    return `${wraptext}${title}${wraptext}`
}
