import { TASK_TYPE } from '../../comm/const'

export function taskTypeText(type) {
    switch (type) {
        case TASK_TYPE.MR:
            return 'MR';
        case TASK_TYPE.SYNC:
            return 'Sync';
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