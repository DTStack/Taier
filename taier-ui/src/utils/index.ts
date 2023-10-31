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

import { Utils } from '@dtinsight/dt-utils';
import type { languages } from '@dtinsight/molecule/esm/monaco';
import { endsWith, get, pickBy, range as lodashRange } from 'lodash';
import moment from 'moment';
import { history } from 'umi';

import { updateDrawer } from '@/components/customDrawer';
import {
    FAILED_STATUS,
    FINISH_STATUS,
    FROZEN_STATUS,
    PARENTFAILED_STATUS,
    RUN_FAILED_STATUS,
    RUNNING_STATUS,
    STOP_STATUS,
    SUBMITTING_STATUS,
    TASK_STATUS,
    WAIT_STATUS,
} from '@/constant';
import { taskRenderService } from '@/services';
import { Keywords, Snippets } from './completion';

/**
 * 返回今日 [00:00:00, 23:59:69]
 */
export function getTodayTime(date?: moment.Moment) {
    return [
        moment(date).set({
            hour: 0,
            minute: 0,
            second: 0,
        }),
        moment(date).set({
            hour: 23,
            minute: 59,
            second: 59,
        }),
    ] as const;
}

export function getCookie(name: string) {
    const arr = document.cookie.match(new RegExp(`(^| )${name}=([^;]*)(;|$)`));
    if (arr != null) {
        return unescape(decodeURI(arr[2]));
    }
    return null;
}

export function deleteCookie(name: string, domain?: string, path = '/') {
    const d = new Date(0);
    const cookieDomain = domain ? `; domain=${domain}` : '';
    document.cookie = `${name}=; expires=${d.toUTCString()}${cookieDomain}; path=${path}`;
}

/**
 * 格式化时间为 `YYYY-MM-DD HH:mm:ss`
 */
export function formatDateTime(timestap: string | number | Date) {
    return moment(timestap).format('YYYY-MM-DD HH:mm:ss');
}

export function checkExist(prop: any) {
    return prop !== undefined && prop !== null && prop !== '';
}

interface FilterParser {
    index: number;
    queue: string;
    comments: {
        begin: number;
        end: number;
    }[];
}
/**
 * 过滤sql中的注释
 */
export function filterComments(rawSql: string) {
    // 处理引号
    function quoteToken(parser: FilterParser, sql: string): string | undefined {
        const { queue } = parser;
        const lastItem = queue[queue.length - 1];
        if (lastItem === "'" || lastItem === '"') {
            const nextToken = sql.indexOf(lastItem, parser.index + 1);
            if (nextToken !== -1) {
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
    function singleLineCommentToken(parser: FilterParser, sql: string): string | undefined {
        const { queue } = parser;
        if (queue.endsWith('--')) {
            const nextToken = sql.indexOf('\n', parser.index + 1);
            const begin = parser.index - 1;
            if (nextToken !== -1) {
                const end = nextToken - 1;
                parser.comments.push({
                    begin,
                    end,
                });
                parser.index = end;
                parser.queue = '';
            } else {
                parser.comments.push({
                    begin,
                    end: sql.length - 1,
                });
                parser.index = sql.length - 1;
                parser.queue = '';
            }
        } else {
            return '';
        }
    }

    // 处理多行注释
    function multipleLineCommentToken(parser: FilterParser, sql: string): string | undefined {
        const { queue } = parser;
        if (queue.endsWith('/*')) {
            const nextToken = sql.indexOf('*/', parser.index + 1);
            if (nextToken !== -1) {
                parser.comments.push({
                    begin: parser.index - 1,
                    end: nextToken + 1,
                });
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
        comments: [],
    };

    for (parser.index = 0; parser.index < rawSql.length; parser.index += 1) {
        const char = rawSql[parser.index];
        parser.queue += char;
        const tokenFuncs = [quoteToken, singleLineCommentToken, multipleLineCommentToken];
        for (let i = 0; i < tokenFuncs.length; i += 1) {
            const err = tokenFuncs[i](parser, rawSql);
            if (err) {
                return null;
            }
        }
    }
    const sql = replaceStrFormIndexArr(rawSql, ' ', parser.comments);
    return sql;
}

/**
 * 字符串替换（根据索引数组）
 */
export function replaceStrFormIndexArr(str: string, replaceStr: string, indexArr: FilterParser['comments']) {
    let result = '';
    let index = 0;

    if (!indexArr || indexArr.length < 1) {
        return str;
    }
    for (let i = 0; i < indexArr.length; i += 1) {
        const indexItem = indexArr[i];
        const { begin } = indexItem;

        result = result + str.substring(index, begin) + replaceStr;
        index = indexItem.end + 1;

        if (i === indexArr.length - 1) {
            result += str.substring(index);
        }
    }

    return result;
}

/**
 * 分割sql
 * @param {String} sqlText
 */
export function splitSql(rawSqlText: string) {
    let sqlText = rawSqlText;
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
    for (let i = 0; i < sqlText.length; i += 1) {
        const char = sqlText[i];

        if (char === "'" || char === '"') {
            if (tmpChar === char) {
                tmpChar = null;
            } else if (!tmpChar) {
                tmpChar = char;
            }
        } else if (char === ';') {
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

export function filterSql(sql: string) {
    const arr: string[] = [];
    let sqls: string | string[] | null = filterComments(sql);

    // 如果有有效内容
    if (sqls) {
        sqls = splitSql(sqls);
    }

    if (sqls && sqls.length > 0) {
        for (let i = 0; i < sqls.length; i += 1) {
            const sqlText = sqls[i];
            const trimed = Utils.trim(sqlText);
            if (trimed !== '') {
                // 过滤语句前后空格
                arr.push(Utils.trim(sqlText));
            }
        }
    }
    return arr;
}

export const getTenantId = () => {
    return getCookie(`tenantId`);
};

export const getUserId = () => {
    return getCookie('userId');
};

function isUtf8(s: string) {
    const lastnames = ['ä', 'å', 'æ', 'ç', 'è', 'é'];
    for (let i = 0; i < lastnames.length; i += 1) {
        if (s && s.indexOf(lastnames[i]) > -1) {
            return false;
        }
    }
    return true;
}

export const utf16to8 = (str: string) => {
    if (typeof str !== 'string') return str;
    if (!isUtf8(str)) return str;
    let out = '';
    const len = str.length || 0;
    for (let i = 0; i < len; i += 1) {
        const c = str.charCodeAt(i);
        if (c >= 0x0001 && c <= 0x007f) {
            out += str.charAt(i);
        } else if (c > 0x07ff) {
            out += String.fromCharCode(0xe0 | ((c >> 12) & 0x0f));
            out += String.fromCharCode(0x80 | ((c >> 6) & 0x3f));
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3f));
        } else {
            out += String.fromCharCode(0xc0 | ((c >> 6) & 0x1f));
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3f));
        }
    }
    return out;
};

export const utf8to16 = (str: string) => {
    if (typeof str !== 'string') return str;
    if (isUtf8(str)) return str;
    let out = '';
    const len = str.length;
    let char2;
    let char3;
    let i = 0;
    while (i < len) {
        const c = str.charCodeAt(i);
        i += 1;
        switch (c >> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                out += str.charAt(i - 1);
                break;
            case 12:
            case 13:
                // 110x xxxx 10xx xxxx
                char2 = str.charCodeAt(i);
                i += 1;
                out += String.fromCharCode(((c & 0x1f) << 6) | (char2 & 0x3f));
                break;
            case 14:
                // 1110 xxxx 10xx xxxx 10xx xxxx
                char2 = str.charCodeAt(i);
                i += 1;
                char3 = str.charCodeAt(i);
                i += 1;
                out += String.fromCharCode(((c & 0x0f) << 12) | ((char2 & 0x3f) << 6) | ((char3 & 0x3f) << 0));
                break;
            default:
        }
    }
    return out;
};

export function goToTaskDev(record: { id: string | number; [key: string]: any }) {
    const { id } = record ?? {};
    // Open task in tab
    taskRenderService.openTask({ id: id.toString() });
    // Clear history query
    history.push({
        query: {},
    });
    // Close drawer
    updateDrawer({ id: 'root', visible: false, renderContent: () => null });
    // clear popupMenu
    removePopUpMenu();
}

/**
 * 从 document.body 隐藏 mxGraph 所产生的 popup
 */
export const removePopUpMenu = () => {
    const remove = () => {
        const tips = document.querySelectorAll<HTMLDivElement>('.mxPopupMenu');
        if (tips) {
            tips.forEach((o) => {
                o.style.visibility = 'hidden';
            });
        }
    };
    setTimeout(remove, 500);
};

export function getVertexStyle(type: TASK_STATUS): string {
    // 成功
    if (FINISH_STATUS.includes(type)) {
        return 'whiteSpace=wrap;fillColor=var(--badge-finished-background);strokeColor=var(--badge-finished-border);';
    }

    // 运行中
    if (RUNNING_STATUS.includes(type)) {
        return 'whiteSpace=wrap;fillColor=var(--badge-running-background);strokeColor=var(--badge-running-border);';
    }

    // 等待提交/提交中/等待运行
    if ([[TASK_STATUS.WAIT_SUBMIT], SUBMITTING_STATUS, WAIT_STATUS].some((collection) => collection.includes(type))) {
        return 'whiteSpace=wrap;fillColor=var(--badge-pending-background);strokeColor=var(--badge-pending-border);';
    }

    // 失败
    if ([FAILED_STATUS, PARENTFAILED_STATUS, RUN_FAILED_STATUS].some((collection) => collection.includes(type))) {
        return 'whiteSpace=wrap;fillColor=var(--badge-failed-background);strokeColor=var(--badge-failed-border);';
    }

    // 冻结/取消
    if ([STOP_STATUS, FROZEN_STATUS].some((collection) => collection.includes(type))) {
        return 'whiteSpace=wrap;fillColor=var(--badge-cancel-background);strokeColor=var(--badge-cancel-border);';
    }

    // 默认
    return 'whiteSpace=wrap;fillColor=var(--badge-common-background);strokeColor=var(--badge-common-border);';
}

function formatJSON(str: string) {
    const jsonObj = JSON.parse(str);
    Object.keys(jsonObj).forEach((key) => {
        if (typeof jsonObj[key] === 'string') {
            try {
                jsonObj[key] = formatJSON(jsonObj[key]);
            } catch {
                // do nothing
            }
        }
    });

    return jsonObj;
}

/**
 * 格式化 JSON 字符串，用于日志输出
 */
export function prettierJSONstring(str: string) {
    try {
        const obj = formatJSON(str);
        return JSON.stringify(obj, null, 2);
    } catch (error) {
        return str;
    }
}

/**
 * 生成 SQL 关键字
 */
export async function createSQLProposals(
    range: languages.CompletionItem['range']
): Promise<languages.CompletionItem[]> {
    return (await Keywords(range)).concat(Snippets(range));
}

/**
 * 获取 13 位的随机 id
 */
export function randomId() {
    return Date.now() + Math.round(Math.random() * 1000);
}

/**
 * 复制操作
 */
export function copyText(text: string) {
    if (navigator.clipboard) {
        // clipboard api 复制
        navigator.clipboard.writeText(text);
    } else {
        const textarea = document.createElement('textarea');
        document.body.appendChild(textarea);
        // 隐藏此输入框
        textarea.style.position = 'fixed';
        textarea.style.clip = 'rect(0 0 0 0)';
        textarea.style.top = '10px';
        // 赋值
        textarea.value = text;
        // 选中
        textarea.select();
        // 复制
        document.execCommand('copy', true);
        // 移除输入框
        document.body.removeChild(textarea);
    }
}

/**
 * 创建timepicker disable区间
 */
export function disableRangeCreater(
    beginDate: moment.Moment | null | undefined,
    endDate: moment.Moment | null | undefined,
    type: 'hour' | 'minute' | 'second',
    isEnd?: boolean
): number[] {
    if (!beginDate || !endDate) {
        return [];
    }
    const nextBeginDate = beginDate.clone();
    const nextEndDate = endDate.clone();
    const compareDate = isEnd ? nextEndDate : nextBeginDate;
    const otherDate = isEnd ? nextBeginDate : nextEndDate;
    let max: number;
    let rangeValue: number;
    switch (type) {
        case 'hour': {
            max = 24;
            compareDate.hours(otherDate.hours());
            rangeValue = otherDate.hours();
            break;
        }
        case 'minute': {
            if (otherDate.hours() !== compareDate.hours()) {
                return [];
            }
            max = 60;
            compareDate.minutes(otherDate.minutes());
            rangeValue = otherDate.minutes();
            break;
        }
        case 'second': {
            if (otherDate.hours() !== compareDate.hours() || otherDate.minutes() !== compareDate.minutes()) {
                return [];
            }
            max = 60;
            compareDate.seconds(otherDate.seconds());
            rangeValue = otherDate.seconds();
            break;
        }
        default:
            break;
    }
    if (isEnd) {
        return lodashRange(compareDate < otherDate ? rangeValue! - 1 : rangeValue!);
    }
    return lodashRange(compareDate > otherDate ? rangeValue! : rangeValue! + 1, max!);
}

/**
 * 生成数字序列
 * @example createSeries(5); // [1, 2, 3, 4, 5]
 */
export function createSeries(num: number) {
    return Array.from(new Array(num).keys()).map((item) => item + 1);
}

/**
 * 基于 text 解析 columns
 * @example
 * ```js
 * getColumnsByColumnsText('id int') // [{field: 'id', type: 'int'}]
 * ```
 */
export function getColumnsByColumnsText(text = '') {
    const columns: { field: string; type: string }[] = [];
    const tmpMap: Record<string, boolean> = {};
    if (text) {
        text.split('\n')
            .filter(Boolean)
            .forEach((v = '') => {
                const asCase = /^.*\w.*\s+as\s+(\w+)$/i.exec(v.trim());
                if (asCase) {
                    if (!tmpMap[asCase[1]]) {
                        tmpMap[asCase[1]] = true;
                        columns.push({
                            field: asCase[1],
                            type: asCase[2],
                        });
                    }
                } else {
                    const [field, type] = v.trim().split(' ');
                    if (!tmpMap[field]) {
                        tmpMap[field] = true;
                        columns.push({ field, type });
                    }
                }
            });
    }
    return columns;
}

/**
 * Get code character by keyCode
 * @notice This is unreliable
 */
export function renderCharacterByCode(keyCode: number) {
    const unicodeCharacter = String.fromCharCode(keyCode);
    if (unicodeCharacter === '\b') return '⌫';
}

/**
 * Put the value into an array unless the value already is an array
 */
export function toArray(value: any) {
    if (Array.isArray(value)) return value;
    return [value];
}

const regex = /({{).+?(}})/s;
/**
 * Convert dynamic params used in dataSync Form
 * @example
 * ```js
 * const values = convertParams({ sourceId: '{{ form#a.b }}', targetId: '{{ form#a.b#toArray }}' }, { a: { b: 1 }});
 * console.log(values); // { sourceId: 1, targetId: [1] }
 * ```
 */
export const convertParams = (params: Record<string, any>, form: Record<string, any>) => {
    return Object.keys(params).reduce<Record<string, any>>((pre, cur) => {
        let value = params[cur];
        if (typeof value === 'string' && regex.test(value)) {
            const content = value.substring(2, value.length - 2);
            const [scope, path, utils] = content.split('#');
            value = get({ form }, `${scope.trim()}.${path.trim()}`);

            if (utils?.trim()) {
                const utilCollection: Record<string, (value: any) => any> = {
                    toArray,
                };
                value = utilCollection[utils.trim()](value);
            }
        }

        pre[cur] = value;
        return pre;
    }, {});
};

/**
 * Advanced get function of lodash
 * @example
 * ```js
 * const values = getPlus({ a: { b: [{ c: 1, value: 100 }]} }, '{{a.b#find.c}}', 100);
 * console.log(values); // 1
 * ```
 */
export function getPlus(obj: Record<string, any>, rawPath: string, value?: any) {
    const path = regex.test(rawPath) ? rawPath.substring(2, rawPath.length - 2) : rawPath;
    const namePath = path.split('.');
    if (namePath.some((p) => p.includes('#'))) {
        const idx = namePath.findIndex((p) => p.includes('#'));
        const firstPath = namePath.slice(0, idx);
        const [idxPath, idxHandler] = namePath[idx].split('#');
        const restPath = namePath.slice(idx + 1);
        const rest = get(obj, firstPath);
        const target = rest[idxPath][idxHandler]((item: any) => item.value === value);
        return get(target, restPath);
    }

    return get(obj, path);
}

/**
 * Convert nested object to form's name path
 * @example
 * ```js
 * const results = convertObjToNamePath({ a: { b: 1 }});
 * console.log(results); // [['a','b'], 1]
 * ```
 */
export function convertObjToNamePath(obj: Record<string, any>) {
    let stack = { ...obj };
    const namePath = [];
    while (typeof stack === 'object' && Object.keys(stack).length) {
        const firstKey = Object.keys(stack)[0];
        namePath.push(firstKey);
        stack = stack[firstKey];
    }

    return [namePath, stack];
}

/**
 * For visiting a tree with children
 * @example
 * ```js
 * visit(tree, (item) => item.type === 1, (item) => {
 * 	// Define the value of nodes whose type equals to 1 to be 1
 * 	item.value = 1;
 * })
 * ```
 */
export function visit<T extends { children: P[]; [key: string]: any }, P extends { type: string; [key: string]: any }>(
    obj: T,
    filter: (item: P) => boolean,
    handler: (item: P, vNode: { formName: string[] }) => void
) {
    const stack = [...obj.children.map((child) => ({ node: child, vNode: { formName: [child.name] } }))];
    while (stack.length) {
        const item = stack.pop()!;
        const isVisit = filter(item.node);
        if (isVisit) {
            handler(item.node, item.vNode);
        }

        if (item.node.type === 'object' && item.node.children.length) {
            stack.push(
                ...item.node.children.map((child: any) => ({
                    node: child,
                    vNode: { formName: [...item.vNode.formName, child.name] },
                }))
            );
        }
    }
}

/**
 * 过滤掉对象中的 `undefined` 和 `null` 的值
 */
export function pickByTruly<T extends Record<string, any>>(obj: T) {
    return pickBy<T>(obj, (val) => val !== undefined && val !== null);
}

/**
 * 将对象按照 keys 数据进行分割
 */
export function splitByKey<T extends Record<string, any>>(obj: T, keys: string[]) {
    return Object.keys(obj).reduce<{ obj1: Partial<T>; obj2: Partial<T> }>(
        (pre, cur) => {
            Object.defineProperty(keys.includes(cur) ? pre.obj1 : pre.obj2, cur, {
                value: obj[cur],
                writable: true,
                enumerable: true,
                configurable: true,
            });

            return pre;
        },
        {
            obj1: {},
            obj2: {},
        }
    );
}

/**
 * 判断数据库的类型是否是 string
 */
export const isValidFormatType = (type: string) => {
    if (!type) return false;
    const typeStr = type.toUpperCase();
    return typeStr === 'STRING' || typeStr === 'VARCHAR' || typeStr === 'VARCHAR2';
};

export function createElement({ className }: { className?: string }) {
    if (className) {
        let dom = document.querySelector(`.${className}`);

        if (!dom) {
            dom = document.createElement('div');
            dom.classList.add(className);
        }

        document.body.appendChild(dom);
        return dom;
    }

    const dom = document.createElement('div');

    document.body.appendChild(dom);
    return dom;
}
