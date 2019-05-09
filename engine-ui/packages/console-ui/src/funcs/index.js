import { debounce, endsWith } from 'lodash';
import { notification, Modal } from 'antd'
import React from 'react';
import { MY_APPS } from 'main/consts';
import { rdosApp } from 'config/base';

/**
 * 存放一些零碎的公共方法
*/

// 请求防抖动
export function debounceEventHander (...args) {
    const debounced = debounce(...args)
    return function (e) {
        e.persist()
        return debounced(e)
    }
}

/**
 * 查找树中的某个节点
 */
export function findTreeNode (treeNode, node) {
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
export function removeTreeNode (treeNodes, target) {
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
export function appendTreeNode (treeNode, append, target) {
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
export function replaceTreeNode (treeNode, replace) {
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
export function mergeTreeNodes (origin, target) {
    replaceTreeNode(origin, target);
    if (target.children) {
        const children = target.children
        for (let i = 0; i < children.length; i += 1) {
            mergeTreeNodes(origin, children[i])
        }
    }
}

/**
 * 打开新窗口
 * @param {*} url
 * @param {*} target
 */
export function openNewWindow (url, target) {
    window.open(url, target || '_blank')
}

/**
 * 检验改应用是否包含项目选项
 * @param {s} app
 */
export function hasProject (app) {
    return app === MY_APPS.RDOS || app === MY_APPS.STREAM || app === MY_APPS.SCIENCE
}

/**
 * 字符串替换
 */
export function replaceStrFormBeginToEnd (str, replaceStr, begin, end) {
    return str.substring(0, begin) + replaceStr + str.substring(end + 1)
}

/**
 * 字符串替换（根据索引数组）
 */
export function replaceStrFormIndexArr (str, replaceStr, indexArr) {
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
export function filterComments (sql) {
    let tmpArr = [];
    const comments = [];
    if (!sql) {
        return '';
    }
    for (let i = 0; i < sql.length; i++) {
        let char = sql[i];

        // 读取字符
        // 推入数组
        tmpArr.push({
            index: i,
            char: char
        });
        let firstChar = tmpArr[0];
        // 校验数组是否有匹配语法
        if (tmpArr.length < 2) {
            if (firstChar.char != "'" && firstChar.char != '"' && firstChar.char != '-') {
                tmpArr = [];
            }
            continue;
        }

        let lastChar = tmpArr[tmpArr.length - 1];

        if (firstChar.char == "'" || firstChar.char == '"') {
            // 引号匹配，则清空
            if (lastChar.char == firstChar.char) {
                tmpArr = [];
                continue;
            }
        } else if (firstChar.char == '-') {
            // 假如第一个是横线，则开始校验注释规则
            // 判断是否为两个注释符号，不是，则清空
            if (tmpArr[1].char != '-') {
                tmpArr = []; continue;
            } else if (lastChar.char == '\n') { // 为注释作用域，遇到换行符，则结束注释
                comments.push({
                    begin: firstChar.index,
                    end: lastChar.index
                })
                tmpArr = []; continue;
            } else if (i == sql.length - 1) { // 解析结束
                comments.push({
                    begin: firstChar.index,
                    end: i
                })
                continue;
            }
        } else {
            tmpArr = [];
        }
    }

    sql = replaceStrFormIndexArr(sql, ' ', comments)
    return sql;
}

/**
 * 分割sql
 * @param {String} sqlText
 */
export function splitSql (sqlText) {
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

export function getRandomInt (min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min)) + min; // The maximum is exclusive and the minimum is inclusive
}

/**
 * 滚动元素到视窗范围内
 */
export function scrollToView (id) {
    const ele = document.getElementById(id);
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
export function timeout (promise, ms) {
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
    const iconMap = {
        'error': <img src='public/main/img/icon/notification-error.svg' />,
        'success': <img src='public/main/img/icon/notification-success.svg' />
    }
    changeArr.forEach((key) => {
        const oldFunc = notification[key];
        notification[key] = function (config = {}) {
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

/**
 * 全局唯一的notification实例
 * 规则：在固定时间段内，相连并且相同的错误信息只会弹出一个。
 * @param {*} title
 * @param {*} message
 */
export function singletonNotification (title, message, type, style) {
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
export function dtNotification (title, message, type, config) {
    const showType = type || 'error';
    const showMessage = message.length > 100 ? (<span>
        {message.substring(0, 100)}... <a onClick={() => {
            Modal[showType]({
                title: title,
                content: message,
                width: 520,
                style: { wordBreak: 'break-word' }
            })
        }}>查看详情</a>
    </span>) : message;
    notification[showType]({
        ...config,
        message: title,
        description: showMessage
    });
}
export function getContainer (id) {
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
export function replaceObjectArrayFiledName (data, targetField, replaceName) {
    data && data.map(item => {
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
export const filterValueOption = (input, option) => {
    return option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
}

/**
 * 从document.body 隐藏 mxGraph 所产生的的tooltip
 */
export const removeToolTips = () => {
    const remove = () => {
        const tips = document.querySelectorAll('.mxTooltip');
        if (tips) {
            tips.forEach(o => {
                o.style.visibility = 'hidden';
            })
        }
    }
    setTimeout(remove, 500);
}
/**
 * 把running:{ value: 0, text '运行中' } 结构的数据，转换为 0: { symbol: 'running', text: '运行中' }
 */
export function generateValueDic (dic) {
    let newDic = {};
    Object.keys(dic).forEach((key) => {
        let v = dic[key];
        newDic[v.value] = {
            symbol: key,
            ...v
        }
    });
    return newDic;
}

export function toRdosGateway (uri, params = {}) {
    params['redirect_uri'] = uri;
    const keyAndValues = Object.entries(params);
    const queryStr = keyAndValues.map(([key, value]) => {
        return `${key}=${value}`
    }).join('&');
    location.href = `${rdosApp.link}/gateway${queryStr ? `?${queryStr}` : ''}`;
}
