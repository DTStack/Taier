import moment from 'moment'
import {
    message
} from 'antd';

import { createLinkMark, createLog } from 'widgets/code-editor/utils'

import API from '../api';
import { sqlExecStatus } from '../consts';
import editorAction from '../consts/editorActionType';

// const INTERVALS = 1500;

// 储存各个tab的定时器id，用来stop任务时候清楚定时任务
const intervalsStore = {}
// 停止信号量，stop执行成功之后，设置信号量，来让所有正在执行中的网络请求知道任务已经无需再继续
const stopSign = {}
// 正在运行中的sql key，调用stop接口的时候需要使用
const runningSql = {}

function getUniqueKey (id) {
    return `${id}_${moment().valueOf()}`
}

// async function doSelect (resolve, dispatch, jobId, currentTab) {
//     const res = await API.getSQLResultData({ jobId: jobId });
//     if (res && res.code) {
//         // 获取到返回值
//         if (res && res.message) dispatch(output(currentTab, `请求结果:\n ${res.message}`))
//         if (res && res.data && res.data.msg) dispatch(output(currentTab, `请求结果: ${res.data.msg}`))
//     }
//     // 状态正常
//     if (res && res.code === 1) {
//         switch (res.data.status) {
//         case sqlExecStatus.FINISHED: {
//             // 成功
//             getDataOver(dispatch, currentTab, res, jobId)
//             resolve(true);
//             return;
//         }
//         case sqlExecStatus.FAILED:
//         case sqlExecStatus.CANCELED: {
//             if (res.data && res.data.download) {
//                 dispatch(output(currentTab, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
//             }
//             dispatch(removeLoadingTab(currentTab))
//             resolve(false)
//             return;
//         }
//         default: {
//             // 正常运行，则再次请求,并记录定时器id
//             intervalsStore[currentTab] = setTimeout(
//                 () => {
//                     if (stopSign[currentTab]) {
//                         console.log('find stop sign in doSelect')
//                         stopSign[currentTab] = false;
//                         return;
//                     }
//                     doSelect(resolve, dispatch, jobId, currentTab)
//                 }, INTERVALS
//             )
//         }
//         }
//     } else {
//         dispatch(output(currentTab, `请求异常！`))
//         dispatch(removeLoadingTab(currentTab))
//         // 不正常，则直接终止执行
//         resolve(false)
//     }
// }
function getLogStatus (status) {
    switch (status) {
        case sqlExecStatus.FINISHED: {
            return 'info';
        }
        case sqlExecStatus.FAILED: {
            return 'error';
        }
        case sqlExecStatus.CANCELED: {
            return 'warning';
        }
        default: {
            return 'info'
        }
    }
}
/**
 * 输出SQL执行结果
 */
function getDataOver (dispatch, currentTab, res, jobId) {
    dispatch(output(currentTab, createLog('执行完成!', 'info')));
    if (res.data.result) {
        dispatch(outputRes(currentTab, res.data.result, jobId))
    }
    if (res.data && res.data.download) {
        dispatch(output(currentTab, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
    }
}

async function exec (dispatch, currentTab, task, params, sqls, index, resolve, reject) {
    const key = getUniqueKey(task.id);

    params.sql = `${sqls[index]}`;
    params.uniqueKey = key;
    runningSql[currentTab] = key; // 默认的运行 Key

    dispatch(output(currentTab, createLog(`第${index + 1}条任务开始执行`, 'info')));

    function execContinue () {
        if (stopSign[currentTab]) {
            console.log('find stop sign in exec')
            stopSign[currentTab] = false;
            return;
        }
        exec(dispatch, currentTab, task, params, sqls, index + 1, resolve, reject)
    }

    // 开始执行
    const res = await API.execSQL(params);

    // 假如已经是停止状态，则弃用结果
    if (stopSign[currentTab]) {
        console.log('find stop sign in succCall')
        stopSign[currentTab] = false;
        return;
    }

    if (res && res.code && res.message) dispatch(output(currentTab, createLog(`${res.message}`, 'error')))
    // 执行结束
    if (!res || (res && res.code != 1)) {
        dispatch(output(currentTab, createLog(`请求异常！`, 'error')))
        dispatch(removeLoadingTab(currentTab))
    }
    if (res && res.code === 1) {
        // dispatch(output(currentTab, '执行完成'));
        if (res.data && res.data.msg) dispatch(output(currentTab, createLog(`${res.data.msg}`, getLogStatus(res.data.status))))
        // 直接打印结果
        getDataOver(dispatch, currentTab, res, res.data.jobId);

        if (index < sqls.length - 1) {
            // 剩余任务，则继续执行
            execContinue();
        } else {
            dispatch(removeLoadingTab(currentTab));
            resolve(true);
        }
    }
}

// 执行sql
export function execSql (currentTab, task, params, sqls) {
    return (dispatch) => {
        stopSign[currentTab] = false;
        return new Promise((resolve, reject) => {
            exec(dispatch, currentTab, task, params, sqls, 0, resolve, reject);
        })
    }
}

// 停止sql
export function stopSql (currentTab, currentTabData, isSilent) {
    return async (dispatch, getState) => {
        // 静默关闭，不通知任何人（服务器，用户）
        if (isSilent) {
            const running = getState().editor.running;
            if (running.indexOf(currentTab) > -1) {
                stopSign[currentTab] = true;
                dispatch(output(currentTab, createLog('执行停止', 'warning')))
                dispatch(removeLoadingTab(currentTab))
                if (intervalsStore[currentTab]) {
                    clearTimeout(intervalsStore[currentTab])
                    intervalsStore[currentTab] = null;
                }
                return;
            }
            return;
        }

        const jobId = runningSql[currentTab];
        if (!jobId) return;

        const res = await API.stopExecSQL({
            taskId: currentTabData.id,
            jobId: jobId
        });

        if (res.code === 1) {
            dispatch(output(currentTab, createLog('执行停止', 'warning')));
            // 消除轮询定时器
            if (intervalsStore[currentTab]) {
                clearTimeout(intervalsStore[currentTab])
                intervalsStore[currentTab] = null;
            }

            stopSign[currentTab] = true;
            dispatch(removeLoadingTab(currentTab));
            message.success('停止执行成功！');
        } else {
            message.success('停止执行失败！');
        }
    }
}

// Actions
export function output (tabId, log, key, type) {
    return {
        payload: {
            key,
            tabId,
            siderType: type,
            data: log
        },
        type: editorAction.APPEND_CONSOLE_LOG
    }
}

export function setOutput (tabId, log, key, type, extData) {
    return {
        payload: {
            key,
            tabId,
            extData,
            siderType: type,
            data: createLog(log, 'info')
        },
        type: editorAction.SET_CONSOLE_LOG
    }
}

export function outputRes (tabId, data, key, type) {
    return {
        payload: {
            key,
            tabId,
            siderType: type,
            data
        },
        type: editorAction.UPDATE_RESULTS
    }
}

export function removeRes (tabId, index, key, type) {
    return {
        payload: {
            key,
            tabId,
            siderType: type,
            data: index
        },
        type: editorAction.DELETE_RESULT
    }
}

export function resetConsole (tabId, type) {
    return {
        payload: {
            siderType: type,
            tabId
        },
        type: editorAction.RESET_CONSOLE
    }
}

export function setSelectionContent (data) {
    return {
        type: editorAction.SET_SELECTION_CONTENT,
        data
    }
}

// Loading actions
export function addLoadingTab (id) {
    return {
        type: editorAction.ADD_LOADING_TAB,
        data: {
            id: id
        }
    }
}
export function removeLoadingTab (id) {
    return {
        type: editorAction.REMOVE_LOADING_TAB,
        data: {
            id: id
        }
    }
}
export function removeAllLoadingTab () {
    return {
        type: editorAction.REMOVE_ALL_LOAING_TAB
    }
}

export function updateEditorOptions (data) {
    return {
        type: editorAction.UPDATE_OPTIONS,
        data
    }
}

export function getEditorThemeClassName (editorTheme) {
    // 如果是dark类的编辑器，则切换ide的theme为dark风格
    return editorTheme === 'vs-dark' || editorTheme === 'hc-black'
        ? 'theme-dark' : 'theme-white';
}
