import React, { Component } from 'react'
import { debounce } from 'lodash';

import { TASK_TYPE, TASK_STATUS, RESOURCE_TYPE, LEARNING_TYPE, PYTON_VERSION, SCRIPT_TYPE } from './const'

// 请求防抖动
export function debounceEventHander(...args) {
    const debounced = debounce(...args)
    return function (e) {
        e.persist()
        return debounced(e)
    }
}

/**
 * 默认的SQL模板
 */
export function getDefaultSQLTemp(data) {
    let temp = ''
    for (let i = 0; i < data.length; i++) {
        temp +=
            `ADDJAR ADD JAR WITH 
        ${data[i]};
    `
    }
    return temp;
}

/**
 * 获取任务类型指定图表className
 */
export function taskTypeIcon(type, task = {}) {
    const { pythonVersion, learningType, scriptType } = task;
    if (scriptType == null) {
        switch (type) {
            case TASK_TYPE.SQL: {
                return 's-sqlicon'
            }
            case TASK_TYPE.MR: {
                return 's-mricon'
            }
            case TASK_TYPE.SYNC: {
                return 's-datasyncicon'
            }
            case TASK_TYPE.PYTHON: {
                return 's-sparkpythonicon'
            }
            case TASK_TYPE.VIRTUAL_NODE: {
                return 's-virtualicon'
            }
            case TASK_TYPE.DEEP_LEARNING: {
                if (learningType == LEARNING_TYPE.MXNET) {
                    return 's-mxnet'
                } else if (learningType == LEARNING_TYPE.TENSORFLOW) {
                    return 's-tensorflow'
                } else {
                    return 's-deeplearning'
                }
            }
            case TASK_TYPE.PYTHON_23: {
                if (pythonVersion == PYTON_VERSION.PYTHON2) {
                    return 's-python2icon'
                } else if (pythonVersion == PYTON_VERSION.PYTHON3) {
                    return 's-python3icon'
                } else {
                    return 's-pythonicon'
                }
            }
            case TASK_TYPE.SHELL: {
                return 's-shell'
            }
            default: ''
        }
    } else {
        switch (type) {
            case SCRIPT_TYPE.SQL: {
                return 's-sqlicon'
            }
            case SCRIPT_TYPE.PYTHON2: {
                return 's-python2icon'
            }
            case SCRIPT_TYPE.PYTHON3: {
                return 's-python3icon'
            }
            case SCRIPT_TYPE.SHELL: {
                return 's-shell'
            }
            default: ''
        }
    }

}

export function resourceTypeIcon(type) {
    switch (type) {
        case RESOURCE_TYPE.JAR: {
            return 's-jaricon-r'
        }
        case RESOURCE_TYPE.PY: {
            return 's-pythonicon-r'
        }
        default:
            return '';
    }
}

/**
 * 查找树中的某个节点
 */
export function findTreeNode(treeNode, node) {
    let result = ""
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
export function removeTreeNode(treeNodes, target) {
    for (let i = 0; i < treeNodes.length; i += 1) {
        if (treeNodes[i].id === target.id) {
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
export function appendTreeNode(treeNode, append, target) {
    const targetId = parseInt(target.id, 10)
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            appendTreeNode(children[i], append, target)
        }
    }
    if (treeNode.id === targetId && treeNode.children) {
        treeNode.children.push(append)
    }
}

/**
 * 遍历树形节点，用新节点替换老节点
*/
export function replaceTreeNode(treeNode, replace) {
    if (
        treeNode.id === parseInt(replace.id, 10)
        &&
        treeNode.level === replace.level
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
 * 匹配自定义任务参数
 * @param {Array} taskCustomParams 
 * @param {String} sqlText 
 */
export function matchTaskParams(taskCustomParams, sqlText) {
    const regx = /\$\{([.\w]+)\}/g;
    const data = [];
    let res = null;
    while ((res = regx.exec(sqlText)) !== null) {
        const name = res[1]
        const param = {
            paramName: name,
            paramCommand: '',
        };
        const sysParam = taskCustomParams.find(item => item.paramName === name)
        if (sysParam) {
            param.type = 0
            param.paramCommand = sysParam.paramCommand
        } else {
            param.type = 1
        }
        // 去重
        const exist = data.find(item => name === item.paramName)
        if (!exist) {
            data.push(param)
        }
    }
    return data
}

export function getVertxtStyle(type) {
    switch (type) {
        case TASK_STATUS.FINISHED: // 完成
        case TASK_STATUS.SET_SUCCESS:
            return 'whiteSpace=wrap;fillColor=#F6FFED;strokeColor=#B7EB8F;'
        case TASK_STATUS.SUBMITTING:
        case TASK_STATUS.RUNNING:
            return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;'
        case TASK_STATUS.RESTARTING:
        case TASK_STATUS.STOPING:
        case TASK_STATUS.DEPLOYING:
        case TASK_STATUS.WAIT_RUN:
            return 'whiteSpace=wrap;fillColor=#FFFBE6;strokeColor=#FFE58F;'
        case TASK_STATUS.RUN_FAILED:
        case TASK_STATUS.SUBMIT_FAILED:
            return 'whiteSpace=wrap;fillColor=#FFF1F0;strokeColor=#FFA39E;'
        case TASK_STATUS.STOPED:// 等待
        case TASK_STATUS.FROZEN:
        case TASK_STATUS.WAIT_SUBMIT:
        default: // 默认
            return 'whiteSpace=wrap;fillColor=#F3F3F3;strokeColor=#D4D4D4;'
    }
}

/**
 * 判断当前在离线应用
 */
export function inOffline() {
    return location.href.indexOf('offline') > -1;
}

/**
 * 判断当前在实时应用
 */
export function inRealtime() {
    return location.href.indexOf('realtime') > -1;
}