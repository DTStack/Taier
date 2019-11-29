import { debounce, endsWith, cloneDeep, range } from 'lodash';
import { notification, Modal } from 'antd';
import { NotificationApi } from 'antd/lib/notification';
import React from 'react';
import { MY_APPS } from 'main/consts';
import { rdosApp, streamApp, scienceApp, tagApp } from 'config/base';
import { mergeDeep } from 'utils/merge';
import moment from 'moment';

declare var window: any;

/**
 * 存放一些零碎的公共方法
*/

/**
 * 延时隐藏 password 控件中的 value
 * 延时处理主要是为了绕过Ant Form 组件的对控件的值设置问题
 */
export function hidePasswordInDom () {
    setTimeout(() => {
        // 特殊处理密码在 dom 中的展示
        const pwd1 = document.querySelectorAll('input[type="password"]');
        pwd1.forEach(ele => {
            ele.setAttribute('value', '');
        });
    }, 100)
}

/**
 * 更新组件状态
 * @param thisRef 组件this引用
 * @param newState 待更新状态
 */
export function updateComponentState (thisRef: { state: object; setState: Function }, newState: object, callback?: Function): void {
    if (thisRef && thisRef.setState) {
        thisRef.setState(mergeDeep(thisRef.state, newState), callback);
    }
}

// 请求防抖动
export function debounceEventHander (func: any, wait?: number, options?: any) {
    const debounced = debounce(func, wait, options)
    return function (e: any) {
        e.persist()
        return debounced(e)
    }
}
/**
 * 展开JSON对象
 * 例如一个{a:{b: "c"}}, 转换为：{"a.b": "c"}
 * @param obj
 */
export function expandJSONObj (obj: any) {
    const res: any = {};
    const expand = (target: any, parentField?: string | number) => {
        for (let key in target) {
            const field = parentField ? `${parentField}.${key}` : `${key}`;
            if (!target[key]) {
                res[`${field}`] = target[key];
                continue;
            }
            const keys = Object.keys(target[key]);
            if (keys.length > 0 && typeof (target[key]) === 'object') {
                expand(target[key], field)
            } else {
                if (!res[field]) res[`${field}`] = target[key];
            }
        }
    }
    expand(obj);
    return res;
}

/**
 * 查找树中的某个节点
 */
export function findTreeNode (treeNode: any, node: any) {
    let result = ''
    if (treeNode.id === parseInt(node.id, 10)) {
        return treeNode;
    }
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            result = findTreeNode(children[i], node)
            if (result) return result
        }
    }
}

/**
 *
 * @param {Array} treeNodes 树状节点数据
 * @param {Object} target 目标节点
 */
export function removeTreeNode (treeNodes: any, target: any) {
    for (let i = 0; i < treeNodes.length; i += 1) {
        if (treeNodes[i].id === target.id && treeNodes[i].type == target.type) {
            treeNodes.splice(i, 1) // remove节点
            break;
        }
        if (treeNodes[i].children) {
            removeTreeNode(treeNodes[i].children, target)
        }
    }
}

/**
 * 追加子节点
 */
export function appendTreeNode (treeNode: any, append: any, target: any) {
    const targetId = parseInt(target.id, 10)
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            appendTreeNode(children[i], append, target)
        }
    }
    if (treeNode.id === targetId && treeNode.children && treeNode.type == target.type) {
        treeNode.children.push(append)
    }
}

/**
 * 遍历树形节点，用新节点替换老节点
*/
export function replaceTreeNode (treeNode: any, replace: any) {
    if (
        treeNode.id === parseInt(replace.id, 10) && treeNode.type == replace.type
    ) {
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

/**
 *
 * @param {*} origin
 * @param {*} mergeTo
 */
export function mergeTreeNodes (origin: any, target: any) {
    replaceTreeNode(origin, target);
    if (target.children) {
        const children = target.children
        for (let i = 0; i < children.length; i += 1) {
            mergeTreeNodes(origin, children[i])
        }
    }
}
/**
 * 先序遍历树
 */
export function visitTree (tree: any[], callback: (node: any, level: number) => void, subKey: string = 'subTaskVOS', level: number = 0) {
    if (!tree) {
        return;
    }
    for (let i = 0; i < tree.length; i++) {
        let node = tree[i];
        callback(node, level);
        visitTree(node[subKey], callback, subKey, level + 1);
    }
}
/**
 * 打开新窗口
 * @param {*} url
 * @param {*} target
 */
export function openNewWindow (url: any, target: any) {
    window.open(url, target || '_blank')
}

/**
 * 检验改应用是否包含项目选项
 * @param {s} app
 */
export function hasProject (app: any) {
    return app === MY_APPS.RDOS || app === MY_APPS.STREAM || app === MY_APPS.SCIENCE || app === MY_APPS.TAG
}

/**
 * 字符串替换
 */
export function replaceStrFormBeginToEnd (str: any, replaceStr: any, begin: any, end: any) {
    return str.substring(0, begin) + replaceStr + str.substring(end + 1)
}

/**
 * 字符串替换（根据索引数组）
 */
export function replaceStrFormIndexArr (str: any, replaceStr: any, indexArr: any) {
    let result = '';
    let index = 0;

    if (!indexArr || indexArr.length < 1) {
        return str;
    }
    for (let i = 0; i < indexArr.length; i++) {
        let indexItem = indexArr[i];
        let begin = indexItem.begin;

        result = result + str.substring(index, begin) + replaceStr;
        index = indexItem.end + 1;

        if (i == indexArr.length - 1) {
            result = result + str.substring(index);
        }
    }

    return result;
}

/**
 * 过滤sql中的注释
 * @param {s} app
 */
export function filterComments (sql: string) {
    interface FilterParser {
        index: number;
        queue: string;
        comments: {
            begin: number;
            end: number;
        }[];
    }
    // 处理引号
    function quoteToken (parser: FilterParser, sql: string): string {
        let queue = parser.queue;
        let endsWith = queue[queue.length - 1];
        if (endsWith == '\'' || endsWith == '"') {
            let nextToken = sql.indexOf(endsWith, parser.index + 1);
            if (nextToken != -1) {
                parser.index = nextToken;
                parser.queue = '';
            } else {
                parser.index = sql.length - 1;
                parser.queue = '';
            }
        } else {
            return null;
        }
    }
    // 处理单行注释
    function singleLineCommentToken (parser: FilterParser, sql: string): string {
        let queue = parser.queue;
        if (queue.endsWith('--')) {
            let nextToken = sql.indexOf('\n', parser.index + 1);
            let begin = parser.index - 1;
            if (nextToken != -1) {
                let end = nextToken - 1;
                parser.comments.push({
                    begin: begin,
                    end: end
                })
                parser.index = end;
                parser.queue = '';
            } else {
                parser.comments.push({
                    begin: begin,
                    end: sql.length - 1
                })
                parser.index = sql.length - 1;
                parser.queue = '';
            }
        } else {
            return null;
        }
    }
    // 处理多行注释
    function multipleLineCommentToken (parser: FilterParser, sql: string): string {
        let queue = parser.queue;
        if (queue.endsWith('/*')) {
            let nextToken = sql.indexOf('*/', parser.index + 1);
            if (nextToken != -1) {
                parser.comments.push({
                    begin: parser.index - 1,
                    end: nextToken + 1
                })
                parser.index = nextToken;
                parser.queue = '';
            } else {
                parser.index = sql.length - 1;
                parser.queue = '';
            }
        } else {
            return null;
        }
    }
    let parser: FilterParser = {
        index: 0,
        queue: '',
        comments: []
    };
    for (parser.index = 0; parser.index < sql.length; parser.index++) {
        let char = sql[parser.index];
        parser.queue += char;
        let tokenFuncs = [quoteToken, singleLineCommentToken, multipleLineCommentToken];
        for (let i = 0; i < tokenFuncs.length; i++) {
            let err = tokenFuncs[i](parser, sql);
            if (err) {
                console.log(err);
                return null;
            }
        }
    }
    sql = replaceStrFormIndexArr(sql, ' ', parser.comments)
    return sql;
}

/**
 * 分割sql
 * @param {String} sqlText
 */
export function splitSql (sqlText: string) {
    if (!sqlText) {
        return sqlText;
    }
    sqlText = sqlText.trim();
    if (!endsWith(sqlText, ';')) {
        sqlText += ';';
    }

    let results = [];
    let index = 0;
    let tmpChar = null;
    for (let i = 0; i < sqlText.length; i++) {
        let char = sqlText[i];

        if (char == "'" || char == '"') {
            if (tmpChar == char) {
                tmpChar = null;
            } else if (!tmpChar) {
                tmpChar = char;
            }
        } else if (char == ';') {
            if (tmpChar == null) {
                results.push(sqlText.substring(index, i));
                index = i + 1;
            }
        }
    }
    // 清空
    results.push(sqlText.substring(index, sqlText.length));

    return results.filter(Boolean);
}

export function getRandomInt (min: number, max: number) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min; // The maximum is exclusive and the minimum is inclusive
}

/**
 * 滚动元素到视窗范围内
 */
export function scrollToView (id: string) {
    const ele: any = document.getElementById(id);
    if (ele && ele.scrollIntoViewIfNeeded) {
        ele.scrollIntoViewIfNeeded()
    } else if (ele && ele.scrollIntoView) {
        ele.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'center' });
    }
}

/**
 * Promise超时应用
 * @param {} promise
 * @param {*} ms
 */
export function timeout (promise: any, ms: number) {
    return new Promise(function (resolve, reject) {
        setTimeout(function () {
            reject(new Error('timeout'))
        }, ms)
        promise.then(resolve, reject);
    })
}
/**
 * 初始化notification
 */
export function initNotification () {
    notification.config({
        duration: 5
    })
    const changeArr = ['error', 'success']
    const iconMap: any = {
        'error': <img src='public/main/img/icon/notification-error.svg' />,
        'success': <img src='public/main/img/icon/notification-success.svg' />
    }
    changeArr.forEach((key: keyof NotificationApi) => {
        const oldFunc: any = notification[key];
        notification[key] = function (config: any = {}) {
            const notifyMsgs = document.querySelectorAll('.ant-notification-notice-description');
            config = {
                ...config,
                icon: iconMap[key],
                className: 'dt-notification',
                message: <span>
                    {config.message}
                    {notifyMsgs.length ? null : (
                        <a onClick={() => {
                            notification.destroy();
                        }} className='dt-notification__close-btn'>全部关闭</a>
                    )}
                </span>
            }
            oldFunc.apply(notification, [config]);
        }
    })
}

let _singletonNotificationCursorTime = 0;
/**
 * 校验是否处在单实例的时间段
 */
function checkIsTimeout () {
    const offset = 1000;
    const now = new Date().getTime();
    const old = _singletonNotificationCursorTime;

    _singletonNotificationCursorTime = new Date().getTime();
    if (now - offset > old) {
        return true
    }
    return false;
}

/**
 * 包装一下
 */
export function dtNotification (title: any, message: any, type: any, config: any) {
    const showType: any = type || 'error';
    const WrapperModal: any = Modal;
    const showMessage = message.length > 100 ? (<span>
        {message.substring(0, 100)}... <a onClick={() => {
            WrapperModal[showType]({
                title: title,
                content: message,
                width: 520,
                style: { wordBreak: 'break-word' }
            })
        }}>查看详情</a>
    </span>) : message;
    notification[showType as keyof NotificationApi]({
        ...config,
        message: title,
        description: showMessage
    });
}

/**
 * 全局唯一的notification实例
 * 规则：在固定时间段内，相连并且相同的错误信息只会弹出一个。
 * @param {*} title
 * @param {*} message
 */
export function singletonNotification (title: any, message?: any, type?: any, style?: any) {
    const notifyMsgs = document.querySelectorAll('.ant-notification-notice-description');

    /**
    * 1.当前无实例
    * 2.当前存在实例，但是当前实例的最后一个信息和调用的信息不相等
    * 3.存在实例，并且相等，但是已经超出了限定的时间
    */
    if (!notifyMsgs.length ||
        notifyMsgs[notifyMsgs.length - 1].innerHTML != message ||
        checkIsTimeout()
    ) {
        dtNotification(title, message, type, {
            style
        })
    }
}
export function getContainer (id: string) {
    const container = document.createElement('div');
    document.getElementById(id).appendChild(container);
    return container;
}

/**
 * 替换对象数组中某个对象的字段名称
 * @param {} data
 * @param {*} targetField
 * @param {*} replaceName
 */
export function replaceObjectArrayFiledName (data: any, targetField: any, replaceName: any) {
    data && data.map((item: any) => {
        if (item[targetField] && item[targetField].length > 0) {
            item[replaceName] = [...item[targetField]];
            delete item[targetField];
        }
        return item;
    })
}

/**
 * 初始化APP_CONF和COMMON_CONF
 */
export function initConfig () {
    const appConf = window.APP_CONF || {};
    const commonConf = window.COMMON_CONF || {};
    window.APP_CONF = {
        ...commonConf,
        ...appConf
    }
}

/**
 * 不区分大小写的过滤 value Option
 */
export const filterValueOption = (input: any, option: any) => {
    return option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
}

/**
 * 从document.body 隐藏 mxGraph 所产生的的tooltip
 */
export const removeToolTips = () => {
    const remove = () => {
        const tips = document.querySelectorAll('.mxTooltip');
        if (tips) {
            tips.forEach((o: any) => {
                o.style.visibility = 'hidden';
            })
        }
    }
    setTimeout(remove, 500);
}
/**
 * 把running:{ value: 0, text '运行中' } 结构的数据，转换为 0: { symbol: 'running', text: '运行中' }
 */
export function generateValueDic (dic: any): {
    [propName: number]: {
        symbol: any;
        [propName: string]: any;
        [propName: number]: any;
    };
    [propName: string]: {
        symbol: any;
        [propName: string]: any;
        [propName: number]: any;
    };
} {
    let newDic: any = {};
    Object.keys(dic).forEach((key: any) => {
        let v = dic[key];
        newDic[v.value] = {
            symbol: key,
            ...v
        }
    });
    return newDic;
}

export function toRdosGateway (uri: any, params: any = {}) {
    params['redirect_uri'] = uri;
    const keyAndValues = Object.entries(params);
    const queryStr = keyAndValues.map(([key, value]) => {
        return `${key}=${value}`
    }).join('&');
    window.open(`${rdosApp.link}/gateway${queryStr ? `?${queryStr}` : ''}`, 'rdos_open');
}

export function isCookieBeProjectType (key: any) {
    return ['project_id', 'science_project_id', 'tag_project_id', 'stream_project_id'].includes(key);
}

export function isCurrentProjectChanged (key: any) {
    const projectIdCheckMap: any = {
        'project_id': rdosApp.filename,
        'science_project_id': scienceApp.filename,
        'stream_project_id': streamApp.filename,
        'tag_project_id': tagApp.filename
    }
    const pathname = location.pathname;
    const projectPathname = projectIdCheckMap[key];
    if (projectPathname && pathname.indexOf(projectPathname) > -1) {
        return true;
    }
    return false;
}

export function loopIsIntercept (pathAddress: any, arr: any) {
    for (let i = 0; i < arr.length; i++) {
        if (pathAddress.indexOf(arr[i].url) > -1 && arr[i].isShow) {
            window.location.href = '/';
            return;
        }
    }
}

export function getCurrentPath () {
    return document.location.pathname + document.location.hash + document.location.search;
}

export function getEnableLicenseApp (apps: any, licenseApps: any = []) {
    const newApps = cloneDeep(apps);
    let enableApps: any = [];
    if (licenseApps && licenseApps.length > 0) {
        if (apps && apps.length > 0) {
            for (let i: any = 0; i < newApps.length; i++) {
                for (let j: any = 0; j < licenseApps.length; j++) {
                    if (newApps[i].id == licenseApps[j].id && licenseApps[j].isShow) {
                        newApps[i].enable = licenseApps[j].isShow;
                        enableApps.push(newApps[i]);
                    }
                }
            }
        }
    }
    return enableApps
}
/**
 * 去除python注释
 * @param codeText 需要去除的文本
 */
export function filterPythonComment (codeText: string): string {
    if (!codeText) {
        return codeText;
    }
    // (Qouta_code_Qouta|codeWithoutQoutaAndHashTag)*_#_text
    const reg = /^(([^'"#]|('.*'|".*"))*)#.*/;
    codeText = codeText.replace(/\r\n/g, '\n');
    let codeArr: string[] = codeText.split('\n');
    return codeArr.map((line) => {
        let regResult = reg.exec(line);
        if (regResult) {
            return regResult[1];
        }
        return line;
    }).join('\n');
}

/**
 * 创建timepicker disable区间
 * @param beginDate moment.Moment
 * @param endDate moment.Moment
 * @param type string
 * @param isEnd boolean
 */
export function disableRangeCreater (beginDate: moment.Moment, endDate: moment.Moment, type: 'hour' | 'minute' | 'second', isEnd?: boolean): number[] {
    beginDate = beginDate.clone();
    endDate = endDate.clone();
    let compareDate = isEnd ? endDate : beginDate;
    let otherDate = isEnd ? beginDate : endDate;
    let max;
    let rangeValue;
    switch (type) {
        case 'hour': {
            max = 24;
            compareDate.hours(otherDate.hours());
            rangeValue = otherDate.hours();
            break;
        }
        case 'minute': {
            if (otherDate.hours() != compareDate.hours()) {
                return [];
            }
            max = 60;
            compareDate.minutes(otherDate.minutes());
            rangeValue = otherDate.minutes();
            break;
        }
        case 'second': {
            if (otherDate.hours() != compareDate.hours() || otherDate.minutes() != compareDate.minutes()) {
                return [];
            }
            max = 60;
            compareDate.seconds(otherDate.seconds());
            rangeValue = otherDate.seconds();
            break;
        }
    }
    if (isEnd) {
        return range(compareDate < otherDate ? (rangeValue - 1) : rangeValue);
    }
    return range(compareDate > otherDate ? rangeValue : (rangeValue + 1), max)
}
