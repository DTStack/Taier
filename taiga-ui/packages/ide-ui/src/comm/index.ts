/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { debounce, endsWith } from 'lodash';
import moment from 'moment';
import { browserHistory, hashHistory } from 'react-router';
import { createLogger } from 'redux-logger';
import thunkMiddleware from 'redux-thunk';
import { createStore, applyMiddleware, compose } from 'redux';
import { syncHistoryWithStore } from 'react-router-redux';
import { RDB_TYPE_ARRAY, ENGINE_SOURCE_TYPE, DATA_SOURCE } from './const';
import { Utils } from '@dtinsight/dt-utils';

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

/**
 * 过滤sql中的注释
 * @param {s} app
 */
export function filterComments (sql: string) {
    interface FilterParser {
        index: number;
        queue: string;
        comments: {
            begin: number;
            end: number;
        }[];
    }
    // 处理引号
    function quoteToken (parser: FilterParser, sql: string): string | undefined {
        const queue = parser.queue;
        const endsWith = queue[queue.length - 1];
        if (endsWith == '\'' || endsWith == '"') {
            const nextToken = sql.indexOf(endsWith, parser.index + 1);
            if (nextToken != -1) {
                parser.index = nextToken;
                parser.queue = '';
            } else {
                parser.index = sql.length - 1;
                parser.queue = '';
            }
        } else {
            return '';
        }
    }
    // 处理单行注释
    function singleLineCommentToken (parser: FilterParser, sql: string): string | undefined  {
        const queue = parser.queue;
        if (queue.endsWith('--')) {
            const nextToken = sql.indexOf('\n', parser.index + 1);
            const begin = parser.index - 1;
            if (nextToken != -1) {
                const end = nextToken - 1;
                parser.comments.push({
                    begin: begin,
                    end: end
                })
                parser.index = end;
                parser.queue = '';
            } else {
                parser.comments.push({
                    begin: begin,
                    end: sql.length - 1
                })
                parser.index = sql.length - 1;
                parser.queue = '';
            }
        } else {
            return '';
        }
    }
    // 处理多行注释
    function multipleLineCommentToken (parser: FilterParser, sql: string): string | undefined  {
        const queue = parser.queue;
        if (queue.endsWith('/*')) {
            const nextToken = sql.indexOf('*/', parser.index + 1);
            if (nextToken != -1) {
                parser.comments.push({
                    begin: parser.index - 1,
                    end: nextToken + 1
                })
                parser.index = nextToken;
                parser.queue = '';
            } else {
                parser.index = sql.length - 1;
                parser.queue = '';
            }
        } else {
            return '';
        }
    }
    const parser: FilterParser = {
        index: 0,
        queue: '',
        comments: []
    };
    for (parser.index = 0; parser.index < sql.length; parser.index++) {
        const char = sql[parser.index];
        parser.queue += char;
        const tokenFuncs = [quoteToken, singleLineCommentToken, multipleLineCommentToken];
        for (let i = 0; i < tokenFuncs.length; i++) {
            const err = tokenFuncs[i](parser, sql);
            if (err) {
                console.log(err);
                return null;
            }
        }
    }
    sql = replaceStrFormIndexArr(sql, ' ', parser.comments)
    return sql;
}

/**
 * 字符串替换（根据索引数组）
 */
export function replaceStrFormIndexArr (str: any, replaceStr: any, indexArr: any) {
    let result = '';
    let index = 0;

    if (!indexArr || indexArr.length < 1) {
        return str;
    }
    for (let i = 0; i < indexArr.length; i++) {
        const indexItem = indexArr[i];
        const begin = indexItem.begin;

        result = result + str.substring(index, begin) + replaceStr;
        index = indexItem.end + 1;

        if (i == indexArr.length - 1) {
            result = result + str.substring(index);
        }
    }

    return result;
}

/**
 * 分割sql
 * @param {String} sqlText
 */
export function splitSql (sqlText: string) {
    if (!sqlText) {
        return sqlText;
    }
    sqlText = sqlText.trim();
    if (!endsWith(sqlText, ';')) {
        sqlText += ';';
    }

    const results = [];
    let index = 0;
    let tmpChar = null;
    for (let i = 0; i < sqlText.length; i++) {
        const char = sqlText[i];

        if (char == "'" || char == '"') {
            if (tmpChar == char) {
                tmpChar = null;
            } else if (!tmpChar) {
                tmpChar = char;
            }
        } else if (char == ';') {
            if (tmpChar == null) {
                results.push(sqlText.substring(index, i));
                index = i + 1;
            }
        }
    }
    // 清空
    results.push(sqlText.substring(index, sqlText.length));

    return results.filter(Boolean);
}

export function filterSql (sql: any) {
    const arr: any = [];
    let sqls: any = filterComments(sql);

    // 如果有有效内容
    if (sqls) {
        sqls = splitSql(sqls);
    }

    if (sqls && sqls.length > 0) {
        for (let i = 0; i < sqls.length; i++) {
            const sql = sqls[i];
            const trimed = Utils.trim(sql);
            if (trimed !== '') {
                // 过滤语句前后空格
                arr.push(Utils.trimlr(sql));
            }
        }
    }
    return arr;
};
