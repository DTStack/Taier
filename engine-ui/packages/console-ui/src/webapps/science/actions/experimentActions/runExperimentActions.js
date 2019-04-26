/* eslint-disable no-unreachable */
import { changeTab, changeTabSlient } from '../base/tab';
import { siderBarType, taskStatus, sqlExecStatus } from '../../consts';
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
            res = {
                code: 1,
                data: {
                    2510: 2510,
                    2511: 2511,
                    2512: 2512,
                    2513: 2513
                }
            }
        } catch (e) {
            return false;
        }
        if (res.code === 1) {
            runnningTask.set(currentTab, res.data);
            tabData.graphData.forEach((item) => {
                if (item.vertex) {
                    item.data.jobId = res.data[item.id]
                }
            })
            dispatch(changeContent(tabData, {}, false));
            await dispatch(getRunningTaskStatus(currentTab, tabData));
            dispatch(removeLoadingTab(currentTab));
            runnningTask.reset(currentTab);
        } else {
            dispatch(experimentLog.appendExperimentLog(currentTab, createLog(`请求异常！`, 'error')))
            return false;
        }
    }
}
/* 停止 */
export function stopRunningTask (data) {
    return (dispatch) => {
        const jobIdList = runnningTask.get(data.id);
        api.stopJobList({ jobIdList })
    }
}
function getRunningTaskStatus (tabId, tabData) {
    return async (dispatch, getState) => {
        if (!tabId) {
            tabId = parseInt(getState().experiment.currentTabIndex);
        }
        // TODO 请求接口
        const response = {
            code: 1,
            data: [{
                'taskId': 2510,
                'jobId': '2510',
                'status': 5
            }, {
                'taskId': 2511,
                'jobId': '2511',
                'status': 5
            }, {
                'taskId': 1,
                'jobId': '2512',
                'status': 8
            }, {
                'taskId': 1,
                'jobId': '2513',
                'status': 8
            }]
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
        // TODO 模拟的轮训
        resolveMsgExperiment(tabId, response, dispatch);
        if (response && response.code === 1) {
            let status = isCompeletedFinish(response.data);
            if (status.compeletedFinish && status.handlerStatus) {
                // 成功
                resolveDataExperiment(tabId, response.data, null, dispatch);
            } else if (status.compeletedFinish && !status.handlerStatus) {
                // 失败
                dispatch(experimentLog.appendExperimentLog(tabId, createLog(`节点执行失败`, 'error')))
                resolveDataExperiment(tabId, response.data, null, dispatch);
            } else if (!status.compeletedFinish) {
                // 继续轮训
                outputExperimentStatus(response.data.status, '.....', dispatch)
                dispatch(getRunningTaskStatus(tabId, tabData));
            }
        } else {
            dispatch(experimentLog.appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
            return false;
        }
    }
}
