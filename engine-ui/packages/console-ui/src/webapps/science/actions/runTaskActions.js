import API from '../api';
import { message } from 'antd';
import { sqlExecStatus, taskStatus, TASK_STATUS } from '../consts';
import { createLinkMark, createLog } from 'widgets/code-editor/utils'
import { generateValueDic } from 'funcs'
import { setNotebookLog, appendNotebookLog, setNotebookResult, showNotebookLog } from './notebookActions';
import { addLoadingTab, removeLoadingTab } from './editorActions';

const statusTextDic = generateValueDic(TASK_STATUS);

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

const jobData = {};

function setJobData (tabId, jobId) {
    jobData[tabId] = { jobId };
}

function getJobData (tabId) {
    return jobData[tabId];
}

function isTaskStoped (tabId) {
    return runTaskStatus[tabId] == RUN_TASK_STATUS.STOP;
}
function setTaskStop (tabId) {
    runTaskStatus[tabId] = RUN_TASK_STATUS.STOP;
    setJobData(tabId, null);
}
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
        res = await API.notebook.execTask(params);
    } catch (e) {
        return false;
    }
    if (isTaskStoped(tabId)) {
        return false;
    }
    resolveMsg(tabId, res, dispatch);
    if (res && res.code == 1) {
        const jobId = res.data.jobId;
        if (jobId) {
            setJobData(tabId, jobId)
            let selectRes = await pollTask(tabId, jobId, dispatch);
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
        res = await API.notebook.pollTask({ jobId });
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
                outputStatus(tabId, res.data.status, dispatch)
                let pollRes = await new Promise((resolve, reject) => {
                    setTimeout(() => {
                        resolve(pollTask(tabId, jobId, dispatch));
                    }, 3000);
                })
                return pollRes;
            }
        }
    } else {
        dispatch(appendNotebookLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}

export function stopTask (tabId, isSilent = false) {
    return async dispatch => {
        if (isSilent) {
            setTaskStop(tabId);
        } else {
            let res = await API.notebook.stopExecSQL({
                taskId: tabId,
                jobId: getJobData(tabId).jobId
            });
            if (res && res.code == 1) {
                message.success('停止信号已发出');
            }
        }
    }
}

function outputStatus (tabId, status, dispatch) {
    const statusText = statusTextDic[status] && statusTextDic[status].text;
    dispatch(appendNotebookLog(tabId, createLog(`${statusText || '未知状态'}.....`, 'info')))
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
