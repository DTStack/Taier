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
