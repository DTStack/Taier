import * as React from 'react';
import { Circle } from 'widgets/circle';
import { Icon, Tooltip } from 'antd';
import { TASK_STATUS, CHECK_STATUS } from '../../consts';

/**
 * 字段状态校验
 * @param {*} status
 */
export function DetailCheckStatus (props: any) {
    switch (props.value) {
        case true: {
            return <span>通过</span>;
        }
        case false: {
            return (
                <Tooltip title="未通过">
                    <Icon
                        type="close-circle-o"
                        style={{ fontSize: 16, color: '#f00' }}
                    />
                </Tooltip>
            );
        }
        default: {
            return <span>--</span>;
        }
    }
}

/**
 * 字段状态校验
 * @param {*} status
 */
export function TaskStatus (props: any) {
    switch (props.value) {
        case TASK_STATUS.WAIT_RUN:
            return (
                <span style={props.style}>
                    <Circle
                        title="等待运行"
                        style={{ background: '#d9d9d9' }}
                    />{' '}
                    等待运行
                </span>
            );
        case TASK_STATUS.RUNNING:
            return (
                <span style={props.style}>
                    <Circle title="运行中" style={{ background: '#2491F7' }} />{' '}
                    运行中
                </span>
            );
        case TASK_STATUS.FAIL:
            return (
                <span style={props.style}>
                    <Circle
                        title="运行失败"
                        style={{ background: '#EF5350' }}
                    />{' '}
                    运行失败
                </span>
            );
        case TASK_STATUS.PASS:
            return (
                <span style={props.style}>
                    <Circle
                        title="校验通过"
                        style={{ background: '#00A755' }}
                    />{' '}
                    校验通过
                </span>
            );
        case TASK_STATUS.UNPASS:
            return (
                <span style={props.style}>
                    <Circle
                        title="校验未通过"
                        style={{ background: '#F5A623' }}
                    />{' '}
                    校验未通过
                </span>
            );
        default:
            return <span style={props.style}>--</span>;
    }
}

/**
 * 逐行校验结果
 * @param {*} status
 */
export function DataCheckStatus (props: any) {
    switch (props.value) {
        case CHECK_STATUS.INITIAL:
            return (
                <span style={props.style}>
                    <Circle
                        title="等待运行"
                        style={{ background: '#d9d9d9' }}
                    />{' '}
                    等待运行
                </span>
            );
        case CHECK_STATUS.RUNNING:
            return (
                <span style={props.style}>
                    <Circle title="运行中" style={{ background: '#2491F7' }} />{' '}
                    运行中
                </span>
            );
        case CHECK_STATUS.SUCCESS:
            return (
                <span style={props.style}>
                    <Circle
                        title="运行成功"
                        style={{ background: '#00A755' }}
                    />{' '}
                    运行成功
                </span>
            );
        case CHECK_STATUS.FAIL:
            return (
                <span style={props.style}>
                    <Circle
                        title="运行失败"
                        style={{ background: '#EF5350' }}
                    />{' '}
                    运行失败
                </span>
            );
        case CHECK_STATUS.PASS:
            return (
                <span style={props.style}>
                    <Circle
                        title="校验通过"
                        style={{ background: '#00A755' }}
                    />{' '}
                    校验通过
                </span>
            );
        case CHECK_STATUS.UNPASS:
            return (
                <span style={props.style}>
                    <Circle
                        title="校验未通过"
                        style={{ background: '#F5A623' }}
                    />{' '}
                    校验未通过
                </span>
            );
        case CHECK_STATUS.EXPIRED:
            return (
                <span style={props.style}>
                    <Circle
                        title="校验结果失效"
                        style={{ background: '#F5A623' }}
                    />{' '}
                    校验结果失效
                </span>
            );
        default:
            return <span style={props.style}>--</span>;
    }
}
