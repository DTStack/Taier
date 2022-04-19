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

/* eslint-disable no-bitwise */
import { debounce, endsWith } from 'lodash';
import moment from 'moment';
import { createLogger } from 'redux-logger';
import thunkMiddleware from 'redux-thunk';
import { createStore, applyMiddleware, compose } from 'redux';
import {
	FAILED_STATUS,
	FINISH_STATUS,
	FROZEN_STATUS,
	PARENTFAILED_STATUS,
	RUNNING_STATUS,
	RUN_FAILED_STATUS,
	STOP_STATUS,
	SUBMITTING_STATUS,
	TASK_STATUS,
	WAIT_STATUS,
} from '@/constant';
import { CATELOGUE_TYPE, ENGINE_SOURCE_TYPE_ENUM, MENU_TYPE_ENUM } from '@/constant';
import { Utils } from '@dtinsight/dt-utils';
import { DATA_SOURCE_ENUM, RDB_TYPE_ARRAY } from '@/constant';
import type { CatalogueDataProps } from '@/interface';
import { history } from 'umi';
import { openTaskInTab } from '@/extensions/folderTree';
import { updateDrawer } from '@/components/customDrawer';
import type { languages } from '@dtinsight/molecule/esm/monaco';
import { Keywords, Snippets } from './competion';

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
	];
}

export function getCookie(name: string) {
	const arr = document.cookie.match(new RegExp(`(^| )${name}=([^;]*)(;|$)`));
	if (arr != null) {
		return unescape(decodeURI(arr[2]));
	}
	return null;
}

export function deleteCookie(name: string, domain?: string, path: string = '/') {
	const d = new Date(0);
	const cookieDomain = domain ? `; domain=${domain}` : '';
	document.cookie = `${name}=; expires=${d.toUTCString()}${cookieDomain}; path=${path}`;
}

// 请求防抖动
export function debounceEventHander(func: any, wait?: number, options?: any) {
	const debounced = debounce(func, wait, options);
	return (e: any) => {
		e.persist();
		return debounced(e);
	};
}

/**
 * 匹配自定义任务参数
 * @param {Array} taskCustomParams
 * @param {String} sqlText
 */
export function matchTaskParams(taskCustomParams: any[], sqlText: string) {
	const regx = /\$\{([.\w]+)\}/g;
	const data: any[] = [];
	let res = null;
	// eslint-disable-next-line no-cond-assign
	while ((res = regx.exec(sqlText)) !== null) {
		const name = res[1];
		const param: any = {
			paramName: name,
			paramCommand: '',
		};
		const sysParam = taskCustomParams.find((item) => item.paramName === name);
		if (sysParam) {
			param.type = 0;
			param.paramCommand = sysParam.paramCommand;
		} else {
			param.type = 1;
		}
		// 去重
		const exist = data.find((item) => name === item.paramName);
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

export function formJsonValidator(_: any, value: string) {
	let msg = '';
	try {
		if (value) {
			const t = JSON.parse(value);
			if (typeof t !== 'object') {
				msg = '请填写正确的JSON';
			}
		}
	} catch (e) {
		msg = '请检查JSON格式，确认无中英文符号混用！';
	}

	if (msg) {
		return Promise.reject(new Error(msg));
	}
	return Promise.resolve();
}

declare let window: any;

function configureStoreDev(rootReducer: any) {
	const store = createStore(
		rootReducer,
		compose(
			applyMiddleware(thunkMiddleware, createLogger()),
			window.devToolsExtension ? window.devToolsExtension() : (fn: any) => fn,
		),
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
 */
export function getStore(rootReducer: any) {
	const store =
		process.env.NODE_ENV === 'production'
			? configureStoreProd(rootReducer)
			: configureStoreDev(rootReducer);
	return {
		store,
	};
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
				// eslint-disable-next-line no-param-reassign
				parser.index = nextToken;
				// eslint-disable-next-line no-param-reassign
				parser.queue = '';
			} else {
				// eslint-disable-next-line no-param-reassign
				parser.index = sql.length - 1;
				// eslint-disable-next-line no-param-reassign
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
				// eslint-disable-next-line no-param-reassign
				parser.index = end;
				// eslint-disable-next-line no-param-reassign
				parser.queue = '';
			} else {
				parser.comments.push({
					begin,
					end: sql.length - 1,
				});
				// eslint-disable-next-line no-param-reassign
				parser.index = sql.length - 1;
				// eslint-disable-next-line no-param-reassign
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
				// eslint-disable-next-line no-param-reassign
				parser.index = nextToken;
				// eslint-disable-next-line no-param-reassign
				parser.queue = '';
			} else {
				// eslint-disable-next-line no-param-reassign
				parser.index = sql.length - 1;
				// eslint-disable-next-line no-param-reassign
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
export function replaceStrFormIndexArr(str: any, replaceStr: any, indexArr: any) {
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
	let sqls = filterComments(sql);

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

export const queryParse = (url: string) => {
	const search = url.split('?')[1];
	if (!search) return {};
	return search.split('&').reduce((temp, current) => {
		const next = temp;
		const [key, value] = current.split('=');
		next[key] = value;
		return next;
	}, {} as Record<string, string>);
};

export const getTenantId = () => {
	return getCookie(`tenantId`);
};

export const getUserId = () => {
	return getCookie('userId');
};

/**
 * 由于 antd@3 的嵌套表单 name 可以通过 a.b 实现，但是 antd@4 需要通过 [a,b] 所以需要将最终得到的结果做转化
 * @examples
 * ```js
 * // rawValues = { a.b.c: 1, a.b.a: 2};
 * const values = convertToObj(rawValues);
 * // values = { a: { b: {c: 1, a: 2}}};
 * ```
 */
export const convertToObj = (values: Record<string, any>) => {
	const res: Record<string, any> = {};
	Object.keys(values).forEach((keyString) => {
		const keys = keyString.split('.');
		keys.forEach(
			function (this: { res: Record<string, any> }, key, index, thisArr) {
				if (index === thisArr.length - 1) {
					this.res[key] = values[keyString];
				} else if (this.res.hasOwnProperty(key)) {
					this.res = this.res[key];
				} else {
					this.res[key] = {};
					this.res = this.res[key];
				}
			},
			{ res },
		);
	});
	return res;
};

/**
 * 上述方法的逆运算
 */
export const convertToStr = (values: Record<string, any>, prefix = '') => {
	let res: Record<string, any> = {};

	Object.keys(values).forEach((key) => {
		if (typeof values[key] === 'object' && !Array.isArray(values[key])) {
			const obj = convertToStr(values[key], `${prefix ? `${prefix}.` : ''}${key}`);
			res = { ...res, ...obj };
		} else {
			res[`${prefix ? `${prefix}.` : ''}${key}`] = values[key];
		}
	});

	return res;
};

/**
 * 不区分大小写的过滤 value Option
 */
export const filterValueOption = (input: any, option: any) => {
	return option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

/**
 * 遍历树形节点，用新节点替换老节点
 */
export function replaceTreeNode(treeNode: any, replace: any) {
	if (treeNode.id === parseInt(replace.id, 10) && treeNode.type === replace.type) {
		// eslint-disable-next-line no-param-reassign
		treeNode = Object.assign(treeNode, replace);
		return;
	}
	if (treeNode.children) {
		const { children } = treeNode;
		for (let i = 0; i < children.length; i += 1) {
			replaceTreeNode(children[i], replace);
		}
	}
}

/**
 * Get the root folder which distinguished from the source
 * @param data
 * @param source
 * @returns
 */
export function getRootFolderViaSource(data: CatalogueDataProps[], source: CATELOGUE_TYPE) {
	switch (source) {
		case CATELOGUE_TYPE.TASK: {
			return data.find((item) => item.catalogueType === MENU_TYPE_ENUM.TASK);
		}

		case CATELOGUE_TYPE.RESOURCE: {
			return data.find((item) => item.catalogueType === MENU_TYPE_ENUM.RESOURCE);
		}

		case CATELOGUE_TYPE.FUNCTION: {
			return data.find((item) => item.catalogueType === MENU_TYPE_ENUM.FUNCTION);
		}
		default:
			return undefined;
	}
}

function isUtf8(s: string) {
	const lastnames = new Array('ä', 'å', 'æ', 'ç', 'è', 'é');
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
				out += String.fromCharCode(
					((c & 0x0f) << 12) | ((char2 & 0x3f) << 6) | ((char3 & 0x3f) << 0),
				);
				break;
			default:
		}
	}
	return out;
};

export function goToTaskDev(record: { id: string | number; [key: string]: any }) {
	const { id } = record ?? {};
	// Open task in tab
	openTaskInTab(id, { id });
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
 * 从 document.body 隐藏 mxGraph 所产生的 tooltip
 */
export const removeToolTips = () => {
	const remove = () => {
		const tips = document.querySelectorAll<HTMLDivElement>('.mxTooltip');
		if (tips) {
			tips.forEach((o) => {
				// eslint-disable-next-line no-param-reassign
				o.style.visibility = 'hidden';
			});
		}
	};
	setTimeout(remove, 500);
};

/**
 * 从 document.body 隐藏 mxGraph 所产生的 popup
 */
export const removePopUpMenu = () => {
	const remove = () => {
		const tips = document.querySelectorAll<HTMLDivElement>('.mxPopupMenu');
		if (tips) {
			tips.forEach((o) => {
				// eslint-disable-next-line no-param-reassign
				o.style.visibility = 'hidden';
			});
		}
	};
	setTimeout(remove, 500);
};

export function getVertxtStyle(type: TASK_STATUS): string {
	// 成功
	if (FINISH_STATUS.includes(type)) {
		return 'whiteSpace=wrap;fillColor=rgba(18, 188, 106, 0.06);strokeColor=#12bc6a;';
	}

	// 运行中
	if (RUNNING_STATUS.includes(type)) {
		return 'whiteSpace=wrap;fillColor=rgba(18, 188, 106, 0.06);strokeColor=#12bc6a;';
	}

	// 等待提交/提交中/等待运行
	if (
		[[TASK_STATUS.WAIT_SUBMIT], SUBMITTING_STATUS, WAIT_STATUS].some((collection) =>
			collection.includes(type),
		)
	) {
		return 'whiteSpace=wrap;fillColor=#fffbe6;strokeColor=#fdb313;';
	}

	// 失败
	if (
		[FAILED_STATUS, PARENTFAILED_STATUS, RUN_FAILED_STATUS].some((collection) =>
			collection.includes(type),
		)
	) {
		return 'whiteSpace=wrap;fillColor=#fff1f0;strokeColor=#fe615c;';
	}

	// 冻结/取消
	if ([STOP_STATUS, FROZEN_STATUS].some((collection) => collection.includes(type))) {
		return 'whiteSpace=wrap;fillColor=#e6e9f2;strokeColor=#5b6da6;';
	}

	// 默认
	return 'whiteSpace=wrap;fillColor=#F3F3F3;strokeColor=#D4D4D4;';
}

/**
 * 是否是 Hadoop 引擎
 */
export function isSparkEngine(engineType?: numOrStr) {
	return ENGINE_SOURCE_TYPE_ENUM.HADOOP.toString() === engineType?.toString();
}

/**
 * 是否是Libra引擎
 */
export function isLibraEngine(engineType: numOrStr) {
	return ENGINE_SOURCE_TYPE_ENUM.LIBRA.toString() === engineType.toString();
}

/**
 * 是否是TiDB引擎
 */
export function isTiDBEngine(engineType: numOrStr) {
	return ENGINE_SOURCE_TYPE_ENUM.TI_DB.toString() === engineType.toString();
}

/**
 * 是否是 Oracle 引擎
 */
export function isOracleEngine(engineType: numOrStr) {
	return ENGINE_SOURCE_TYPE_ENUM.ORACLE.toString() === engineType.toString();
}

/**
 * 是否是 GreenPlum 引擎
 */
export function isGreenPlumEngine(engineType: numOrStr) {
	return ENGINE_SOURCE_TYPE_ENUM.GREEN_PLUM.toString() === engineType.toString();
}

/**
 * 是否是 K8s 引擎
 */
export function isKubernetesEngine(engineType: numOrStr) {
	return ENGINE_SOURCE_TYPE_ENUM.KUBERNETES.toString() === engineType.toString();
}

/**
 * 是否和账户绑定
 */
export function isBindAccount(engineType: number): boolean {
	return [
		ENGINE_SOURCE_TYPE_ENUM.TI_DB,
		ENGINE_SOURCE_TYPE_ENUM.ORACLE,
		ENGINE_SOURCE_TYPE_ENUM.GREEN_PLUM,
		ENGINE_SOURCE_TYPE_ENUM.ADB,
	].includes(engineType);
}

/**
 * 是否属于关系型数据源
 * @param {*} type
 */
export function isRDB(type: any) {
	return RDB_TYPE_ARRAY.indexOf(parseInt(type, 10)) > -1;
}

/**
 * 是否为HDFS类型
 * @param {*} type
 */
export function isHdfsType(type: DATA_SOURCE_ENUM | undefined) {
	return DATA_SOURCE_ENUM.HDFS === type;
}

/**
 * simply judge whether the array is equal
 * @param arr1
 * @param arr2
 * @returns arr1 === arr2
 */
export function isEqualArr(arr1: string[], arr2: string[]): boolean {
	const toString = JSON.stringify;
	return toString(arr1.sort()) === toString(arr2.sort());
}

function formatJSON(str: string) {
	const standardStr = str.replace(/\n/g, '\\n').replace(/\r/g, '\\r').replace(/\t/g, '\\t');
	const jsonObj = JSON.parse(standardStr);
	Object.keys(jsonObj).forEach((key) => {
		if (typeof jsonObj[key] === 'string') {
			jsonObj[key] = formatJSON(jsonObj[key]);
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
export function createSQLProposals(
	range: languages.CompletionItem['range'],
): languages.CompletionItem[] {
	return Keywords(range).concat(Snippets(range));
}

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

/** 去除不同版本的相同数据源，只保留第一个版本，并使用数据源 groupTag 来代替原来带具体版本号的 name */
export const formatSourceTypes = (
	sourceTypes: { name: string; value: number; groupTag: string }[],
) => {
	if (!sourceTypes.length) {
		return [];
	}
	const result: { name: string; value: number; groupTag: string }[] = [];
	// 因为不同版本的相同数据源是连续的，可以只一次遍历，与上一个比较即可
	for (let i = 0; i < sourceTypes.length; i += 1) {
		if (sourceTypes[i].groupTag !== sourceTypes?.[i - 1]?.groupTag) {
			const temp = { ...sourceTypes[i] };
			temp.name = sourceTypes[i].groupTag;
			result.push(temp);
		}
	}
	return result;
};
