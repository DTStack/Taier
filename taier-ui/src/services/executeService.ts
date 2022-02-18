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

import { Component } from '@dtinsight/molecule/esm/react';
import type { ITaskResultService } from './taskResultService';
import taskResultService from './taskResultService';
import type { CatalogueDataProps, IOfflineTaskProps, IResponseProps } from '@/interface';
import API from '@/api';
import { checkExist } from '@/utils';
import { OFFLINE_TASK_STATUS_FILTERS, TASK_STATUS, TASK_TYPE_ENUM } from '@/constant';
import { createLinkMark, createLog, createTitle } from 'dt-react-codemirror-editor';
import moment from 'moment';

/**
 * 任务执行的结果
 */
interface ITaskExecResultProps {
	/**
	 * 是否需要下载日志
	 * @deprecated 目前不支持下载功能
	 */
	download: null | string;
	isContinue: boolean;
	/**
	 * 需要轮训的接口才有 jobId
	 */
	jobId: null | string;
	/**
	 * 执行异常返回的信息
	 */
	msg: string | null;
	/**
	 * 执行完成后的结果
	 */
	result: null | any;
	/**
	 * 当前执行的 sql 语句
	 */
	sqlText: string;
	status: TASK_STATUS;
	taskType: TASK_TYPE_ENUM;
}

const SELECT_TYPE = {
	SCRIPT: 0, // 脚本
	TASK: 1, // 任务
	COMPONENT: 2, // 组件
};

/**
 * 当前执行的任务属性是把目录获取到的数据和 getTaskById 获取到的数据合并起来
 */
type ITask = CatalogueDataProps & IOfflineTaskProps;

interface IExecuteService {
	/**
	 * 执行 sql 任务
	 * @param currentTabId 当前任务 id
	 * @param task 当前任务
	 * @param rawParams 执行的参数
	 * @param sqls 需要执行的 sql 语句
	 */
	execSql: (
		currentTabId: number,
		task: ITask,
		rawParams: Record<string, any>,
		sqls: string[],
	) => Promise<void>;
	/**
	 * 停止当前执行的 Sql 任务
	 *
	 * 目前执行停止之后还需要继续轮训后端状态，所以停止方法调用成功也不主动执行停止操作，而且根据后续轮训状态来执行停止操作
	 * @param isSilent 静默关闭，不通知任何人（服务器，用户）
	 */
	stopSql: (currentTabId: number, currentTabData: ITask, isSilent: boolean) => Promise<void>;
	/**
	 * 执行数据同步任务
	 */
	execDataSync: (currentTabId: number, params: Record<string, any>) => Promise<boolean>;
	/**
	 * 停止数据同步任务
	 * @param isSilent 静默关闭，不通知任何人（服务器，用户）
	 */
	stopDataSync: (currentTabId: number, isSilent: boolean) => Promise<void>;
}

type IExecuteStates = Record<string, void>;

class ExecuteService extends Component<IExecuteStates> implements IExecuteService {
	private INTERVALS = 1500;
	protected state: IExecuteStates;
	/**
	 * 停止信号量，stop执行成功之后，设置信号量，来让所有正在执行中的网络请求知道任务已经无需再继续
	 */
	protected stopSign: Map<number, boolean>;
	/**
	 * 正在运行中的 jobId，调用stop接口的时候需要使用
	 */
	protected runningSql: Map<number, string>;
	/**
	 * 储存各个tab的定时器id，用来stop任务时候清楚定时任务
	 */
	protected intervalsStore: Map<number, number>;

	private taskResultService: ITaskResultService;

	constructor() {
		super();
		this.state = {};
		this.taskResultService = taskResultService;
		this.stopSign = new Map();
		this.runningSql = new Map();
		this.intervalsStore = new Map();
	}

	public execSql = (
		currentTabId: number,
		task: ITask,
		rawParams: Record<string, any>,
		sqls: string[],
	) => {
		this.stopSign.set(currentTabId, false);
		const key = this.getUniqueKey(task.id);
		const params = {
			...rawParams,
			uniqueKey: key,
		};
		return this.exec(currentTabId, task, params, sqls, 0);
	};

	public stopSql = (currentTabId: number, currentTabData: ITask, isSilent: boolean) => {
		if (isSilent) {
			this.stopSign.set(currentTabId, true);
			this.taskResultService.appendLogs(
				currentTabId.toString(),
				createLog('执行停止', 'warning'),
			);
			if (this.intervalsStore.has(currentTabId)) {
				window.clearTimeout(this.intervalsStore.get(currentTabId));
				this.intervalsStore.delete(currentTabId);
			}
			return Promise.resolve();
		}
		const jobId = this.runningSql.get(currentTabId);
		if (!jobId) return Promise.resolve();

		// 任务执行
		if (checkExist(currentTabData.taskType)) {
			return API.stopSQLImmediately({
				taskId: currentTabData.id,
				jobId,
			});
		}

		// 脚本执行
		if (checkExist(currentTabData.type)) {
			return API.stopScript({
				scriptId: currentTabData.id,
				jobId,
			});
		}

		return Promise.resolve();
	};

	public execDataSync = async (currentTabId: number, params: Record<string, any>) => {
		this.stopSign.set(currentTabId, false);
		this.taskResultService.clearLogs(currentTabId.toString());
		this.taskResultService.appendLogs(
			currentTabId.toString(),
			`同步任务【${params.name}】开始执行`,
		);
		const res: IResponseProps<ITaskExecResultProps> = await API.execDataSyncImmediately(params);
		// 执行结果异常的情况下，存在 message
		if (res && res.code && res.message) {
			this.taskResultService.appendLogs(
				currentTabId.toString(),
				createLog(`${res.message}`, 'error'),
			);
		}
		// 执行结束
		if (!res || (res && res.code !== 1)) {
			this.taskResultService.appendLogs(
				currentTabId.toString(),
				createLog(`请求异常！`, 'error'),
			);
		}
		if (res && res.code === 1) {
			this.taskResultService.appendLogs(
				currentTabId.toString(),
				createLog(`已经成功发送执行请求...`, 'info'),
			);
			if (res.data && res.data.msg) {
				this.taskResultService.appendLogs(
					currentTabId.toString(),
					createLog(`${res.data.msg}`, this.typeCreate(res.data.status)),
				);
			}
			// 存在 jobId 则去轮训接口获取后续任务执行的状态
			if (res.data && res.data.jobId) {
				this.runningSql.set(currentTabId, res.data.jobId);
				return this.selectData(
					res.data.jobId,
					currentTabId,
					{ id: currentTabId, taskType: SELECT_TYPE.TASK },
					TASK_TYPE_ENUM.SYNC,
				);
			}

			this.taskResultService.appendLogs(
				currentTabId.toString(),
				createLog(`执行返回结果异常`, 'error'),
			);
			return true;
		}

		return true;
	};

	public stopDataSync = async (currentTabId: number, isSilent: boolean) => {
		if (isSilent) {
			this.stopSign.set(currentTabId, true);
			this.taskResultService.appendLogs(
				currentTabId.toString(),
				createLog('执行停止', 'warning'),
			);
			if (this.intervalsStore.get(currentTabId)) {
				window.clearTimeout(this.intervalsStore.get(currentTabId));
				this.intervalsStore.delete(currentTabId);
			}
			return;
		}
		const jobId = this.runningSql.get(currentTabId);
		if (!jobId) return;

		const res = await API.stopDataSyncImmediately({ jobId });
		if (res && res.code === 1) {
			this.taskResultService.appendLogs(
				currentTabId.toString(),
				createLog('执行停止', 'warning'),
			);
		}
	};

	private getUniqueKey = (id: number) => `${id}_${moment().valueOf()}`;

	/**
	 * 执行一系列 sql 任务
	 * @param {number} currentTabId 当前任务的 ID
	 * @param {ITask} task 任务对象
	 * @param {Params} params 额外参数
	 * @param {Array} sqls 要执行的Sql数组
	 * @param {Int} index 当前执行的数组下标
	 */
	private exec = (
		currentTabId: number,
		task: ITask,
		rawParams: Record<string, any>,
		sqls: string[],
		index: number,
	) => {
		const params = { ...rawParams };
		params.sql = `${sqls[index]}`;
		params.isEnd = sqls.length === index + 1;
		if (index === 0) {
			// 重置当前任务执行的日志信息
			taskResultService.clearLogs(currentTabId.toString());
		}
		taskResultService.appendLogs(
			currentTabId.toString(),
			createLog(`第${index + 1}条任务开始执行`, 'info'),
		);

		// 任务执行
		if (checkExist(task.taskType)) {
			params.taskId = task.id;
			return API.execSQLImmediately(params)
				.then((res) => this.succCall(res, currentTabId, task))
				.then((res) => {
					// 执行结果正常，才会去判断是否继续后续步骤
					if (res) {
						const isContinue = this.judgeIfContinueExec(sqls, index);
						if (isContinue) {
							// 继续执行之前判断是否停止
							if (this.stopSign.get(currentTabId)) {
								this.stopSign.set(currentTabId, false);
							} else {
								// 继续执行下一条 sql
								this.exec(currentTabId, task, params, sqls, index + 1);
							}
						}
					}
				});
		}

		// 脚本执行
		if (checkExist(task.type)) {
			params.scriptId = task.id;
			return API.execScript(params)
				.then((res) => this.succCall(res, currentTabId, task))
				.then((res) => {
					if (res) {
						const isContinue = this.judgeIfContinueExec(sqls, index);
						if (isContinue) {
							// 继续执行之前判断是否停止
							if (this.stopSign.get(currentTabId)) {
								this.stopSign.set(currentTabId, false);
							} else {
								// 继续执行下一条 sql
								this.exec(currentTabId, task, params, sqls, index + 1);
							}
						}
					}
				});
		}

		// 组件执行
		if (checkExist((task as any).componentType)) {
			params.componentId = task.id;
			params.componentType = (task as any).componentType;
			return API.execComponent(params)
				.then((res) => this.succCall(res, currentTabId, task))
				.then((res) => {
					if (res) {
						const isContinue = this.judgeIfContinueExec(sqls, index);
						if (isContinue) {
							// 继续执行之前判断是否停止
							if (this.stopSign.get(currentTabId)) {
								this.stopSign.set(currentTabId, false);
							} else {
								// 继续执行下一条 sql
								this.exec(currentTabId, task, params, sqls, index + 1);
							}
						}
					}
				});
		}

		return Promise.resolve();
	};

	private typeCreate = (status: TASK_STATUS) =>
		status === TASK_STATUS.RUN_FAILED ? 'error' : 'info';

	/**
	 * 执行 sql 成功后的回调
	 * @returns
	 */
	private succCall = async (
		res: IResponseProps<ITaskExecResultProps>,
		currentTabId: number,
		task: ITask,
	) => {
		// 假如已经是停止状态，则弃用结果
		if (this.stopSign.get(currentTabId)) {
			this.stopSign.set(currentTabId, false);
			return false;
		}

		// 如果接口请求执行失败而非 sql 语句执行失败，需要将执行信息输出到日志 Panel
		if (res && res.code && res.message) {
			taskResultService.appendLogs(
				currentTabId.toString(),
				createLog(`${res.message}`, 'error'),
			);
		}

		// 如果接口请求执行失败
		if (!res || (res && res.code !== 1)) {
			taskResultService.appendLogs(currentTabId.toString(), createLog(`请求异常！`, 'error'));
			return false;
		}

		if (res && res.code === 1) {
			// 执行成功后，将执行后的信息输出到日志 Panel
			if (res.data && res.data.msg) {
				taskResultService.appendLogs(
					currentTabId.toString(),
					createLog(`${res.data.msg}`, this.typeCreate(res.data.status)),
				);
			}

			// 在立即执行 sql 成功后，显示转化之后的任务信息(sqlText)
			if (res.data && res.data.sqlText) {
				taskResultService.appendLogs(
					currentTabId.toString(),
					`${createTitle('任务信息')}\n${res.data.sqlText}\n${createTitle('')}`,
				);
			}

			// 如果存在 jobId，则需要轮训根据 jobId 继续获取后续结果
			if (res.data?.jobId) {
				this.runningSql.set(currentTabId, res.data.jobId);
				if (
					task.taskType === TASK_TYPE_ENUM.ADB ||
					task.taskType === TASK_TYPE_ENUM.IMPALA_SQL ||
					task.taskType === TASK_TYPE_ENUM.INCEPTOR
				) {
					this.getDataOver(currentTabId, res, res.data.jobId);
					return true;
				}

				return this.selectData(res.data.jobId, currentTabId, task);
			}

			// 不存在jobId，则直接返回结果
			this.getDataOver(currentTabId, res);
			return true;
		}

		return false;
	};

	/**
	 * 轮训调用
	 */
	private selectData = (
		jobId: string,
		currentTabId: number,
		task: Pick<ITask, 'id' | 'taskType'>,
		taskType: TASK_TYPE_ENUM = TASK_TYPE_ENUM.SQL,
	) => this.doSelect(jobId, currentTabId, task, taskType);

	/**
	 * 判断是否继续执行
	 * @param sqls 需要执行的全部 sqls 数组
	 * @param index 当前执行的 sql 索引
	 */
	private judgeIfContinueExec = (sqls: string[], index: number) => index < sqls.length - 1;

	/**
	 * 根据不同的状态输出不同的信息，并进行后续的日志输出以及是否进行 download
	 */
	private getDataOver = (
		currentTabId: number,
		res: IResponseProps<ITaskExecResultProps>,
		jobId?: string,
	) => {
		taskResultService.appendLogs(currentTabId.toString(), createLog('执行完成!', 'info'));

		if (res.data?.result) {
			taskResultService.setResult(jobId || currentTabId.toString(), res.data.result);
		}
		if (res.data && res.data.download) {
			taskResultService.appendLogs(
				currentTabId.toString(),
				`完整日志下载地址：${createLinkMark({
					href: res.data.download,
					download: '',
				})}\n`,
			);
		}
	};

	private outputStatus = (currentTabId: number, status: TASK_STATUS, extText?: string) => {
		for (let i = 0; i < OFFLINE_TASK_STATUS_FILTERS.length; i += 1) {
			if (OFFLINE_TASK_STATUS_FILTERS[i].value === status) {
				taskResultService.appendLogs(
					currentTabId.toString(),
					createLog(`${OFFLINE_TASK_STATUS_FILTERS[i].text}${extText || ''}`, 'info'),
				);
			}
		}
	};

	// cancle状态和faild状态(接口正常，业务异常状态)特殊处理
	private abnormal = (currentTabId: number, data: ITaskExecResultProps) => {
		if (data.status) {
			this.outputStatus(currentTabId, data.status);
		}
		if (data.download) {
			taskResultService.appendLogs(
				currentTabId.toString(),
				`完整日志下载地址：${createLinkMark({
					href: data.download,
					download: '',
				})}\n`,
			);
		}
	};

	/**
	 * 展示返回结果中的 message
	 */
	private showMsg = (currentTabId: number, res: any) => {
		if (res.message) {
			taskResultService.appendLogs(
				currentTabId.toString(),
				createLog(`${res.message}`, 'error'),
			);
		}
		if (res.data && res.data.msg) {
			taskResultService.appendLogs(
				currentTabId.toString(),
				createLog(`${res.data.msg}`, this.typeCreate(res.data.status)),
			);
		}
	};

	/**
	 * 数据同步轮训请求
	 */
	private retationRequestSync = (currentTabId: number, data?: ITaskExecResultProps | null) => {
		if (data) {
			// 取消重拾请求，改为一次性请求
			this.abnormal(currentTabId, data);
		}
	};

	/**
	 * 获取日志接口
	 */
	private selectRunLog = (
		currentTabId: number,
		jobId: string,
		task: Pick<ITask, 'id'>,
		type: TASK_TYPE_ENUM,
		num: number = 0,
	) => {
		API.selectRunLog({
			jobId,
			taskId: task.id,
			type,
			sqlId: null,
		}).then((res) => {
			if (this.stopSign.get(currentTabId)) {
				this.stopSign.set(currentTabId, false);
				return false;
			}
			const { code, data, retryLog } = res;
			if (code) {
				this.showMsg(currentTabId, res);
			}
			if (code !== 1) {
				taskResultService.appendLogs(
					currentTabId.toString(),
					createLog(`请求异常！`, 'error'),
				);
			}
			if (data && data.download) {
				taskResultService.appendLogs(
					currentTabId.toString(),
					`完整日志下载地址：${createLinkMark({
						href: res.data.download,
						download: '',
					})}\n`,
				);
			}
			if (retryLog && num && num <= 3) {
				this.selectRunLog(currentTabId, jobId, task, type, num + 1);
				this.outputStatus(
					currentTabId,
					TASK_STATUS.WAIT_RUN,
					`正在进行第${num + 1}次重试，请等候`,
				);
			}
		});
	};

	/**
	 * 获取结果接口
	 */
	private selectExecResultData = (
		currentTabId: number,
		jobId: string,
		task: Pick<ITask, 'id'>,
		/**
		 * 即 SELECT_TYPE
		 */
		type: number,
		taskType: TASK_TYPE_ENUM,
	) => {
		return new Promise<void>((resolve) => {
			API.selectExecResultData(
				{
					jobId,
					taskId: task.id,
					type,
					sqlId: null,
				},
				taskType,
			)
				.then((res: IResponseProps<ITaskExecResultProps>) => {
					if (this.stopSign.get(currentTabId)) {
						this.stopSign.set(currentTabId, false);
						return false;
					}
					if (res && res.code !== 1) {
						taskResultService.appendLogs(
							currentTabId.toString(),
							createLog(`请求异常！`, 'error'),
						);
					} else if (res && res.data?.result) {
						taskResultService.setResult(jobId.toString(), res.data.result);
					}
				})
				.finally(() => {
					resolve();
				});
		});
	};

	/**
	 * 获取执行结果
	 */
	private doSelect = (
		jobId: string,
		currentTabId: number,
		task: Pick<ITask, 'id' | 'taskType'>,
		taskType: TASK_TYPE_ENUM,
	) => {
		const isTask = checkExist(task && task.taskType);
		const type = SELECT_TYPE.TASK;
		// 同步任务轮训结果
		if (taskType && taskType === TASK_TYPE_ENUM.SYNC) {
			return API.selectExecResultDataSync({
				jobId,
				taskId: task.id,
				type: isTask ? SELECT_TYPE.TASK : SELECT_TYPE.SCRIPT,
				sqlId: null,
			}).then((res: IResponseProps<ITaskExecResultProps>) => {
				if (this.stopSign.get(currentTabId)) {
					this.stopSign.set(currentTabId, false);
					return false;
				}
				if (res && res.code) {
					this.showMsg(currentTabId, res);
				}
				// 状态正常
				if (res && res.code === 1 && res.data) {
					switch (res.data.status) {
						case TASK_STATUS.FINISHED: {
							// 成功
							this.getDataOver(currentTabId, res, jobId);
							return true;
						}
						case TASK_STATUS.RUN_FAILED: {
							// 失败时，判断msg字段内部是否包含appLogs标示，若有，则直接展示
							if (res.data.msg && res.data.msg.indexOf('appLogs') > -1) {
								this.abnormal(currentTabId, res.data);
								return true;
							}
							// 否则重新执行该请求
							this.retationRequestSync(currentTabId, res.data);
							return false;
						}
						case TASK_STATUS.STOPED: {
							this.abnormal(currentTabId, res.data);
							return true;
						}
						default: {
							// 正常运行，则再次请求,并记录定时器id
							return new Promise<boolean>((resolve) => {
								const timeout = window.setTimeout(() => {
									// 运行中的数据同步任务不输出日志
									if (res.data!.status !== TASK_STATUS.RUNNING) {
										this.outputStatus(currentTabId, res.data!.status, '.....');
									}
									this.doSelect(jobId, currentTabId, task, taskType).then(
										(success) => {
											resolve(success);
										},
									);
								}, this.INTERVALS);
								this.intervalsStore.set(currentTabId, timeout);
							});
						}
					}
				} else {
					taskResultService.appendLogs(
						currentTabId.toString(),
						createLog(`请求异常！`, 'error'),
					);
					this.retationRequestSync(currentTabId, res.data);
					return false;
				}
			});
		}

		return API.selectStatus({
			jobId,
			taskId: task.id,
			type,
			sqlId: null,
		}).then((res: IResponseProps<ITaskExecResultProps>) => {
			if (this.stopSign.get(currentTabId)) {
				this.stopSign.set(currentTabId, false);
				return false;
			}

			// 状态正常
			if (res && res.data && res.code === 1) {
				switch (res.data.status) {
					case TASK_STATUS.FINISHED: {
						this.outputStatus(currentTabId, res.data.status, '结果获取中，请稍后');
						return this.selectExecResultData(
							currentTabId,
							jobId,
							task,
							type,
							taskType,
						).then(() => {
							this.selectRunLog(currentTabId, jobId, task, type);
							return true;
						});
					}
					case TASK_STATUS.STOPED:
					case TASK_STATUS.RUN_FAILED: {
						this.outputStatus(currentTabId, res.data.status);
						this.selectRunLog(currentTabId, jobId, task, type);
						return false;
					}
					default: {
						// 正常运行，则再次请求,并记录定时器id
						return new Promise<boolean>((resolve) => {
							this.intervalsStore.set(
								currentTabId,
								window.setTimeout(() => {
									this.outputStatus(currentTabId, res.data!.status, '.....');
									this.doSelect(jobId, currentTabId, task, taskType).then(
										(success) => {
											resolve(success);
										},
									);
								}, this.INTERVALS),
							);
						});
					}
				}
			} else {
				taskResultService.appendLogs(
					currentTabId.toString(),
					createLog(`请求异常！`, 'error'),
				);
				if (res.data) {
					this.abnormal(currentTabId, res.data);
				}
				return false;
			}
		});
	};
}

export default new ExecuteService();
