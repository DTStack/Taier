import { changeTab, changeTabSlient } from '../base/tab';
import { siderBarType, TASK_STATUS, sqlExecStatus } from '../../consts';
import { addLoadingTab, removeLoadingTab } from '../editorActions';
import experimentLog from './experimentLog';
import { createLinkMark, createLog } from 'widgets/code-editor/utils'
import api from '../../api/experiment';

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
 * 修改tab内容
 * @param {*} newContent
 * @param {*} tab
 * @param {*} isDirty
 * @param {*} slient 是否不触发reducer的修改
 */
export function changeContent (newContent, tab, isDirty = true, slient = false) {
    const change = slient ? changeTabSlient : changeTab;
    return change(siderBarType.experiment, {
        ...tab,
        ...newContent,
        isDirty
    })
}

function outputExperimentStatus (tabId, status, dispatch) {
    dispatch(experimentLog.appendExperimentLog(tabId, createLog(`${status}.....`, 'info')))
}
function resolveMsgExperiment (tabId, res, dispatch) {
    if (res && res.message) {
        dispatch(experimentLog.appendExperimentLog(tabId, createLog(`${res.message}`, 'error')))
    }
    if (res && res.code == 1) {
        if (res.data && res.data.msg) {
            dispatch(
                experimentLog.appendExperimentLog(
                    tabId,
                    createLog(`${res.data.msg}`, getLogStatus(res.data.status))
                )
            )
        }
    }
}
function resolveDataExperiment (tabId, data, key, dispatch) {
    dispatch(experimentLog.appendExperimentLog(tabId, createLog('执行完成!', 'info')));
    if (data.result) {
        dispatch(experimentLog.setExperimentResult(tabId, key, data.result))
    }
    if (data && data.download) {
        dispatch(experimentLog.appendExperimentLog(tabId, `完整日志下载地址：${createLinkMark({ href: data.download, download: '' })}\n`))
    }
}
/* 执行部分的任务 */
export function getRunTaskList (tabData, taskId, type, currentTab) {
    return async (dispatch, getState) => {
        console.log(currentTab, 'currentTab')
        dispatch(addLoadingTab(currentTab));
        dispatch(experimentLog.setExperimentLog(currentTab, `正在运行...`));
        dispatch(experimentLog.showExperimentLog(currentTab));
        // TODO 请求接口
        const response = {
            code: 1,
            data: {
                jobIds: { 2510: 2510, 2511: 2511, 2512: 2512, 2513: 2513 },
                result: null
            }
        }
        const jobs = [];
        for (const key in response.data.jobIds) {
            if (response.data.jobIds.hasOwnProperty(key)) {
                const element = response.data.jobIds[key];
                jobs.push(element);
            }
        }
        tabData.graphData.forEach((item) => {
            item.jobId = response.data.jobIds[item.id]
        })
        dispatch(changeContent(tabData, {}, false))
        await dispatch(getRunningTaskStatus(jobs, currentTab, tabData));
        dispatch(removeLoadingTab(currentTab));
    }
}
let index = 0;
function getRunningTaskStatus (jobIds, tabId, tabData) {
    return async (dispatch, getState) => {
        if (!tabId) {
            tabId = parseInt(getState().experiment.currentTabIndex);
        }
        // TODO 请求接口
        const response = {
            code: 1,
            data: {
                joobStatus: {
                    2510: index <= 2 ? index : 2,
                    2511: index <= 2 ? index - 1 : 2,
                    2512: index <= 2 ? index - 1 : 2,
                    2513: index <= 2 ? index - 1 : 2
                },
                status: index - 1
            }
        }
        tabData.graphData.forEach((item) => {
            if (item.vertex) {
                item.data.status = response.data.joobStatus[item.jobId]
            }
        })
        dispatch(changeContent(tabData, {}, false))
        // TODO 模拟的轮训
        resolveMsgExperiment(tabId, response, dispatch);
        if (response && response.code == 1) {
            const status = response.data.status;
            switch (status) {
                case TASK_STATUS.success: {
                    // 成功
                    index = 0;
                    resolveDataExperiment(tabId, response.data, null, dispatch);
                    return true;
                }
                case TASK_STATUS.failure: {
                    index = 0;
                    outputExperimentStatus(tabId, status, dispatch);
                    if (response.data && response.data.download) {
                        dispatch(experimentLog.appendExperimentLog(tabId, `完整日志下载地址：${createLinkMark({ href: response.data.download, download: '' })}\n`))
                    }
                    return false;
                }
                default: {
                    index += 1;
                    outputExperimentStatus(response.data.status, '.....', dispatch)
                    setTimeout(() => {
                        let pollRes = dispatch(getRunningTaskStatus(jobIds, tabId, tabData));
                        return pollRes;
                    }, 1000)
                }
            }
        } else {
            dispatch(experimentLog.appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
            return false;
        }
    }
}

// 运行实验task
export function execExperiment (tabData) {
    return async (dispatch) => {
        const tabId = tabData.id;
        dispatch(addLoadingTab(tabId));
        dispatch(experimentLog.setExperimentLog(tabId, `正在运行...`));
        dispatch(experimentLog.showExperimentLog(tabId));
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
            jobId: '111111',
            msg: '执行中'
        },
        message: null
    };
    resolveMsgExperiment(tabId, res, dispatch);
    if (res && res.code == 1) {
        if (res.data.jobId) {
            await pollExperimentTask(tabId, res.data.jobId, dispatch);
        } else {
            resolveDataExperiment(tabId, res.data, params.uniqueKey, dispatch);
            return true;
        }
    } else {
        dispatch(experimentLog.appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}
async function pollExperimentTask (tabId, jobId, dispatch) {
    let res;
    try {
        res = await api.submitExperiment({ jobId });
    } catch (e) {
        return false;
    }
    console.log('res:', res);
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
                    dispatch(experimentLog.appendExperimentLog(tabId, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
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
        dispatch(experimentLog.appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}
