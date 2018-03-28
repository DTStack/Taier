import React, { Component } from 'react';
import { Circle } from 'widgets/circle';

import { TASK_STATUS } from '../../consts';

/**
 * 字段状态校验
 * @param {*} status 
 */
export function FildCheckStatus(props) {
    if (props.value) {
        return '通过'
    } else if (status === null) {
        return '未运行'
    } else {
        return '未通过'
    }
}


/**
 * 字段状态校验
 * @param {*} status 
 */
export function TaskStatus(props) {
    switch (props.value) {
        case TASK_STATUS.RUNNING:
            return <span>
                <Circle title="运行中" style={{ background: '#2491F7' }} /> 运行中
            </span>;
        case TASK_STATUS.FAIL:
            return <span>
                <Circle title="运行失败" style={{ background: '#EF5350' }} /> 运行失败
            </span>;
        case TASK_STATUS.PASS:
            return <span>
                <Circle title="校验通过" style={{ background: '#00A755' }} /> 校验通过
            </span>;
        case TASK_STATUS.UNPASS:
            return <span>
                <Circle title="校验未通过" style={{ background: '#EF5350' }} /> 校验未通过
            </span>;
        default:
            return '';
    }
}