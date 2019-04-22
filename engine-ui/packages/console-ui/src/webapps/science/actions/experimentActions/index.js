import { changeTab, changeTabSlient, addTab, setCurrentTab } from '../base/tab';
import { message } from 'antd';
import { loadTreeData } from '../base/fileTree';
import { siderBarType, consoleKey, TASK_STATUS, sqlExecStatus } from '../../consts';
import editorAction from '../../consts/editorActionType';
import { setOutput, output, outputRes, addLoadingTab, removeLoadingTab } from '../editorActions';
import { createLinkMark, createLog } from 'widgets/code-editor/utils'
import api from '../../api/experiment';
import fileApi from '../../api/fileTree';
import { cloneDeep } from 'lodash';

const mockData = require('./mocks/data.json');
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
 *
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
export function changeText (text, tab) {
    return changeContent({
        sqlText: text
    }, tab)
}

export function addExperiment (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.addExperiment(params);
            if (res && res.code == 1) {
                message.success('新建成功');
                dispatch(loadTreeData(siderBarType.experiment, params.nodePid))
                resolve(res);
            }
        })
    }
}
export function deleteExperiment (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.deleteExperiment(params);
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.experiment, params.parentId))
                resolve(res);
            }
        })
    }
}

export function deleteExperimentFolder (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await fileApi.deleteFolder(params);
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.experiment, params.parentId))
                resolve(res);
            }
        })
    }
}
export function getTaskData (data, currentTab) {
    return (dispatch, getState) => {
        // TODO 请求接口
        const res = mockData;
        if (res && res.code == 1) {
            const graphData = res.data ? JSON.parse(res.data.sqlText) : [];
            data.graphData = graphData;
            dispatch(changeContent(data, {}, false))
        }
    }
}
export function updateTaskData (oldData, newData) {
    return (dispatch, getState) => {
        dispatch(changeContent(oldData, newData, true, true))
    }
}

export function openExperiment (id) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.openExperiment({ id });
            if (res && res.code == 1) {
                dispatch(addTab(siderBarType.experiment, res.data));
                dispatch(setCurrentTab(siderBarType.experiment, id));
                resolve(res);
            }
        })
    }
}
export function saveExperiment (tabData) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.addExperiment(tabData);
            if (res && res.code == 1) {
                dispatch(changeContent(res.data, tabData, false));
                message.success('保存成功！')
                resolve(res);
            }
        })
    }
}

export function copyCell (tabData, copyCell) {
    return (dispatch) => {
        const data = cloneDeep(tabData);
        // TODO
        const res = {
            code: 1,
            data: {
                ...copyCell,
                id: 1111
            }
        }
        if (res.code == 1) {
            data.graphData.push(res.data);
            dispatch(changeContent(data, {}, true));
        }
    }
}

export function getRunTaskList (tabData, taskId, type, currentTab) {
    return async (dispatch, getState) => {
        console.log(currentTab, 'currentTab')
        dispatch(addLoadingTab(currentTab));
        dispatch(setExperimentLog(currentTab, `正在运行...`));
        dispatch(showExperimentLog(currentTab));
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
export function getRunningTaskStatus (jobIds, tabId, tabData) {
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
                        dispatch(appendExperimentLog(tabId, `完整日志下载地址：${createLinkMark({ href: response.data.download, download: '' })}\n`))
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
            dispatch(appendExperimentLog(tabId, createLog(`请求异常！`, 'error')))
            return false;
        }
    }
}
function outputExperimentStatus (tabId, status, dispatch) {
    dispatch(appendExperimentLog(tabId, createLog(`${status}.....`, 'info')))
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

function changeConsoleKey (tabId, activeKey) {
    return {
        type: editorAction.CHANGE_TABS_KEY,
        payload: {
            tabId,
            activeKey,
            siderType: siderBarType.experiment
        }
    }
}

export function showExperimentLog (tabId) {
    return changeConsoleKey(tabId, consoleKey);
}

export function setExperimentLog (tabId, log) {
    return setOutput(tabId, log, consoleKey, siderBarType.experiment, {
        name: '日志',
        disableClose: true
    });
}

export function appendExperimentLog (tabId, log) {
    return output(tabId, log, consoleKey, siderBarType.experiment);
}

export function setExperimentResult (tabId, jobId, data) {
    return outputRes(tabId, data, jobId, siderBarType.experiment, {
        name: '执行结果'
    })
}
