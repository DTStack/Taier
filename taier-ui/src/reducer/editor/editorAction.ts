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

import moment from 'moment';
import API from '@/api';
import { editorAction } from './actionTypes';
import { checkExist } from '@/utils';
import { createLog, createTitle } from 'dt-react-codemirror-editor';
import { TASK_TYPE_ENUM, TASK_STATUS, OFFLINE_TASK_STATUS_FILTERS, SCRIPT_TYPE } from '@/constant';

enum SQLType {
	Normal = 0,
	Advanced = 1,
}
interface SQLJob {
	sqlId: string;
	type: SQLType;
	result: any;
}

const INTERVALS = 1500;

const SELECT_TYPE = {
	SCRIPT: 0, // 脚本
	TASK: 1, // 任务
	COMPONENT: 2, // 组件
};

// 储存各个tab的定时器id，用来stop任务时候清楚定时任务
const intervalsStore: any = {};
// 停止信号量，stop执行成功之后，设置信号量，来让所有正在执行中的网络请求知道任务已经无需再继续
const stopSign: any = {};
// 正在运行中的sql key，调用stop接口的时候需要使用
const runningSql: any = {};

function getUniqueKey(id: any) {
	return `${id}_${moment().valueOf()}`;
}

function typeCreate(status: any) {
	return status === TASK_STATUS.RUN_FAILED ? 'error' : 'info';
}

// 暂时不展示下载的超链接
function createLinkMark(_: any) {
	return '暂不支持下载日志';
}

function getDataOver(
	dispatch: any,
	currentTab: any,
	res: any,
	jobId?: any,
	isAdvancedMode?: boolean,
) {
	// 高级运行需要额外处理finish状态，不能提示用户执行完成，因为无法确认是否后续还有轮训接口请求数据并展示
	// isAdvancedMode： 是否是高级运行，非必填字段，不影响之前逻辑
	if (isAdvancedMode) {
		dispatch(
			output(
				currentTab,
				createLog(res?.data?.msg || '高级运行已完成，正在查询数据中!', 'info'),
			),
		);
		res?.data?.sqlText &&
			dispatch(
				output(
					currentTab,
					`${createTitle('任务信息')}\n${res?.data?.sqlText}\n${createTitle('')}`,
				),
			);
	} else {
		dispatch(output(currentTab, createLog('执行完成!', 'info')));
	}
	jobId &&
		jobId.advancedModeId &&
		res?.data?.sqlText &&
		dispatch(
			output(
				currentTab,
				`${createTitle('任务信息')}\n${res?.data?.sqlText}\n${createTitle('')}`,
			),
		);
	if (res.data.result) {
		dispatch(outputRes(currentTab, res.data.result, jobId));
	}
	if (res.data && res.data.download) {
		dispatch(
			output(
				currentTab,
				`完整日志下载地址：${createLinkMark({
					href: res.data.download,
					download: '',
				})}\n`,
			),
		);
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
function doSelect(
	resolve: any,
	dispatch: any,
	jobId: any,
	currentTab: number,
	task: any,
	taskType: number,
) {
	function outputStatus(status: any, extText?: any) {
		// // 当为数据同步日志时，运行日志就不显示了
		// if (
		// 	taskType === TASK_TYPE_ENUM.SYNC &&
		// 	status === TASK_STATUS.RUNNING
		// ) {
		// 	return;
		// }
		for (let i = 0; i < OFFLINE_TASK_STATUS_FILTERS.length; i++) {
			if (OFFLINE_TASK_STATUS_FILTERS[i].value === status) {
				dispatch(
					output(
						currentTab,
						createLog(`${OFFLINE_TASK_STATUS_FILTERS[i].text}${extText || ''}`, 'info'),
					),
				);
				continue;
			}
		}
	}

	// 数据同步轮训请求
	const retationRequestSync = (data: any) => {
		// 取消重拾请求，改为一次性请求
		data && abnormal(data);
		// data为null时特殊处理，跳出当前运行状态
		resolve(false);
	};

	// cancle状态和faild状态(接口正常，业务异常状态)特殊处理
	const abnormal = (data: any) => {
		data.status && outputStatus(data.status);
		data.download &&
			dispatch(
				output(
					currentTab,
					`完整日志下载地址：${createLinkMark({
						href: data.download,
						download: '',
					})}\n`,
				),
			);
	};

	const showMsg = (res: any) => {
		// 获取到返回值
		if (res.message) {
			dispatch(output(currentTab, createLog(`${res.message}`, 'error')));
		}
		if (res.data && res.data.msg) {
			dispatch(output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))));
		}
	};

	const isTask = checkExist(task && task.taskType);
	const isComponent = checkExist(task && task.componentType);
	let type = SELECT_TYPE.SCRIPT;
	if (isTask) {
		type = SELECT_TYPE.TASK;
	} else if (isComponent) {
		type = SELECT_TYPE.COMPONENT;
	}
	// 获取日志接口
	const selectRunLog = (num: number = 0) => {
		API.selectRunLog({
			jobId: jobId && jobId.jobId ? jobId.jobId : jobId,
			taskId: task.id,
			type: type,
			sqlId: jobId && jobId.advancedModeId ? jobId.advancedModeId : null,
		}).then((res: any) => {
			if (stopSign[currentTab]) {
				stopSign[currentTab] = false;
				resolve(false);
				return;
			}
			const { code, data } = res;
			const { retryLog } = res;
			if (code) showMsg(res);
			if (code !== 1) {
				dispatch(output(currentTab, createLog(`请求异常！`, 'error')));
			}
			if (data && data.download) {
				dispatch(
					output(
						currentTab,
						`完整日志下载地址：${createLinkMark({
							href: res.data.download,
							download: '',
						})}\n`,
					),
				);
			}
			if (retryLog && num && num <= 3) {
				selectRunLog(num + 1);
				outputStatus(TASK_STATUS.WAIT_RUN, `正在进行第${num + 1}次重试，请等候`);
			}
		});
	};
	// 获取结果接口
	const selectExecResultData = () => {
		return new Promise<void>((resultResolve) => {
			API.selectExecResultData(
				{
					jobId: jobId && jobId.jobId ? jobId.jobId : jobId,
					taskId: task.id,
					// type: isTask ? SELECT_TYPE.TASK : SELECT_TYPE.SCRIPT,
					type: type,
					sqlId: jobId && jobId.advancedModeId ? jobId.advancedModeId : null,
				},
				taskType,
			)
				.then((res: any) => {
					if (stopSign[currentTab]) {
						stopSign[currentTab] = false;
						resolve(false);
						return;
					}
					if (res && res.code !== 1) {
						dispatch(output(currentTab, createLog(`请求异常！`, 'error')));
					} else if (res && res.data?.result) {
						dispatch(outputRes(currentTab, res.data.result, jobId));
					}
				})
				.finally(() => {
					resultResolve();
				});
		});
	};

	if (taskType && taskType === TASK_TYPE_ENUM.SYNC) {
		API.selectExecResultDataSync({
			jobId: jobId && jobId.jobId ? jobId.jobId : jobId,
			taskId: task.id,
			type: isTask ? SELECT_TYPE.TASK : SELECT_TYPE.SCRIPT,
			sqlId: jobId && jobId.advancedModeId ? jobId.advancedModeId : null,
		}).then((res: any) => {
			if (stopSign[currentTab]) {
				stopSign[currentTab] = false;
				resolve(false);
				return;
			}
			if (res && res.code) showMsg(res);
			// 状态正常
			if (res && res.code === 1) {
				switch (res.data.status) {
					case TASK_STATUS.FINISHED: {
						// 成功
						getDataOver(dispatch, currentTab, res, jobId);
						resolve(true);
						return;
					}
					case TASK_STATUS.RUN_FAILED: {
						// 失败时，判断msg字段内部是否包含appLogs标示，若有，则直接展示
						if (res.data.msg && res.data.msg.indexOf('appLogs') > -1) {
							abnormal(res.data);
							dispatch(removeLoadingTab(currentTab));
							resolve(true);
							return;
						} else {
							// 否则重新执行该请求
							retationRequestSync(res.data);
							resolve(true);
							return;
						}
					}
					case TASK_STATUS.STOPED: {
						abnormal(res.data);
						dispatch(removeLoadingTab(currentTab));
						resolve(true);
						return;
					}
					default: {
						// 正常运行，则再次请求,并记录定时器id
						intervalsStore[currentTab] = setTimeout(() => {
							outputStatus(res.data.status, '.....');
							doSelect(resolve, dispatch, jobId, currentTab, task, taskType);
						}, INTERVALS);
					}
				}
			} else {
				dispatch(output(currentTab, createLog(`请求异常！`, 'error')));
				retationRequestSync(res.data);
			}
		});
	} else {
		API.selectStatus({
			jobId: jobId && jobId.jobId ? jobId.jobId : jobId,
			taskId: task.id,
			// type: isTask ? SELECT_TYPE.TASK : SELECT_TYPE.SCRIPT,
			type: type,
			sqlId: jobId && jobId.advancedModeId ? jobId.advancedModeId : null,
		}).then((res: any) => {
			if (stopSign[currentTab]) {
				stopSign[currentTab] = false;
				resolve(false);
				return;
			}
			// 状态正常
			if (res && res.code === 1) {
				switch (res.data.status) {
					case TASK_STATUS.FINISHED: {
						// 成功
						outputStatus(res.data.status, '结果获取中，请稍后');
						return selectExecResultData().then(() => {
							selectRunLog();
							return resolve(true);
						});
					}
					case TASK_STATUS.STOPED:
					case TASK_STATUS.RUN_FAILED: {
						outputStatus(res.data.status);
						dispatch(removeLoadingTab(currentTab));
						selectRunLog();
						return;
					}
					default: {
						// 正常运行，则再次请求,并记录定时器id
						intervalsStore[currentTab] = setTimeout(() => {
							outputStatus(res.data.status, '.....');
							doSelect(resolve, dispatch, jobId, currentTab, task, taskType);
						}, INTERVALS);
					}
				}
			} else {
				dispatch(output(currentTab, createLog(`请求异常！`, 'error')));
				res.data && abnormal(res.data);
				// data为null时特殊处理，跳出当前运行状态
				resolve(false);
			}
		});
	}
}

function selectData(
	dispatch: any,
	jobId: any,
	currentTab: number,
	task: any,
	taskType: number = 0,
) {
	return new Promise((resolve: any, reject: any) => {
		doSelect(resolve, dispatch, jobId, currentTab, task, taskType);
	});
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
function exec(
	dispatch: any,
	currentTab: number,
	task: any,
	params: any,
	sqls: any,
	index: any,
	resolve: any,
	reject: any,
) {
	params.sql = `${sqls[index]}`;
	params.isEnd = sqls.length === index + 1;
	if (index === 0) {
		dispatch(setOutput(currentTab, createLog(`第${index + 1}条任务开始执行`, 'info')));
	} else {
		dispatch(output(currentTab, createLog(`第${index + 1}条任务开始执行`, 'info')));
	}
	// 判断是否要继续执行SQL
	function judgeIfContinueExec() {
		if (index < sqls.length - 1) {
			// 剩余任务，则继续执行
			execContinue();
		} else {
			dispatch(removeLoadingTab(currentTab));
			resolve(true);
		}
	}
	function execContinue() {
		if (stopSign[currentTab]) {
			console.log('find stop sign in exec');
			stopSign[currentTab] = false;
			dispatch(removeLoadingTab(currentTab));
			return;
		}
		exec(dispatch, currentTab, task, params, sqls, index + 1, resolve, reject);
	}
	const succCall = (res: any) => {
		// 假如已经是停止状态，则弃用结果
		if (stopSign[currentTab]) {
			console.log('find stop sign in succCall');
			stopSign[currentTab] = false;
			dispatch(removeLoadingTab(currentTab));
			return;
		}
		if (res && res.code && res.message)
			dispatch(output(currentTab, createLog(`${res.message}`, 'error')));
		// 执行结束
		if (!res || (res && res.code !== 1)) {
			dispatch(output(currentTab, createLog(`请求异常！`, 'error')));
			dispatch(removeLoadingTab(currentTab));
			resolve(true);
			return;
		}
		if (res && res.code === 1) {
			if (res.data && res.data.msg)
				dispatch(
					output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))),
				);
			// 在立即执行sql成功后，显示转化之后的任务信息(sqlText)
			if (res.data && res.data.sqlText)
				dispatch(
					output(
						currentTab,
						`${createTitle('任务信息')}\n${res.data.sqlText}\n${createTitle('')}`,
					),
				);
			if (res.data.jobId) {
				runningSql[currentTab] = res.data.jobId;
				if (
					task.taskType === TASK_TYPE_ENUM.ADB ||
					task.taskType === TASK_TYPE_ENUM.IMPALA_SQL ||
					task.type === SCRIPT_TYPE.IMPALA_SQL ||
					task.taskType === TASK_TYPE_ENUM.INCEPTOR
				) {
					getDataOver(dispatch, currentTab, res, res.data.jobId);
					judgeIfContinueExec();
				} else {
					selectData(dispatch, res.data.jobId, currentTab, task).then(
						(isSuccess: any) => {
							if (index < sqls.length - 1 && isSuccess) {
								execContinue();
							} else {
								dispatch(removeLoadingTab(currentTab));
								resolve(true);
							}
						},
					);
				}
			} else {
				// 不存在jobId，则直接返回结果
				getDataOver(dispatch, currentTab, res);
				// 判断是否继续执行
				judgeIfContinueExec();
			}
		}
	};
	if (checkExist(task.taskType)) {
		// 任务执行
		params.taskId = task.id;
		API.execSQLImmediately(params).then(succCall);
	} else if (checkExist(task.type)) {
		// 脚本执行
		params.scriptId = task.id;
		API.execScript(params).then(succCall);
	} else if (checkExist(task.componentType)) {
		// 组件执行
		params.componentId = task.id;
		params.componentType = task.componentType;
		API.execComponent(params).then(succCall);
	}
}

/**
 * SparkSQL 高级模式运行
 * 高级模式下，整个 SQL 会单次发送到服务端，服务端会根据 SQL 分离成 2 种查询类型（0-直接链接数据库查询，1-发送SQL到引擎调度执行）
 * 所以该方法（execSparkSQLAdvancedMode）需要分离出2种不同SQL到结果轮询逻辑。发送执行请求后，服务端会返回一个SQL列表（sqlList),
 * 其中包含了2种SQL查询类型（type), 我们根据次类型做不同到结果轮询，如果是普通查询，则直接打印结果（result), 否则，执行查询（selectData)。
 * 参考文档：https://dtstack.yuque.com/rd-center/batchworks/wrt1ls
 * @param dispatch redux dispatch
 * @param currentTab 当前Tab ID（也就是任务ID）
 * @param task 任务对象
 * @param params 查询参数
 * @param sqls sql数组
 * @param resolve Promise.resolve
 * @param reject Promise.reject
 */
export async function execSparkSQLAdvancedMode(
	dispatch: any,
	currentTab: number,
	task: any,
	params: any,
	sqls: any,
	resolve: any,
	reject: any,
) {
	params.taskId = task.id;
	params.sqlList = sqls;
	dispatch(output(currentTab, createLog(`任务开始执行高级模式`, 'info')));

	const res = await API.execSparkSQLAdvancedMode(params);

	if (res && res.code && res.message)
		dispatch(output(currentTab, createLog(`${res.message}`, 'error')));
	// 执行结束
	if (!res || (res && res.code !== 1)) {
		dispatch(output(currentTab, createLog(`请求异常！`, 'error')));
		dispatch(removeLoadingTab(currentTab));
		return;
	}
	if (res && res.code === 1) {
		if (res.data && res.data.msg)
			dispatch(output(currentTab, createLog(`${res.data.msg}`, typeCreate(res.data.status))));
		// 在立即执行sql成功后，显示转化之后的任务信息(sqlText)
		if (res.data && res.data.sqlText)
			dispatch(
				output(
					currentTab,
					`${createTitle('任务信息')}\n${res.data.sqlText}\n${createTitle('')}`,
				),
			);

		let jobIndex = 0;
		const jobList: SQLJob[] = res.data?.sqlIdList || [];
		runningSql[currentTab] = res.data?.jobId;

		/**
		 * 这个方法有些多余，主要是为了 getDataOver 方法构造一个
		 * 返回结果，否则需要重构 getDataOver 的处理逻辑
		 * @param data 结果数据
		 */
		const wrapResult = (data: any) => {
			return {
				data: {
					...data,
				},
			};
		};
		const ifContinueExec = () => {
			if (jobIndex < jobList.length - 1) {
				handJobRes(++jobIndex);
			} else {
				dispatch(removeLoadingTab(currentTab));
				resolve(true);
			}
		};

		const handJobRes = function (jobIndex: number) {
			// 假如已经是停止状态，则弃用结果
			if (stopSign[currentTab]) {
				stopSign[currentTab] = false;
				dispatch(removeLoadingTab(currentTab));
				resolve(true);
				return;
			}

			const job: SQLJob = jobList[jobIndex];
			/**
			 * 一般 SQL 结果直接输出
			 */
			if (job.type === SQLType.Normal) {
				getDataOver(dispatch, currentTab, wrapResult(job), job.sqlId, true);
				ifContinueExec();
			} else {
				/**
				 *  高级模式SQL需要通过单独轮询查询结果
				 *  需要额外传sqlId在jobId中，这样可以保留代码变动最小，担心sqlId字段存在冲突，所以额外处理为advancedModeId（高级运行id），在作为入参时再改为sqlId
				 */
				const jobId = {
					jobId: res.data?.jobId,
					advancedModeId: job.sqlId,
				};
				selectData(dispatch, jobId, currentTab, task).then((isSuccess) => {
					ifContinueExec();
				});
			}
		};

		if (jobList.length > 0) {
			handJobRes(jobIndex);
		} else {
			// 不存在jobId，则直接返回结果
			getDataOver(dispatch, currentTab, res);
			dispatch(removeLoadingTab(currentTab));
		}
	}
}

// 执行任务
export function execSql(currentTab: any, task: any, params: any, sqls: any) {
	return (dispatch: any) => {
		stopSign[currentTab] = false;
		return new Promise((resolve: any, reject: any) => {
			dispatch(addLoadingTab(currentTab));
			const key = getUniqueKey(task.id);
			params = {
				...params,
				uniqueKey: key,
			};
			// 如果是sparkSQL，运行高级模式，则走单独的执行接口
			if (task.taskType === TASK_TYPE_ENUM.SQL && params.singleSession) {
				execSparkSQLAdvancedMode(dispatch, currentTab, task, params, sqls, resolve, reject);
			} else {
				exec(dispatch, currentTab, task, params, sqls, 0, resolve, reject);
			}
		});
	};
}

// 停止sql
export function stopSql(currentTab: any, currentTabData: any, isSilent: any) {
	return (dispatch: any, getState: any) => {
		// 静默关闭，不通知任何人（服务器，用户）
		if (isSilent) {
			const running = getState().editor.running;
			if (running.indexOf(currentTab) > -1) {
				stopSign[currentTab] = true;
				dispatch(output(currentTab, createLog('执行停止', 'warning')));
				dispatch(removeLoadingTab(currentTab));
				if (intervalsStore[currentTab]) {
					clearTimeout(intervalsStore[currentTab]);
					intervalsStore[currentTab] = null;
				}
				return;
			}
			return;
		}
		const jobId = runningSql[currentTab];
		if (!jobId) return;
		const succCall = (res: any) => {
			/**
			 * 目前执行停止之后还需要继续轮训后端状态，所以停止方法调用成功也不主动执行停止操作，而且根据后续轮训状态来执行停止操作
			 */
		};
		if (checkExist(currentTabData.taskType)) {
			// 任务执行
			API.stopSQLImmediately({
				taskId: currentTabData.id,
				jobId: jobId,
			}).then(succCall);
		} else if (checkExist(currentTabData.type)) {
			// 脚本执行
			API.stopScript({
				scriptId: currentTabData.id,
				jobId: jobId,
			}).then(succCall);
		}
	};
}

/**
 * 执行数据同步任务
 */
export function execDataSync(currentTab: any, params: any) {
	return (dispatch: any) => {
		return new Promise<boolean>(async (resolve) => {
			stopSign[currentTab] = false;
			dispatch(setOutput(currentTab, `同步任务【${params.name}】开始执行`));
			dispatch(addLoadingTab(currentTab));
			const res = await API.execDataSyncImmediately(params);
			if (res && res.code && res.message) {
				dispatch(output(currentTab, createLog(`${res.message}`, 'error')));
			}
			// 执行结束
			if (!res || (res && res.code !== 1)) {
				dispatch(output(currentTab, createLog(`请求异常！`, 'error')));
				dispatch(removeLoadingTab(currentTab));
			}
			if (res && res.code === 1) {
				dispatch(output(currentTab, createLog(`已经成功发送执行请求...`, 'info')));
				if (res.data && res.data.msg)
					dispatch(
						output(
							currentTab,
							createLog(`${res.data.msg}`, typeCreate(res.data.status)),
						),
					);
				if (res.data.jobId) {
					runningSql[currentTab] = res.data.jobId;
					selectData(
						dispatch,
						res.data.jobId,
						currentTab,
						params,
						TASK_TYPE_ENUM.SYNC,
					).then(() => {
						resolve(true);
						dispatch(removeLoadingTab(currentTab));
					});
				} else {
					dispatch(output(currentTab, createLog(`执行返回结果异常`, 'error')));
					dispatch(removeLoadingTab(currentTab));
					resolve(true);
				}
			} else {
				resolve(true);
			}
		});
	};
}

/**
 * 停止数据同步任务
 */
export function stopDataSync(currentTab: any, isSilent: any) {
	return async (dispatch: any, getState: any) => {
		// 静默关闭，不通知任何人（服务器，用户）
		if (isSilent) {
			const running = getState().editor.running;
			if (running.indexOf(currentTab) > -1) {
				stopSign[currentTab] = true;
				dispatch(output(currentTab, createLog('执行停止', 'warning')));
				dispatch(removeLoadingTab(currentTab));
				if (intervalsStore[currentTab]) {
					clearTimeout(intervalsStore[currentTab]);
					intervalsStore[currentTab] = null;
				}
				return;
			}
			return;
		}
		const jobId = runningSql[currentTab];
		if (!jobId) return;

		const res = await API.stopDataSyncImmediately({ jobId: jobId });
		if (res && res.code === 1) {
			dispatch(removeLoadingTab(currentTab));
			dispatch(output(currentTab, createLog('执行停止', 'warning')));
		}
	};
}

// Actions
export function output(tab: any, log: any) {
	return {
		type: editorAction.APPEND_CONSOLE_LOG,
		data: log,
		key: tab,
	};
}

export function setOutput(tab: any, log: any) {
	return {
		type: editorAction.SET_CONSOLE_LOG,
		data: createLog(log, 'info'),
		key: tab,
	};
}

export function outputRes(tab: any, item: any, jobId: any) {
	return {
		type: editorAction.UPDATE_RESULTS,
		data: { jobId: jobId, data: item },
		key: tab,
	};
}

export function removeRes(tab: any, index: any) {
	return {
		type: editorAction.DELETE_RESULT,
		data: index,
		key: tab,
	};
}

export function resetConsole(tab: any) {
	return {
		type: editorAction.RESET_CONSOLE,
		key: tab,
	};
}

/**
 * 初始化tab的console对象
 * @param {tabId} key
 */
export function getTab(key: any) {
	return {
		type: editorAction.GET_TAB,
		key,
	};
}

export function setSelectionContent(data: any) {
	return {
		type: editorAction.SET_SELECTION_CONTENT,
		data,
	};
}

// Loading actions
export function addLoadingTab(id: any) {
	return {
		type: editorAction.ADD_LOADING_TAB,
		data: {
			id: id,
		},
	};
}
export function removeLoadingTab(id: any) {
	return {
		type: editorAction.REMOVE_LOADING_TAB,
		data: {
			id: id,
		},
	};
}
export function removeAllLoadingTab() {
	return {
		type: editorAction.REMOVE_ALL_LOAING_TAB,
	};
}

function themePreChange() {
	const styleDom = document.createElement('style');
	styleDom.type = 'text/css';
	styleDom.innerText = '*{transition-duration: 0s !important;}';
	styleDom.id = 'myThemePreChange';
	document.head.appendChild(styleDom);
	setTimeout(() => {
		document.head.removeChild(styleDom);
	}, 300);
}

export function updateEditorOptions(data: any) {
	if (data.theme) {
		themePreChange();
	}
	return {
		type: editorAction.UPDATE_OPTIONS,
		data,
	};
}

/**
 * 更新右侧面板行为
 * @param {String} showAction 展示行为
 */
export function showRightTablePane() {
	return {
		type: editorAction.SHOW_RIGHT_PANE,
		data: editorAction.SHOW_TABLE_TIP_PANE,
	};
}

export function showRightSyntaxPane() {
	return {
		type: editorAction.SHOW_RIGHT_PANE,
		data: editorAction.SHOW_SYNTAX_HELP_PANE,
	};
}

export function hideRightPane() {
	return {
		type: editorAction.SHOW_RIGHT_PANE,
		data: '',
	};
}

export function updateSyntaxPane(data: any) {
	return {
		type: editorAction.UPDATE_SYNTAX_PANE,
		data: data,
	};
}

export function getEditorThemeClassName(editorTheme: any) {
	// 如果是dark类的编辑器，则切换ide的theme为dark风格
	return editorTheme === 'vs-dark' || editorTheme === 'hc-black' ? 'theme-dark' : 'theme-white';
}
