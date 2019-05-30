/* eslint-disable no-unreachable */
import { changeTab, changeTabSlient } from '../base/tab';
import { siderBarType, taskStatus } from '../../consts';
import { addLoadingTab, removeLoadingTab } from '../editorActions';
import experimentLog from './experimentLog';
import { createLinkMark, createLog } from 'widgets/code-editor/utils'
import api from '../../api/experiment';

function getLogStatus (status) {
    if (!status) return 'info';
    switch (status) {
        case taskStatus.FINISHED:
        case taskStatus.SET_SUCCESS:
            // 成功
            return 'info'

        case taskStatus.STOPED:
        case taskStatus.RUN_FAILED:
        case taskStatus.SUBMIT_FAILED:
        case taskStatus.KILLED:
        case taskStatus.FROZEN:
        case taskStatus.PARENT_FAILD:
        case taskStatus.FAILING:
            // 失败
            return 'error';
        default: {
            // 中间状态，进行中
            return 'info'
        }
    }
}
/**
 * 当前正在执行的任务列表
 */
class RunnningTask {
    _runnningTask = {};
    addRunningTaskList = (id) => {
        this._runnningTask[`_runnningTask_${id}`] = {};
    }
    get = (id) => {
        return this._runnningTask[`_runnningTask${id}`];
    }
    set = (id, value) => {
        this._runnningTask[`_runnningTask${id}`] = value;
    }
    reset = (id) => {
        this._runnningTask[`_runnningTask${id}`] = {}
    }
}
const runnningTask = new RunnningTask();
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
/**
 * @description 用于判断是否任务状态是否全部执行完毕，并且区分成功和失败
 * @param {Array} arrayList 数组
 * @returns compeletedFinish标志是否完全执行完毕，handlerStatus标志是否成功
 */
function isCompeletedFinish (arrayList) {
    const arr = [].concat(arrayList);
    let status = {
        handlerStatus: true,
        compeletedFinish: true
    }
    for (let index = 0; index < arr.length; index++) {
        const element = arr[index];
        switch (element.status) {
            case taskStatus.FINISHED:
            case taskStatus.SET_SUCCESS: {
                // 成功
                continue;
            }
            case taskStatus.STOPED:
            case taskStatus.RUN_FAILED:
            case taskStatus.SUBMIT_FAILED:
            case taskStatus.KILLED:
            case taskStatus.FROZEN:
            case taskStatus.PARENT_FAILD:
            case taskStatus.FAILING: {
                status.handlerStatus = false;
                break;
            }
            default: {
                status.compeletedFinish = false;
                break;
            }
        }
    }
    return status;
}
/**
 * 获取正在运行中组件id
 * @param {Array} arrayList
 */
function getRunningTask (arrayList) {
    const arr = [].concat(arrayList);
    const runningTask = arr.filter(o => {
        if (o) {
            switch (o.status) {
                case taskStatus.FINISHED:
                case taskStatus.SET_SUCCESS: return false;
                case taskStatus.STOPED:
                case taskStatus.RUN_FAILED:
                case taskStatus.SUBMIT_FAILED:
                case taskStatus.KILLED:
                case taskStatus.FROZEN:
                case taskStatus.PARENT_FAILD:
                case taskStatus.FAILING: return false;
                case taskStatus.WAIT_SUBMIT: return false;
                default: {
                    return true;
                }
            }
        } else {
            return false;
        }
    });
    return runningTask.map((item) => item.taskId)
}
/**
 * 获取取消状态的组件id
 * @param {Array} arrayList
 */
function getCancelTask (arrayList) {
    const arr = [].concat(arrayList);
    const runningTask = arr.filter(o => {
        switch (o.status) {
            case taskStatus.STOPED:
            case taskStatus.KILLED: return true;
            default: {
                return false;
            }
        }
    });
    return runningTask.map((item) => item.taskId)
}
let _cacheTask = [];
function getFinishedJustNowTask (arrayList) {
    const ids = getRunningTask(arrayList); // 当前正在运行中的taskid
    const preRunningIds = [].concat(_cacheTask); // 之前正在运行中的taskid
    const difference = preRunningIds.filter(o => o && ids.indexOf(o) === -1);
    const result = [];
    if (difference.length > 0) {
        difference.forEach((d) => {
            const object = arrayList.find(o => o.taskId === d);
            result.push(object);
        })
        _cacheTask = [];
    } else {
        _cacheTask = ids;
    }
    return result;
}

function outputExperimentStatus (tabId, taskName, dispatch) {
    let text = '';
    taskName.forEach((item) => {
        text += `[${item.data.name}]`
    })
    dispatch(experimentLog.appendExperimentLog(tabId, createLog(`${text || '等待'}执行中.....`, 'info')))
}
function resolveMsgExperiment (tabId, res, dispatch) {
    if (res && res.message) {
        dispatch(experimentLog.appendExperimentLog(tabId, createLog(`${res.message}`, 'error')))
    }
    if (res && res.code == 1) {
        const tasks = getFinishedJustNowTask(res.data);
        if (tasks.length > 0) {
            tasks.forEach(task => {
                task.msg && dispatch(
                    experimentLog.appendExperimentLog(
                        tabId,
                        createLog(`${task.msg}`, getLogStatus(res.data.status))
                    )
                )
            })
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
function rejectDataExperiment (tabId, data, tabData, dispatch) {
    const object = tabData.graphData.find(o => o.vertex && getCancelTask(data).includes(o.data.id + ''));
    // 获取取消状态的组件数据
    if (object) {
        dispatch(experimentLog.appendExperimentLog(tabId, createLog(`[${object.data.name}]节点执行取消`, 'info')));
    } else {
        dispatch(experimentLog.appendExperimentLog(tabId, createLog(`节点执行失败`, 'error')));
    }
    resolveDataExperiment(tabData, data, null, dispatch)
}

/**
 *  运行任务
 *  @param tabData-当前打开的tab的值，包括了detailData，graphData等数据
 *  @param taskId-需要执行的任务id
 *  @param type-运行任务的类型，0表示运行，1表示从此处开始执行，2表示执行到此节点，3表示执行此节点
 *  @param currentTab-表示当前tab的值
 *  */
export function getRunTaskList (tabData, taskId, type, currentTab) {
    return async (dispatch, getState) => {
        runnningTask.reset(currentTab);
        dispatch(addLoadingTab(currentTab));
        dispatch(experimentLog.setExperimentLog(currentTab, `正在运行...`));
        dispatch(experimentLog.showExperimentLog(currentTab));
        let res;
        try {
            res = await api.getTaskJobId({ taskId, executeOrder: type });
        } catch (e) {
            return false;
        }
        if (res.code === 1) {
            runnningTask.set(currentTab, res.data);
            tabData.graphData.forEach((item) => {
                if (item.vertex) {
                    item.data.jobId = res.data[item.data.id]
                }
            })
            dispatch(changeContent(tabData, {}, false));
            await getRunningTaskStatus(currentTab, tabData, res.data, dispatch);
            dispatch(removeLoadingTab(currentTab));
            runnningTask.reset(currentTab);
        } else {
            dispatch(experimentLog.appendExperimentLog(currentTab, createLog(`请求异常！${res.message}`, 'error')));
            dispatch(removeLoadingTab(currentTab));
            runnningTask.reset(currentTab);
        }
    }
}
/* 停止 */
export function stopRunningTask (data) {
    return (dispatch) => {
        console.log(data);
        const jobIdList = runnningTask.get(data.id);
        const stopJobs = [];
        for (const key in jobIdList) {
            if (jobIdList.hasOwnProperty(key)) {
                const element = jobIdList[key];
                stopJobs.push(element)
            }
        }
        api.stopJobList({ jobIdList: stopJobs })
        dispatch(experimentLog.appendExperimentLog(data.id, createLog(`实验[${data.name}]停止运行，请耐心等待……`, 'warning')));
    }
}
async function getRunningTaskStatus (tabId, tabData, jobIds, dispatch) {
    let response;
    try {
        response = await api.getRunTaskStatus({ values: jobIds });
    } catch (e) {
        return false;
    }
    tabData.graphData.forEach((item) => {
        if (item.vertex) {
            const jobStatus = response.data.find(o => o.jobId == item.data.jobId);
            if (jobStatus) {
                item.data.status = jobStatus.status
            }
        }
    })
    dispatch(changeContent(tabData, {}, false))
    resolveMsgExperiment(tabId, response, dispatch);
    if (response && response.code === 1) {
        /**
         * status.compeletedFinish 表示完全结束
         * status.handlerStatus 表示执行结果的状态
         */
        const status = isCompeletedFinish(response.data);
        if (status.compeletedFinish && status.handlerStatus) {
            // 成功
            resolveDataExperiment(tabId, response.data, null, dispatch);
        } else if (status.compeletedFinish && !status.handlerStatus) {
            // 失败
            rejectDataExperiment(tabId, response.data, tabData, dispatch);
        } else if (!status.compeletedFinish) {
            // 继续轮训
            // 获取当前正在运行的组件数据
            const object = tabData.graphData.filter(o => o.vertex && getRunningTask(response.data).includes(o.data.id + ''));
            outputExperimentStatus(tabId, object, dispatch)
            await new Promise(resolve => {
                setTimeout(() => {
                    resolve(getRunningTaskStatus(tabId, tabData, jobIds, dispatch));
                }, 1500)
            })
        }
    } else {
        dispatch(experimentLog.appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
        return false;
    }
}
