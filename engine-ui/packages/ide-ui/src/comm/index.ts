import { debounce } from 'lodash';
import moment from 'moment';
import { browserHistory, hashHistory } from 'react-router';
import { createLogger } from 'redux-logger';
import thunkMiddleware from 'redux-thunk';
import { createStore, applyMiddleware, compose } from 'redux';
import { syncHistoryWithStore } from 'react-router-redux';
import { RDB_TYPE_ARRAY, ENGINE_SOURCE_TYPE, DATA_SOURCE } from './const';

// 日志下载
export function createLinkMark( title: string, attrs: string) {
    return `[${title}]${JSON.stringify(attrs)}\n`;
}

/**
 * dtlog日志构造器
 * @param {string} log 日志内容
 * @param {string} type 日志类型
 */
export function createLog(log: string, type = '') {
    let now = moment().format('HH:mm:ss');
    if (process.env.NODE_ENV === 'test') {
        now = 'test';
    }
    return `[${now}] <${type}> ${log}`;
}

export function createTitle(title = '') {
    const baseLength = 15;
    const offsetLength = Math.floor((1.5 * title.length) / 2);
    const arr = new Array(Math.max(baseLength - offsetLength, 5));
    const wraptext = arr.join('=');
    return `${wraptext}${title}${wraptext}`;
}

// 请求防抖动
export function debounceEventHander(func: any, wait?: number, options?: any) {
    const debounced = debounce(func, wait, options);
    return function (e: any) {
        e.persist();
        return debounced(e);
    };
}

/**
 * 是否属于关系型数据源
 * @param {*} type
 */
export function isRDB(type: any) {
    return RDB_TYPE_ARRAY.indexOf(parseInt(type, 10)) > -1;
}

/**
 * 匹配自定义任务参数
 * @param {Array} taskCustomParams
 * @param {String} sqlText
 */
export function matchTaskParams(taskCustomParams: any, sqlText: any) {
    const regx = /\$\{([.\w]+)\}/g;
    const data: any = [];
    let res = null;
    while ((res = regx.exec(sqlText)) !== null) {
        const name = res[1];
        const param: any = {
            paramName: name,
            paramCommand: '',
        };
        const sysParam = taskCustomParams.find(
            (item: any) => item.paramName === name
        );
        if (sysParam) {
            param.type = 0;
            param.paramCommand = sysParam.paramCommand;
        } else {
            param.type = 1;
        }
        // 去重
        const exist = data.find((item: any) => name === item.paramName);
        if (!exist) {
            data.push(param);
        }
    }
    return data;
}

export function formatDateTime(timestap: string | number | Date) {
    return moment(timestap).format('YYYY-MM-DD HH:mm:ss');
}

export function checkExist(prop: any) {
    return prop !== undefined && prop !== null && prop !== '';
}

// Judge spark engine
export function isSparkEngine(engineType: any) {
    return ENGINE_SOURCE_TYPE.HADOOP === parseInt(engineType, 10);
}

// Judge libra engine
export function isLibraEngine(engineType: any) {
    return ENGINE_SOURCE_TYPE.LIBRA === parseInt(engineType, 10);
}

export function isOracleEngine(engineType: any) {
    return ENGINE_SOURCE_TYPE.ORACLE === parseInt(engineType, 10);
}

export function isGreenPlumEngine(engineType: any) {
    return ENGINE_SOURCE_TYPE.GREEN_PLUM === parseInt(engineType, 10);
}

/**
 * 是否为HDFS类型
 * @param {*} type
 */
export function isHdfsType(type: any) {
    return DATA_SOURCE.HDFS === parseInt(type, 10);
}

/**
 * Judge tiDB engine
 * @param engineType any
 */
export function isTiDBEngine(engineType: any) {
    return ENGINE_SOURCE_TYPE.TI_DB === parseInt(engineType, 10);
}
export function formJsonValidator(rule: any, value: any, callback: any) {
    let msg: any;
    try {
        if (value) {
            const t = JSON.parse(value);
            if (typeof t !== 'object') {
                msg = '请填写正确的JSON';
            }
        }
    } catch (e) {
        msg = '请检查JSON格式，确认无中英文符号混用！';
    } finally {
        callback(msg);
    }
}

declare let window: any;

function configureStoreDev(rootReducer: any) {
    const store = createStore(
        rootReducer,
        compose(
            applyMiddleware(thunkMiddleware, createLogger()),
            window.devToolsExtension
                ? window.devToolsExtension()
                : (fn: any) => fn
        )
    );
    return store;
}

function configureStoreProd(rootReducer: any) {
    const stroe = createStore(rootReducer, applyMiddleware(thunkMiddleware));
    return stroe;
}

/**
 *
 * @param { Object } rootReducer
 * @param { String } routeMode [hash, browser]
 */
export function getStore(rootReducer: any, routeMode?: any) {
    const store =
        process.env.NODE_ENV === 'production'
            ? configureStoreProd(rootReducer)
            : configureStoreDev(rootReducer);
    const bhistory =
        !routeMode || routeMode !== 'hash' ? browserHistory : hashHistory;
    const history = syncHistoryWithStore(bhistory, store);
    return {
        store,
        history,
    };
}
