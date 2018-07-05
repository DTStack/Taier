import moment from 'moment'
import utils from 'utils'
import {
    message
} from 'antd'

import API from "../../../api";
import {
    output, outputRes, removeLoadingTab
} from './sqlEditor'


const INTERVALS = 3000;
const EXCHANGE_STATUS = {
    4: "running",
    5: "success",
    8: "fail"
}
//储存各个tab的定时器id，用来stop任务时候清楚定时任务
const intervalsStore = {}
//停止信号量，stop执行成功之后，设置信号量，来让所有正在执行中的网络请求知道任务已经无需再继续
const stopSign = {}
//正在运行中的sql key，调用stop接口的时候需要使用
const runningSql = {}

function getUniqueKey(id) {
    return `${id}_${moment().valueOf()}`
}

function getDataOver(dispatch, currentTab, res, jobId) {
    if(res.data.result){
        dispatch(outputRes(currentTab, res.data.result,jobId))
    }
    dispatch(output(currentTab, '执行成功!'))
}

function doSelect(resolve, dispatch, jobId, currentTab) {
    API.selectSQLResultData({
        jobId: jobId
    })
        .then(
            (res) => {
                //获取到返回值
                if (res && res.message) dispatch(output(currentTab, `请求结果:\n ${res.message}`))
                //状态正常
                if (res && res.code === 1) {
                    switch (EXCHANGE_STATUS[res.data.status]) {
                        case "success": {
                            //成功
                            getDataOver(dispatch, currentTab, res, jobId)
                            resolve(true);
                            return;
                        }
                        case "running": {
                            //正常运行，则再次请求,并记录定时器id
                            intervalsStore[currentTab] = setTimeout(
                                () => {
                                    if (stopSign[currentTab]) {
                                        console.log("find stop sign in doSelect")
                                        stopSign[currentTab] = false;
                                        return;
                                    }
                                    dispatch(output(currentTab, `执行中.....`))
                                    doSelect(resolve, dispatch, jobId, currentTab)
                                }, INTERVALS
                            )
                            return;
                        }
                        case "fail":
                        default: {
                            //同失败
                            dispatch(removeLoadingTab(currentTab))
                            resolve(false)
                            return;
                        }
                    }
                } else {
                    dispatch(output(currentTab, `请求异常！`))
                    dispatch(removeLoadingTab(currentTab))
                    setTimeout(() => {
                    }, 0);
                    //不正常，则直接终止执行
                    resolve(false)
                    return;
                }
            }
        )
}

function selectData(dispatch, jobId, currentTab) {
    return new Promise(
        (resolve, reject) => {
            doSelect(resolve, dispatch, jobId, currentTab)
        }
    )
}

function exec(dispatch, currentTab, task, params, sqls, index) {
    const key = getUniqueKey(task.id)

    params.sql = `${sqls[index]}`
    params.uniqueKey = key
    dispatch(output(currentTab, `第${index + 1}条SQL开始执行`))
    function execContinue() {
        if (stopSign[currentTab]) {
            console.log("find stop sign in exec")
            stopSign[currentTab] = false;
            return;
        }
        exec(dispatch, currentTab, task, params, sqls, index + 1)
    }
    const succCall = (res) => {
        //假如已经是停止状态，则弃用结果
        if (stopSign[currentTab]) {
            console.log("find stop sign in succCall")
            stopSign[currentTab] = false;
            return;
        }
        if (res.message) dispatch(output(currentTab, `请求结果:\n ${res.message}`))
        //执行结束
        if (res.code != 1) {
            dispatch(removeLoadingTab(currentTab))
        }
        if (res.code === 1) {
            if (res.data.jobId) {
                runningSql[currentTab]=res.data.jobId;
                selectData(dispatch, res.data.jobId, currentTab)
                    .then(
                        (isSuccess) => {
                            if (index < sqls.length - 1 && isSuccess) {
                                //剩余任务，则继续执行
                                execContinue();
                            }
                            if (index >= sqls.length - 1) {
                                dispatch(removeLoadingTab(currentTab))
                            }
                        }
                    )
            } else {
                //不存在jobId，则直接返回结果
                getDataOver(dispatch, currentTab, res)
                if (index < sqls.length - 1) {
                    //剩余任务，则继续执行
                    execContinue();
                } else {
                    dispatch(removeLoadingTab(currentTab))
                }
            }

        }

    }
    if (utils.checkExist(task.taskType)) {// 任务执行
        params.taskId = task.id,
            API.execSQLImmediately(params).then(succCall)
    } else if (utils.checkExist(task.type)) { // 脚本执行
        params.scriptId = task.id,
            API.execScript(params).then(succCall)
    }
}
//执行sql
export function execSql(currentTab, task, params, sqls) {
    return (dispatch) => {
        stopSign[currentTab] = false;
        exec(dispatch, currentTab, task, params, sqls, 0);
    }
}
//停止sql
export function stopSql(currentTab, currentTabData, isSilent) {
    return (dispatch, getState) => {
        //静默关闭，不通知任何人（服务器，用户）
        if (isSilent) {
            const running = getState().sqlEditor.running;
            if (running.indexOf(currentTab) > -1) {
                stopSign[currentTab] = true;
                dispatch(output(currentTab, "执行停止"))
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
            if (res.code === 1) {
                dispatch(output(currentTab, "执行停止"))
                //消除轮询定时器
                if (intervalsStore[currentTab]) {
                    clearTimeout(intervalsStore[currentTab])
                    intervalsStore[currentTab] = null;
                }
                stopSign[currentTab] = true;
                dispatch(removeLoadingTab(currentTab))
                message.success('停止执行成功！')
            } else {
                message.success('停止执行失败！')
            }
        }

        if (utils.checkExist(currentTabData.taskType)) {// 任务执行
            API.stopSQLImmediately({
                taskId: currentTabData.id,
                jobId: jobId,
            }).then(succCall)
        } else if (utils.checkExist(currentTabData.type)) { // 脚本执行
            API.stopScript({
                scriptId: currentTabData.id,
                jobId: jobId,
            }).then(succCall)
        }
    }
}
