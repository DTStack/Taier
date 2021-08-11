import { TASK_STATUS } from '../../../consts/comm'
import { APPS_TYPE } from '../../../consts'

declare var window: any

export function getVertxtStyle (type: any) {
    switch (type) {
        case TASK_STATUS.FINISHED: // 完成
        case TASK_STATUS.SET_SUCCESS:
            return 'whiteSpace=wrap;fillColor=#F6FFED;strokeColor=#B7EB8F;';
        case TASK_STATUS.SUBMITTING:
        case TASK_STATUS.TASK_STATUS_NOT_FOUND:
        case TASK_STATUS.RUNNING:
            return 'whiteSpace=wrap;fillColor=#E6F7FF;strokeColor=#90D5FF;';
        case TASK_STATUS.RESTARTING:
        case TASK_STATUS.STOPING:
        case TASK_STATUS.DEPLOYING:
        case TASK_STATUS.WAIT_SUBMIT:
        case TASK_STATUS.WAIT_RUN:
            return 'whiteSpace=wrap;fillColor=#FFFBE6;strokeColor=#FFE58F;';
        case TASK_STATUS.RUN_FAILED:
        case TASK_STATUS.PARENT_FAILD:
        case TASK_STATUS.SUBMIT_FAILED:
            return 'whiteSpace=wrap;fillColor=#FFF1F0;strokeColor=#FFA39E;';
        case TASK_STATUS.FROZEN:
            return 'whiteSpace=wrap;fillColor=#EFFFFE;strokeColor=#26DAD1;';
        case TASK_STATUS.STOPED: // 已停止
        default:
        // 默认
            return 'whiteSpace=wrap;fillColor=#F3F3F3;strokeColor=#D4D4D4;';
    }
}

export function goToTaskDev (record: any) {
    const { appType, id, projectId } = record ?? {}
    if (appType == APPS_TYPE.INDEX) {
        window.open(`${window.APP_CONF.EASY_INDEX_URL}/#/easy-index/index-define?taskId=${id}&pid=${projectId}`)
    }
}
