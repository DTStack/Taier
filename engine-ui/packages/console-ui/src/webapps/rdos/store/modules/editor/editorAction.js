import moment from 'moment'
import utils from 'utils'

import API from '../../../api';
import { taskStatus, offlineTaskStatusFilter, TASK_TYPE, TASK_STATUS } from '../../../comm/const'
import { editorAction } from './actionTypes';
import { createLinkMark, createLog, createTitle } from 'widgets/code-editor/utils'

const INTERVALS = 1500;

// 储存各个tab的定时器id，用来stop任务时候清楚定时任务
const intervalsStore = {}
// 停止信号量，stop执行成功之后，设置信号量，来让所有正在执行中的网络请求知道任务已经无需再继续
const stopSign = {}
// 正在运行中的sql key，调用stop接口的时候需要使用
const runningSql = {}

function getUniqueKey (id) {
    return `${id}_${moment().valueOf()}`
}

function typeCreate (status) {
    return status == taskStatus.FAILED ? 'error' : 'info';
}

function getDataOver (dispatch, currentTab, res, jobId) {
    dispatch(output(currentTab, createLog('执行完成!', 'info')))
    if (res.data.result) {
        dispatch(outputRes(currentTab, res.data.result, jobId))
    }
    if (res.data && res.data.download) {
        dispatch(output(currentTab, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
    }
}

/**
 * 获取执行结果
 * @param {*} resolve
 * @param {*} dispatch
 * @param {*} jobId
 * @param {*} currentTab
 * @param {*} taskType 任务类型
 */
function doSelect (resolve, dispatch, jobId, currentTab, taskType) {
    function outputStatus (status, extText) {
        // 当为数据同步日志时，运行日志就不显示了
        if (taskType === TASK_TYPE.SYNC && status === TASK_STATUS.RUNNING) {
            return;
        }
        for (let i = 0; i < offlineTaskStatusFilter.length; i++) {
            if (offlineTaskStatusFilter[i].value == status) {
                dispatch(output(currentTab, createLog(`${offlineTaskStatusFilter[i].text}${extText || ''}`, 'info')))
                continue;
            }
        }
    }
    API.selectExecResultData({
        jobId: jobId
    }, taskType)
        .then(
            (res) => {
                if (res && res.code) {
                    // 获取到返回值
                    if (res && res.message) dispatch(output(currentTab, createLog(`${res.message}`, 'error')))
                    if (res && res.data && res.data.msg) dispatch(output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))))
                }
                // 状态正常
                if (res && res.code === 1) {
                    switch (res.data.status) {
                        case taskStatus.FINISHED: {
                            // 成功
                            getDataOver(dispatch, currentTab, res, jobId)
                            dispatch(removeLoadingTab(currentTab))
                            resolve(true);
                            return;
                        }
                        case taskStatus.FAILED:
                        case taskStatus.CANCELED: {
                            outputStatus(res.data.status)
                            if (res.data && res.data.download) {
                                dispatch(output(currentTab, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
                            }
                            dispatch(removeLoadingTab(currentTab))
                            resolve(false)
                            return;
                        }
                        default: {
                            // 正常运行，则再次请求,并记录定时器id
                            intervalsStore[currentTab] = setTimeout(
                                () => {
                                    if (stopSign[currentTab]) {
                                        console.log('find stop sign in doSelect')
                                        stopSign[currentTab] = false;
                                        return;
                                    }
                                    outputStatus(res.data.status, '.....')
                                    doSelect(resolve, dispatch, jobId, currentTab, taskType)
                                }, INTERVALS
                            )
                        }
                    }
                } else {
                    dispatch(output(currentTab, createLog(`请求异常！`, 'error')))
                    dispatch(removeLoadingTab(currentTab))
                    // 不正常，则直接终止执行
                    resolve(false)
                }
            }
        )
}

function selectData (dispatch, jobId, currentTab, taskType) {
    return new Promise(
        (resolve, reject) => {
            doSelect(resolve, dispatch, jobId, currentTab, taskType)
        }
    )
}

function exec (dispatch, currentTab, task, params, sqls, index, resolve, reject) {
    const key = getUniqueKey(task.id)

    params.sql = `${sqls[index]}`
    params.uniqueKey = key
    dispatch(output(currentTab, createLog(`第${index + 1}条任务开始执行`, 'info')))
    dispatch(output(currentTab, `${createTitle('任务信息')}\n${params.sql}\n${createTitle('')}`))
    function execContinue () {
        if (stopSign[currentTab]) {
            console.log('find stop sign in exec')
            stopSign[currentTab] = false;
            return;
        }
        exec(dispatch, currentTab, task, params, sqls, index + 1, resolve, reject)
    }
    const succCall = (res) => {
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
            if (res.data && res.data.msg) dispatch(output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))))

            if (res.data.jobId) {
                runningSql[currentTab] = res.data.jobId;

                selectData(dispatch, res.data.jobId, currentTab)
                    .then(
                        (isSuccess) => {
                            if (index < sqls.length - 1 && isSuccess) {
                                // 剩余任务，则继续执行
                                execContinue();
                            }
                            if (index >= sqls.length - 1) {
                                dispatch(removeLoadingTab(currentTab))
                                resolve(true)
                            }
                        }
                    )
            } else {
                // 不存在jobId，则直接返回结果
                getDataOver(dispatch, currentTab, res)
                if (index < sqls.length - 1) {
                    // 剩余任务，则继续执行
                    execContinue();
                } else {
                    dispatch(removeLoadingTab(currentTab))
                    resolve(true)
                }
            }
        }
    }
    if (utils.checkExist(task.taskType)) { // 任务执行
        params.taskId = task.id;
        API.execSQLImmediately(params).then(succCall);
    } else if (utils.checkExist(task.type)) { // 脚本执行
        params.scriptId = task.id;
        API.execScript(params).then(succCall);
    }
}

// 执行任务
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
    return (dispatch, getState) => {
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
        const jobId = runningSql[currentTab]
        if (!jobId) return
        const succCall = res => {
            /**
             * 目前执行停止之后还需要继续轮训后端状态，所以停止方法调用成功也不主动执行停止操作，而且根据后续轮训状态来执行停止操作
             */
        }

        if (utils.checkExist(currentTabData.taskType)) { // 任务执行
            API.stopSQLImmediately({
                taskId: currentTabData.id,
                jobId: jobId
            }).then(succCall)
        } else if (utils.checkExist(currentTabData.type)) { // 脚本执行
            API.stopScript({
                scriptId: currentTabData.id,
                jobId: jobId
            }).then(succCall)
        }
    }
}

/**
 * 执行数据同步任务
 */
export function execDataSync (currentTab, params) {
    return async dispatch => {
        dispatch(setOutput(currentTab, `同步任务【${params.name}】开始执行`));
        dispatch(addLoadingTab(currentTab));
        const res = await API.execDataSyncImmediately(params);
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
            dispatch(output(currentTab, createLog(`已经成功发送执行请求...`, 'info')))
            if (res.data && res.data.msg) dispatch(output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))))
            if (res.data.jobId) {
                runningSql[currentTab] = res.data.jobId;
                selectData(dispatch, res.data.jobId, currentTab, TASK_TYPE.SYNC);
            } else {
                dispatch(output(currentTab, createLog(`执行返回结果异常`, 'error')))
                dispatch(removeLoadingTab(currentTab))
            }
        }
    }
}

/**
 * 停止数据同步任务
 */
export function stopDataSync (currentTab, isSilent) {
    return async (dispatch, getState) => {
        stopSign[currentTab] = true;
        // 静默关闭，不通知任何人（服务器，用户）
        if (isSilent) {
            const running = getState().editor.running;
            if (running.indexOf(currentTab) > -1) {
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
        const jobId = runningSql[currentTab]
        if (!jobId) return

        const res = await API.stopDataSyncImmediately({ jobId: jobId });
        if (res && res.code === 1) {
            dispatch(removeLoadingTab(currentTab))
            dispatch(output(currentTab, createLog('执行停止', 'warning')))
        }
    }
}

// Actions
export function output (tab, log) {
    return {
        type: editorAction.APPEND_CONSOLE_LOG,
        data: log,
        key: tab
    }
}

export function setOutput (tab, log) {
    return {
        type: editorAction.SET_CONSOLE_LOG,
        data: createLog(log, 'info'),
        key: tab
    }
}

export function outputRes (tab, item, jobId) {
    return {
        type: editorAction.UPDATE_RESULTS,
        data: { jobId: jobId, data: item },
        key: tab
    }
}

export function removeRes (tab, index) {
    return {
        type: editorAction.DELETE_RESULT,
        data: index,
        key: tab
    }
}

export function resetConsole (tab) {
    return {
        type: editorAction.RESET_CONSOLE,
        key: tab
    }
}

/**
 * 初始化tab的console对象
 * @param {tabId} key
 */
export function getTab (key) {
    return {
        type: editorAction.GET_TAB,
        key
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
