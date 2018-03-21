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
        default:
            return 'SQL';
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