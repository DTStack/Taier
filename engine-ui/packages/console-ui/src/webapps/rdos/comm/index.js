import React, { Component } from 'react'
import { debounce } from 'lodash';

import {  TASK_TYPE } from './const'

// 请求防抖动
export function debounceEventHander(...args) {
    const debounced = debounce(...args)
    return function(e) {
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
export function taskTypeIcon(type) {
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
            return 's-pythonicon'
        }
        case TASK_TYPE.VIRTUAL_NODE: {
            return 's-virtualicon'
        }
        default: ''
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