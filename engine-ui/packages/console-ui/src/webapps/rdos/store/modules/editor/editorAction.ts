import moment from 'moment'
import utils from 'utils'

import API from '../../../api';
import { taskStatus, offlineTaskStatusFilter, TASK_TYPE, TASK_STATUS, ENGINE_SOURCE_TYPE } from '../../../comm/const'
import { editorAction } from './actionTypes';
import { createLinkMark, createLog, createTitle } from 'widgets/code-editor/utils'

const INTERVALS = 1500;

// 储存各个tab的定时器id，用来stop任务时候清楚定时任务
const intervalsStore: any = {}
// 停止信号量，stop执行成功之后，设置信号量，来让所有正在执行中的网络请求知道任务已经无需再继续
const stopSign: any = {}
// 正在运行中的sql key，调用stop接口的时候需要使用
const runningSql: any = {}

function getUniqueKey (id: any) {
    return `${id}_${moment().valueOf()}`
}

function typeCreate (status: any) {
    return status == taskStatus.FAILED ? 'error' : 'info';
}

function getDataOver (dispatch: any, currentTab: any, res: any, jobId?: any) {
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
function doSelect (resolve: any, dispatch: any, jobId: any, currentTab: number, task: any, taskType: number, retryTimes: number = 1) {
    function outputStatus (status: any, extText?: any) {
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
        jobId: jobId,
        taskId: task.id
    }, taskType)
        .then(
            (res: any) => {
                if (stopSign[currentTab]) {
                    console.log('find stop sign in doSelect')
                    stopSign[currentTab] = false;
                    resolve(false)
                    return;
                }
                if (res && res.code) {
                    // 获取到返回值
                    if (res && res.message) {
                        dispatch(output(currentTab, createLog(`${res.message}`, 'error')))
                    }
                    if (res && res.data && res.data.msg) {
                        dispatch(output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))))
                    }
                }
                // 状态正常
                if (res && res.code === 1) {
                    switch (res.data.status) {
                        case taskStatus.FINISHED: {
                            // 成功
                            getDataOver(dispatch, currentTab, res, jobId)
                            resolve(true);
                            return;
                        }
                        case taskStatus.FAILED:
                        case taskStatus.CANCELED: {
                            outputStatus(res.data.status)
                            if (res.data && res.data.download) {
                                dispatch(output(currentTab, `完整日志下载地址：${createLinkMark({ href: res.data.download, download: '' })}\n`))
                            }
                            resolve(false)
                            return;
                        }
                        default: {
                            // 正常运行，则再次请求,并记录定时器id
                            intervalsStore[currentTab] = setTimeout(
                                () => {
                                    outputStatus(res.data.status, '.....')
                                    doSelect(resolve, dispatch, jobId, currentTab, task, taskType)
                                }, INTERVALS
                            )
                        }
                    }
                } else {
                    dispatch(output(currentTab, createLog(`请求异常！`, 'error')))
                    if (retryTimes <= 3) { // 默认出现服务器异常的情况下，发起重试请求，则重试次数不超过3次
                        dispatch(output(currentTab, createLog(`正在尝试第${retryTimes}次重试`, 'info')))
                        intervalsStore[currentTab] = setTimeout(// 重试间隔时间3s
                            () => {
                                doSelect(resolve, dispatch, jobId, currentTab, task, taskType, retryTimes + 1)
                            }, 3000
                        )
                    } else {
                        // 则直接终止执行
                        resolve(false)
                    }
                }
            }
        )
}

function selectData (dispatch: any, jobId: any, currentTab: number, task: any, taskType?: number) {
    return new Promise(
        (resolve: any, reject: any) => {
            doSelect(resolve, dispatch, jobId, currentTab, task, taskType)
        }
    )
}

/**
 * 执行一系列sql任务
 * @param {dispath} dispatch redux dispatch
 * @param {index} currentTab 当前tabID
 * @param {Task} task 任务对象
 * @param {Params} params 额外参数
 * @param {Array} sqls 要执行的Sql数组
 * @param {Int} index 当前执行的数组下标
 * @param {function} resolve promise resolve
 * @param {function} reject promise reject
 */
function exec (dispatch: any, currentTab: number, task: any, params: any, sqls: any, index: any, resolve: any, reject: any) {
    const key = getUniqueKey(task.id)

    params.sql = `${sqls[index]}`
    params.uniqueKey = key
    dispatch(output(currentTab, createLog(`第${index + 1}条任务开始执行`, 'info')))
    // 判断是否要继续执行SQL
    function judgeIfContinueExec () {
        if (index < sqls.length - 1) {
            // 剩余任务，则继续执行
            execContinue();
        } else {
            dispatch(removeLoadingTab(currentTab))
            resolve(true)
        }
    }
    function execContinue () {
        if (stopSign[currentTab]) {
            console.log('find stop sign in exec')
            stopSign[currentTab] = false;
            dispatch(removeLoadingTab(currentTab))
            return;
        }
        exec(dispatch, currentTab, task, params, sqls, index + 1, resolve, reject)
    }
    const succCall = (res: any) => {
        // 假如已经是停止状态，则弃用结果
        if (stopSign[currentTab]) {
            console.log('find stop sign in succCall')
            stopSign[currentTab] = false;
            dispatch(removeLoadingTab(currentTab))
            return;
        }
        if (res && res.code && res.message) dispatch(output(currentTab, createLog(`${res.message}`, 'error')))
        // 执行结束
        if (!res || (res && res.code != 1)) {
            dispatch(output(currentTab, createLog(`请求异常！`, 'error')))
            dispatch(removeLoadingTab(currentTab))
            resolve(true)
            return;
        }
        if (res && res.code === 1) {
            if (res.data && res.data.msg) dispatch(output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))))
            // 在立即执行sql成功后，显示转化之后的任务信息(sqlText)
            if (res.data && res.data.sqlText) dispatch(output(currentTab, `${createTitle('任务信息')}\n${res.data.sqlText}\n${createTitle('')}`))
            if (res.data.jobId) {
                runningSql[currentTab] = res.data.jobId;
                if (res.data.engineType == ENGINE_SOURCE_TYPE.LIBRA) {
                    getDataOver(dispatch, currentTab, res, res.data.jobId) // libra不去轮训selectData接口，直接返回数据
                    judgeIfContinueExec();
                } else {
                    selectData(dispatch, res.data.jobId, currentTab, task)
                        .then(
                            (isSuccess: any) => {
                                if (index < sqls.length - 1 && isSuccess) {
                                    execContinue();
                                } else {
                                    dispatch(removeLoadingTab(currentTab))
                                    resolve(true)
                                }
                            }
                        )
                }
            } else {
                // 不存在jobId，则直接返回结果
                getDataOver(dispatch, currentTab, res);
                // 判断是否继续执行
                judgeIfContinueExec();
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
export function execSql (currentTab: any, task: any, params: any, sqls: any) {
    return (dispatch: any) => {
        stopSign[currentTab] = false;
        return new Promise((resolve: any, reject: any) => {
            dispatch(addLoadingTab(currentTab));
            exec(dispatch, currentTab, task, params, sqls, 0, resolve, reject);
        })
    }
}

// 停止sql
export function stopSql (currentTab: any, currentTabData: any, isSilent: any) {
    return (dispatch: any, getState: any) => {
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
        const succCall = (res: any) => {
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
export function execDataSync (currentTab: any, params: any) {
    return async (dispatch: any) => {
        stopSign[currentTab] = false;
        dispatch(setOutput(currentTab, `同步任务【${params.name}】开始执行`));
        dispatch(addLoadingTab(currentTab));
        const res = await API.execDataSyncImmediately(params);
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
                selectData(dispatch, res.data.jobId, currentTab, params, TASK_TYPE.SYNC).then(() => {
                    dispatch(removeLoadingTab(currentTab))
                });
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
export function stopDataSync (currentTab: any, isSilent: any) {
    return async (dispatch: any, getState: any) => {
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

        const res = await API.stopDataSyncImmediately({ jobId: jobId });
        if (res && res.code === 1) {
            dispatch(removeLoadingTab(currentTab))
            dispatch(output(currentTab, createLog('执行停止', 'warning')))
        }
    }
}

// Actions
export function output (tab: any, log: any) {
    return {
        type: editorAction.APPEND_CONSOLE_LOG,
        data: log,
        key: tab
    }
}

export function setOutput (tab: any, log: any) {
    return {
        type: editorAction.SET_CONSOLE_LOG,
        data: createLog(log, 'info'),
        key: tab
    }
}

export function outputRes (tab: any, item: any, jobId: any) {
    return {
        type: editorAction.UPDATE_RESULTS,
        data: { jobId: jobId, data: item },
        key: tab
    }
}

export function removeRes (tab: any, index: any) {
    return {
        type: editorAction.DELETE_RESULT,
        data: index,
        key: tab
    }
}

export function resetConsole (tab: any) {
    return {
        type: editorAction.RESET_CONSOLE,
        key: tab
    }
}

/**
 * 初始化tab的console对象
 * @param {tabId} key
 */
export function getTab (key: any) {
    return {
        type: editorAction.GET_TAB,
        key
    }
}

export function setSelectionContent (data: any) {
    return {
        type: editorAction.SET_SELECTION_CONTENT,
        data
    }
}

// Loading actions
export function addLoadingTab (id: any) {
    return {
        type: editorAction.ADD_LOADING_TAB,
        data: {
            id: id
        }
    }
}
export function removeLoadingTab (id: any) {
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

export function updateEditorOptions (data: any) {
    return {
        type: editorAction.UPDATE_OPTIONS,
        data
    }
}

/**
 * 更新右侧面板行为
 * @param {String} showAction 展示行为
 */
export function showRightTablePane () {
    return {
        type: editorAction.SHOW_RIGHT_PANE,
        data: editorAction.SHOW_TABLE_TIP_PANE
    }
}

export function showRightSyntaxPane () {
    return {
        type: editorAction.SHOW_RIGHT_PANE,
        data: editorAction.SHOW_SYNTAX_HELP_PANE
    }
}

export function hideRightPane () {
    return {
        type: editorAction.SHOW_RIGHT_PANE,
        data: ''
    }
}

export function updateSyntaxPane (data: any) {
    return {
        type: editorAction.UPDATE_SYNTAX_PANE,
        data: data
    }
}

export function getEditorThemeClassName (editorTheme: any) {
    // 如果是dark类的编辑器，则切换ide的theme为dark风格
    return editorTheme === 'vs-dark' || editorTheme === 'hc-black'
        ? 'theme-dark' : 'theme-white';
}
