import React from 'react'

import { TASK_TYPE, SCHEDULE_STATUS } from '../../comm/const'
import { Circle } from 'widgets/circle' 

export function taskTypeText(type) {
    switch (type) {
        case TASK_TYPE.MR:
            return 'MR';
        case TASK_TYPE.SYNC:
            return '数据同步';
        case TASK_TYPE.VIRTUAL_NODE:
            return 'Virtual';
        case TASK_TYPE.PYTHON:
            return 'Python';
        case TASK_TYPE.R:
            return 'R';
        case TASK_TYPE.SQL:
            return 'SQL';
        default:
            return '未知';
    }
}

export function TaskScheduleStatus(props) {
    switch (props.value) {
        case SCHEDULE_STATUS.STOPPED:
        return <Circle title="已冻结" style={{ background: '#26DAD2' }} />;
        case SCHEDULE_STATUS.NORMAL:
        default:
            return <Circle title="正常" style={{ background: '#2491F7' }} />;
    }
}

export function IndexType(props) {
    switch (props.value) {
        case 1:
        return <span>原子指标</span>;
        case 2:
        default:
            return <span>修饰词</span>;
    }
}

export function TableNameCheck(props) {
    switch (props.value) {
        case 1:
        return <span>分层不合理</span>;
        case 2:
            return <span>分层不合理</span>;
        case 3:
        return <span>引用标识不合理</span>;
        case 4:
            return <span>引用不合理</span>;
        default:
            return <span></span>;
    }
}

export function FieldNameCheck(props) {
    switch (props.value) {
        case 1:
        return <span>字段名称不合理</span>;
        case 2:
            return <span>字段类型不合理</span>;
        case 3:
        return <span>字段描述不合理</span>;
        default:
            return <span></span>;
    }
}