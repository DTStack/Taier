import { cloneDeep, isEmpty, isObject } from "lodash";

export const convertRequestParams = (template = {}, params = {}) => {
    const loop = (template: any = {}, params: any = {}) => {
        const newData: any = {}
        Object.keys(template).forEach(k => {
            if (params[k] === undefined) {
                newData[k] = undefined;
                return;
            }
            const el = template[k];
            const d = cloneDeep(params[k]);
            if (isObject(el) && !isEmpty(el) && isObject(d)) {
                newData[k] = loop(el, d);
            } else {
                newData[k] = d
            }
        })
        return newData
    }
    return loop(template, params);
}
export const restfulDataPreview = {
    bodyStr: '',
    decode: '',
    fieldDelimiter: '',
    fields: '',
    headerStr: '',
    paramStr: '',
    requestInterval: 0,
    requestMode: '',
    url: '',
    protocol: ''
}

export const taskParams = {
    additionalResourceIdList: [],
    componentVersion: '',
    computeType: 0,
    createModel: 0,
    engineType: 0,
    exeArgs: '',
    forceUpdate: false,
    graphData: [],
    id: 1,
    isDeleted: 0,
    isDirtyDataManage: true,
    lockVersion: 1,
    mainClass: '',
    name: '',
    nodePid: 1,
    operatorJsons: '',
    preSave: false,
    readWriteLockVO: {
        getLock: false,
        gmtCreate: 0,
        gmtModified: 0,
        id: 0,
        isDeleted: 0,
        lastKeepLockUserName: 1,
        lockName: '',
        modifyUserId: 1,
        projectId: 1,
        relationId: 1,
        result: 1,
        type: '',
        version: 1
    },
    resourceIdList: [],
    settingMapStr: '',
    sideStr: '',
    sideParams: '',
    sinkStr: '',
    sinkParams: '',
    sourceStr: '',
    sourceList: [],
    sourceMapStr: '',
    sourceParams: '',
    sqlText: '',
    strategyId: 1,
    streamTaskDirtyDataManageVO: {
        id: 0,
        linkInfo: {},
        logPrintInterval: 0,
        maxCollectFailedRows: 0,
        maxRows: 0,
        outputType: '',
        taskId: 0
    },
    streamTaskRetry: {
        failRetry: 0,
        maxRetryNum: 3,
        retryInterval: 10,
        retryIntervalUnit: 0,
        submitExpired: 3,
        submitExpiredUnit: 0
    },
    targetMapStr: '',
    taskDesc: '',
    taskId: 1,
    taskParams: '',
    taskType: 0,
    updateSource: true,
    version: 0,
    whereToEnter: 0,
    createUserId: 0,
    projectId: 0,
    tenantId: 0,
    userId: 0
}
