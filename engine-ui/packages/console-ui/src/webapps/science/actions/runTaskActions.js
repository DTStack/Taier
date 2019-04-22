/* eslint-disable no-unused-vars */
import API from '../api';
import { sqlExecStatus, taskStatus, TASK_STATUS } from '../consts';
import { createLinkMark, createLog } from 'widgets/code-editor/utils'
import { setNotebookLog, appendNotebookLog, setNotebookResult, showNotebookLog } from './notebookActions';
import { setExperimentLog, appendExperimentLog, setExperimentResult, showExperimentLog } from './experimentActions';
import { addLoadingTab, removeLoadingTab } from './editorActions';

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

const RUN_TASK_STATUS = {
    NONE: 1,
    STOP: 0
}
const runTaskStatus = {};

function isTaskStoped (tabId) {
    return runTaskStatus[tabId] == RUN_TASK_STATUS.STOP;
}
// function setTaskStop (tabId) {
//     runTaskStatus[tabId] = RUN_TASK_STATUS.STOP;
// }
function resetTaskStatus (tabId) {
    runTaskStatus[tabId] = RUN_TASK_STATUS.NONE;
}

export function exec (tabData, serverParams = {}, tasks) {
    return async (dispatch) => {
        const tabId = tabData.id;
        resetTaskStatus(tabId);
        dispatch(addLoadingTab(tabId));
        dispatch(setNotebookLog(tabId, `正在提交...`));
        dispatch(showNotebookLog(tabId));
        let res = await execTasks(tabData, serverParams, tasks, dispatch);
        dispatch(removeLoadingTab(tabId));
        return res;
    }
}
async function execTasks (tabData, serverParams, tasks, dispatch) {
    const tabId = tabData.id;
    for (let i = 0; i < tasks.length; i++) {
        const task = tasks[i];
        dispatch(appendNotebookLog(tabId, createLog(`第${i + 1}条任务开始执行`, 'info')));
        let res = await execTask(tabData, serverParams, task, dispatch);
        if (!res) {
            return false;
        }
    }
    return true;
}

async function execTask (tabData, serverParams, task, dispatch) {
    const tabId = tabData.id;
    const params = {
        ...serverParams,
        sql: task,
        taskId: tabId,
        uniqueKey: tabId + '_' + Date.now() + '_' + ~~(Math.random() * 10000)
    };
    let res;
    try {
        res = await API.comm.execTask(params);
    } catch (e) {
        return false;
    }
    if (isTaskStoped(tabId)) {
        return false;
    }
    resolveMsg(tabId, res, dispatch);
    if (res && res.code == 1) {
        if (res.data.jobId) {
            let selectRes = await pollTask(tabId, res.data.jobId, dispatch);
            return selectRes;
        } else {
            resolveData(tabId, res.data, params.uniqueKey, dispatch);
            return true;
        }
    } else {
        dispatch(appendNotebookLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}
/**
 * 轮询任务状态结果
 * @param {*} tabId tabId
 * @param {*} jobId jobID
 * @param {*} dispatch redux dispatch
 */
async function pollTask (tabId, jobId, dispatch) {
    let res;
    if (isTaskStoped(tabId)) {
        return false;
    }
    try {
        res = await API.comm.pollTask({ jobId });
    } catch (e) {
        return false;
    }
    resolveMsg(tabId, res, dispatch);
    if (res && res.code == 1) {
        const status = res.data.status;
        switch (status) {
            case taskStatus.FINISHED: {
                // 成功
                resolveData(tabId, res.data, jobId, dispatch);
                return true;
            }
            case taskStatus.FAILED:
            case taskStatus.CANCELED: {
                outputStatus(tabId, status, dispatch);
                if (res.data && res.data.download) {
                    dispatch(appendNotebookLog(tabId, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
                }
                return false;
            }
            default: {
                outputStatus(res.data.status, '.....')
                let pollRes = await pollTask(tabId, jobId, dispatch);
                return pollRes;
            }
        }
    } else {
        dispatch(appendNotebookLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}

function outputStatus (tabId, status, dispatch) {
    dispatch(appendNotebookLog(tabId, createLog(`${status}.....`, 'info')))
}
/**
 * 处理打印消息
 * @param {*} tabId tabId
 * @param {*} res 返回值
 * @param {*} dispatch redux dispatch
 */
function resolveMsg (tabId, res, dispatch) {
    if (res.message) {
        dispatch(appendNotebookLog(tabId, createLog(`${res.message}`, 'error')))
    }
    if (res && res.code == 1) {
        if (res.data && res.data.msg) {
            dispatch(
                appendNotebookLog(
                    tabId,
                    createLog(`${res.data.msg}`, getLogStatus(res.data.status))
                )
            )
        }
    }
}
function resolveData (tabId, data, key, dispatch) {
    dispatch(appendNotebookLog(tabId, createLog('执行完成!', 'info')));
    if (data.result) {
        dispatch(setNotebookResult(tabId, key, data.result))
    }
    if (data && data.download) {
        dispatch(appendNotebookLog(tabId, `完整日志下载地址：${createLinkMark({ href: data.download, download: '' })}\n`))
    }
}

// 运行实验task
export function execExperiment (tabData) {
    return async (dispatch) => {
        const tabId = tabData.id;
        dispatch(addLoadingTab(tabId));
        dispatch(setExperimentLog(tabId, `正在运行...`));
        dispatch(showExperimentLog(tabId));
        setTimeout(() => {
            runExperiment(tabData, dispatch);
            dispatch(removeLoadingTab(tabId));
        }, 1000)
    }
}

async function runExperiment (tabData, dispatch) {
    const tabId = tabData.id;
    const params = {
        taskId: tabId,
        uniqueKey: tabId + '_' + Date.now() + '_' + ~~(Math.random() * 10000)
    };
    let res;
    // TODO 模拟请求
    res = {
        code: 1,
        data: {
            jobId: '111111'
        },
        message: null
    };
    resolveMsgExperiment(tabId, res, dispatch);
    if (res && res.code == 1) {
        if (res.data.jobId) {
            let selectRes = await pollExperimentTask(tabId, res.data.jobId, dispatch);
            return selectRes;
        } else {
            resolveDataExperiment(tabId, res.data, params.uniqueKey, dispatch);
            return true;
        }
    } else {
        dispatch(appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}

function resolveMsgExperiment (tabId, res, dispatch) {
    if (res && res.message) {
        dispatch(appendExperimentLog(tabId, createLog(`${res.message}`, 'error')))
    }
    if (res && res.code == 1) {
        if (res.data && res.data.msg) {
            dispatch(
                appendExperimentLog(
                    tabId,
                    createLog(`${res.data.msg}`, getLogStatus(res.data.status))
                )
            )
        }
    }
}
function resolveDataExperiment (tabId, data, key, dispatch) {
    dispatch(appendExperimentLog(tabId, createLog('执行完成!', 'info')));
    if (data.result) {
        dispatch(setExperimentResult(tabId, key, data.result))
    }
    if (data && data.download) {
        dispatch(appendExperimentLog(tabId, `完整日志下载地址：${createLinkMark({ href: data.download, download: '' })}\n`))
    }
}

async function pollExperimentTask (tabId, jobId, dispatch) {
    let res;
    if (isTaskStoped(tabId)) {
        return false;
    }
    try {
        res = await API.comm.pollTask({ jobId });
    } catch (e) {
        return false;
    }
    resolveMsgExperiment(tabId, res, dispatch);
    if (res && res.code == 1) {
        const status = res.data.status;
        switch (status) {
            case TASK_STATUS.success: {
                // 成功
                resolveDataExperiment(tabId, res.data, jobId, dispatch);
                return true;
            }
            case TASK_STATUS.failure: {
                outputExperimentStatus(tabId, status, dispatch);
                if (res.data && res.data.download) {
                    dispatch(appendExperimentLog(tabId, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
                }
                return false;
            }
            default: {
                outputExperimentStatus(res.data.status, '.....', dispatch)
                let pollRes = await pollExperimentTask(tabId, jobId, dispatch);
                return pollRes;
            }
        }
    } else {
        dispatch(appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}
function outputExperimentStatus (tabId, status, dispatch) {
    dispatch(appendNotebookLog(tabId, createLog(`${status}.....`, 'info')))
}
