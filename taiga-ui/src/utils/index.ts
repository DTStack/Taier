import { debounce, endsWith } from 'lodash';
import moment from 'moment';
import { createLogger } from 'redux-logger';
import thunkMiddleware from 'redux-thunk';
import { createStore, applyMiddleware, compose } from 'redux';
import {
	CATELOGUE_TYPE,
	ENGINE_SOURCE_TYPE_ENUM,
	MENU_TYPE_ENUM,
	TASK_TYPE_ENUM,
} from '@/constant';
import { Utils } from '@dtinsight/dt-utils';
import { DATA_SOURCE_ENUM, RDB_TYPE_ARRAY } from '@/constant';
import { getCookie } from './operation';
import { CatalogueDataProps } from '@/interface';

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
	// eslint-disable-next-line no-cond-assign
	while ((res = regx.exec(sqlText)) !== null) {
		const name = res[1];
		const param: any = {
			paramName: name,
			paramCommand: '',
		};
		const sysParam = taskCustomParams.find((item: any) => item.paramName === name);
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

export function checkExist(prop: TASK_TYPE_ENUM | string) {
	return prop !== undefined && prop !== null && prop !== '';
}

/**
 * 是否为HDFS类型
 * @param {*} type
 */
export function isHdfsType(type: any) {
	return DATA_SOURCE_ENUM.HDFS === parseInt(type, 10);
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

/**
 * 是否是 Hadoop 引擎
 */
export function isSparkEngine(engineType: numOrStr) {
	return ENGINE_SOURCE_TYPE_ENUM.HADOOP.toString() === engineType.toString();
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
