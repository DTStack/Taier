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

import * as React from 'react';
import { message, Modal, Button, Popconfirm, Tooltip, Alert, Radio } from 'antd';
import { SyncOutlined, QuestionCircleFilled, ExclamationCircleFilled } from '@ant-design/icons';
import { debounce } from 'lodash';
import { history } from 'umi';
import { Utils, DateTime } from '@dtinsight/dt-utils';
import Sketch, { IActionRef } from '@/components/sketch';
import type { IStreamTaskProps } from '@/interface';
import type { ColumnsType, FilterValue } from 'antd/lib/table/interface';
import { TASK_TYPE_ENUM, FLINK_SQL_TYPE, IForceType } from '@/constant';
import { taskStatusFilter, TASK_STATUS, DATA_SOURCE_ENUM, FLINK_VERSION_TYPE_FILTER } from '@/constant';
import { TaskStatus } from '@/utils/enums';
import { goToTaskDev } from '@/utils';

import DetailPane from './components/detailPane';
import GoOnTask from './components/goOnTask';
import ReRunModal from './components/reRunModal';
import HandTiedModal from './components/handTiedModal';

// TODO
const Api = {
	getRealtimeTaskTypes: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	taskStatistics: () => {
		return new Promise((resolve) => resolve({ code: 11, data: [] }));
	},
	getAllStrategy: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	getTasks: () => {
		return new Promise((resolve) =>
			resolve({
				code: 1,
				data: {
					data: [
						{
							id: 6971,
							taskId: '4i1iuvspof70',
							name: '1_26_eee',
							status: 24,
							strategyName: null,
							taskType: 0,
							createUserName: 'admin@dtstack.com',
							modifyUserName: 'admin@dtstack.com',
							isDirty: null,
							gmtCreate: '2022-02-09T02:39:54.000+00:00',
							gmtModified: '2022-04-15T08:07:27.000+00:00',
							taskDesc: null,
							submitModified: '2022-04-15T08:07:27.000+00:00',
							operateModified: '2022-04-15T08:07:27.000+00:00',
							originSourceType: null,
							execStartTime: null,
							createModel: 1,
							componentVersion: '1.12',
							outputType: 'log',
						},
					],
				},
			}),
		);
	},
	startTask: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	stopTask: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	batchGoONTask: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	batchStopTask: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	roleUserAdmin: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
};

const confirm = Modal.confirm;

const { WEBSOCKET, SOCKET, POSTGRESQL } = DATA_SOURCE_ENUM;

interface IFormFieldProps {
	name?: string;
}

interface IStrategy {
	name: string;
	id: number;
}

class StreamTask extends React.Component<any, any> {
	_timeClock: NodeJS.Timeout | undefined;
	_isUnmounted: boolean | undefined;
	actionRef = React.createRef<IActionRef>();
	state = {
		tasks: {
			data: [],
		},
		filter: {},
		loading: false,
		continue: false,
		reRunModalVisible: false,
		visibleSlidePane: false,
		selectTask: null,
		taskName: Utils.getParameterByName('tname') || '',
		goOnTask: undefined,
		overview: {},
		taskTypes: [],
		sorter: {},
		pageIndex: 1,
		strategyNameFilters: [],
		taskId: undefined,
		__modalKey: null,
		isBatch: false,
		batchReRunVisible: false,
		handTiedModelVisible: false,
		isAdmin: false,
		otherParams: {},
		polling: false,
	};

	componentDidMount() {
		this.loadTaskTypes();
		this.loadCount();
		this.loadStrategy();
		this.isAdminTied();
	}

	loadTaskTypes = () => {
		Api.getRealtimeTaskTypes().then((res: any) => {
			if (res.code === 1) {
				this.setState({
					taskTypes: res.data || [],
				});
			}
		});
	};

	componentWillUnmount() {
		this.clearTimeOut();
		this._isUnmounted = true;
	}

	loadCount() {
		const { taskName, filter } = this.state;
		const { status, taskType, strategyName, componentVersion }: Partial<IStreamTaskProps> =
			filter || {};
		Api.taskStatistics({
			taskName,
			statusList: status,
			type: taskType,
			strategyId: strategyName,
			componentVersion,
		}).then((res: any) => {
			if (res.code == 1) {
				this.setState({
					overview: res?.data?.data,
				});
			}
		});
	}

	loadStrategy = () => {
		Api.getAllStrategy().then((res: any) => {
			if (res?.code !== 1) return;
			const data: IStrategy[] = res?.data || [];
			const strategyNameFilters = data?.map(({ name, id }) => ({
				text: name,
				value: id,
			}));
			strategyNameFilters.unshift({ value: 0, text: '无' });
			this.setState({ strategyNameFilters });
		});
	};

	// 是否为 flinksql 向导模式
	isFlinkSqlGuideMode = (taskData: IStreamTaskProps) => {
		const { taskType, createModel } = taskData;
		return taskType == TASK_TYPE_ENUM.SQL && createModel == FLINK_SQL_TYPE.GUIDE;
	};

	/**
	 * 这里判断是否需要自动刷新，
	 * 当有等待提交之类的状态，则自动刷新
	 */
	debounceLoadtask(data: IStreamTaskProps[]) {
		if (this._isUnmounted) return;
		if (!data) {
			return;
		}
		let haveRun = false;
		const haveRunList = [
			TASK_STATUS.RUNNING,
			TASK_STATUS.STOPING,
			TASK_STATUS.SUBMITTING,
			TASK_STATUS.RESTARTING,
			TASK_STATUS.WAIT_RUN,
			TASK_STATUS.WAIT_COMPUTE,
		];
		for (let i = 0; i < data.length; i++) {
			let status = data[i].status;
			if (haveRunList.indexOf(status) > -1) {
				haveRun = true;
				break;
			}
		}
		if (!haveRun) {
			return;
		}
		this.setState(
			{
				polling: { delay: 5000 },
			},
			this.actionRef.current?.submit,
		);
		this._timeClock = setTimeout(() => {
			this.loadCount();
		}, 5000);
	}

	clearTimeOut = () => {
		if (!this._timeClock) return;
		clearTimeout(this._timeClock);
		this.setState({ polling: false });
	};

	loadTaskList = async (
		values: IFormFieldProps,
		{ current, pageSize }: { current: number; pageSize: number },
		filters: Record<string, FilterValue | null>,
		sorter: any,
	) => {
		const { selectTask, otherParams, filter, visibleSlidePane } = this.state;
		const { status, taskType, strategyName, componentVersion }: Partial<IStreamTaskProps> =
			filter || {};
		const { field, order } = sorter || {};

		const orderMapping: Record<string, string> = {
			gmtModified: 'gmt_modified',
			execStartTime: 'exec_start_time',
		};

		const sortMapping: Record<string, 'desc' | 'asc'> = {
			descend: 'desc',
			ascend: 'asc',
		};

		const reqParams = Object.assign(
			{
				currentPage: current,
				pageSize,
				taskName: values?.name,
				isTimeSortDesc: true,
				statusList: status,
				type: taskType,
				strategyId: strategyName,
				orderBy: orderMapping?.[field],
				sort: sortMapping?.[order],
				componentVersion: componentVersion,
			},
			otherParams,
		);
		this.setState({ loading: true });
		this.clearTimeOut();
		return Api.getTasks(reqParams).then((res: any) => {
			this.setState({ loading: false });
			if (res.code === 1) {
				const selectData = res.data?.data?.find((item: IStreamTaskProps) => {
					return item.id == selectTask;
				});
				this.debounceLoadtask(res.data?.data);
				this.setState({
					pageIndex: current,
					tasks: res?.data,
					visibleSlidePane: !selectData ? false : visibleSlidePane,
					selectTask: !selectData ? null : selectTask,
				});

				return {
					total: 1,
					data: res.data?.data,
				};
			}
		});
	};

	resCallBack = (res: any, msg: string) => {
		const { pageIndex } = this.state;
		if (res?.code !== 1) return;
		message.success(msg);
		this.loadCount();
		this.setState(
			{
				otherParams: {
					pageIndex,
				},
			},
			this.actionRef.current?.submit,
		);
	};

	updateTaskStatus = (task: IStreamTaskProps, mode?: string, isForce?: number) => {
		const { isBatch } = this.state;
		const status = task.status;
		const isRestore =
			status === TASK_STATUS.STOPED ||
			status === TASK_STATUS.FINISHED ||
			status === TASK_STATUS.RUN_FAILED ||
			status === TASK_STATUS.WAIT_SUBMIT ||
			status === TASK_STATUS.SUBMIT_FAILED ||
			status === TASK_STATUS.AUTO_CANCEL
				? 1
				: 0;

		switch (status) {
			case TASK_STATUS.WAIT_SUBMIT:
			case TASK_STATUS.STOPED:
			case TASK_STATUS.RUN_FAILED:
			case TASK_STATUS.KILLED:
			case TASK_STATUS.FINISHED:
			case TASK_STATUS.SUBMIT_FAILED:
			case TASK_STATUS.AUTO_CANCEL: {
				if (
					mode !== 'normal' &&
					(status === TASK_STATUS.STOPED ||
						status === TASK_STATUS.RUN_FAILED ||
						status === TASK_STATUS.FINISHED ||
						status === TASK_STATUS.AUTO_CANCEL)
				) {
					// 续跑
					this.setState({ goOnTask: task.id });
				} else {
					Api.startTask({
						id: task.id,
						isRestoration: isRestore,
					}).then((res: any) => {
						this.resCallBack(res, '任务操作成功！');
					});
				}
				break;
			}
			case TASK_STATUS.RUNNING:
			case TASK_STATUS.SUBMITTING:
			case TASK_STATUS.RESTARTING:
			case TASK_STATUS.WAIT_RUN:
			case TASK_STATUS.WAIT_COMPUTE: {
				Api.stopTask({
					id: task.id,
					isForce,
				}).then((res: any) => {
					this.resCallBack(res, '任务正在停止！');
				});
				break;
			}
		}
		if (isBatch) {
			this.setState({ isBatch: false });
		}
	};

	debounceUpdateTaskStatus = debounce(this.updateTaskStatus, 1000, { maxWait: 5000 });

	recoverTask(task: IStreamTaskProps) {
		const ctx = this;
		const { isBatch } = ctx.state;
		Api.startTask({
			id: task.id,
			isRestoration: 0,
		}).then((res: any) => {
			if (res.code === 1) {
				message.success('任务操作成功！');
				ctx.actionRef.current?.submit();
				ctx.loadCount();
			}
		});
		if (isBatch) {
			ctx.setState({ isBatch: false });
		}
	}

	debounceRecoverTask = debounce(this.recoverTask, 1000, { maxWait: 5000 });

	chooseTask = (record: IStreamTaskProps) => {
		this.setState({
			selectTask: record?.id,
			visibleSlidePane: true,
		});
	};

	closeSlidePane() {
		this.setState({
			visibleSlidePane: false,
			selectTask: null,
		});
	}

	limitMsg = () => {
		message.warning('请至少选择1个任务！');
	};

	batchGoOn = async () => {
		const { selectedRowKeys } = this.actionRef.current!;
		if (selectedRowKeys?.length === 0) {
			this.limitMsg();
			return;
		}
		const res = await Api.batchGoONTask({
			ids: selectedRowKeys,
		});
		if (res?.code === 1) return;
		this.setState(
			{
				isBatch: false,
			},
			() => {
				this.resCallBack(res, '任务操作成功！');
			},
		);
	};

	batchStart = async () => {
		const { batchReRunVisible } = this.state;
		const { selectedRowKeys } = this.actionRef.current!;
		if (selectedRowKeys?.length === 0) {
			this.limitMsg();
			return;
		}
		const res = await Api.batchStartTask({
			ids: selectedRowKeys,
		});
		if (batchReRunVisible) {
			this.setState({
				batchReRunVisible: false,
			});
		}
		if (res?.code !== 1) return;
		this.setState(
			{
				isBatch: false,
			},
			() => {
				this.resCallBack(res, '任务操作成功！');
			},
		);
	};

	batchStop = async (isForce: number) => {
		const { selectedRowKeys } = this.actionRef.current!;
		if (selectedRowKeys?.length === 0) {
			this.limitMsg();
			return;
		}
		const res = await Api.batchStopTask({
			ids: selectedRowKeys,
			isForce,
		});
		if (res?.code !== 1) return;
		this.setState(
			{
				isBatch: false,
			},
			() => {
				this.resCallBack(res, '任务操作成功！');
			},
		);
	};

	initTaskColumns = (): ColumnsType<any> => {
		const { taskTypes, strategyNameFilters } = this.state;
		let taskTypesMap: Record<string, string> = {};
		taskTypes?.forEach((type: { key: string; value: string }) => {
			taskTypesMap[type.key] = type.value;
		});

		return [
			{
				title: '任务名称',
				dataIndex: 'name',
				key: 'name',
				width: 240,
                fixed: 'left',
				render: (text: any, record: any) => {
					return (
						<a
							onClick={() => {
								this.chooseTask(record);
							}}
						>
							{text}
						</a>
					);
				},
			},
			{
				title: '状态',
				dataIndex: 'status',
				key: 'status',
                fixed: 'left',
				render: (text: any) => {
					return <TaskStatus value={text} />;
				},
				filters: taskStatusFilter,
				filterMultiple: true,
			},
			{
				title: '版本',
				dataIndex: 'componentVersion',
				key: 'componentVersion',
				filters: FLINK_VERSION_TYPE,
				filterMultiple: true,
			},
			{
				title: '启停配置',
				dataIndex: 'strategyName',
				key: 'strategyName',
				render: (text: any) => {
					return (
						<Tooltip placement="top" title={text}>
							<span>{Utils.textOverflowExchange(text, 8) || '无'}</span>
						</Tooltip>
					);
				},
				filters: strategyNameFilters,
				filterMultiple: true,
			},
			{
				title: '任务类型',
				dataIndex: 'taskType',
				key: 'taskType',
				render: (text: any) => {
					return taskTypesMap[text];
				},
				filters: taskTypes.map((taskType: any) => {
					return {
						text: taskType.value,
						value: taskType.key,
					};
				}),
				filterMultiple: true,
			},
			{
				title: '责任人',
				dataIndex: 'createUserName',
				key: 'createUserName',
			},
			{
				title: '运行开始时间',
				dataIndex: 'execStartTime',
				key: 'execStartTime',
				render: (text: any) => (text ? DateTime.formatDateTime(text) : '-'),
			},
			{
				title: '最近操作时间',
				dataIndex: 'gmtModified',
				key: 'gmtModified',
				render: (text: any) => (text ? DateTime.formatDateTime(text) : '-'),
				sorter: true,
			},
			{
				title: '最近操作人',
				dataIndex: 'modifyUserName',
				key: 'modifyUserName',
			},
			{
				title: '操作',
				width: 150,
				key: 'operation',
				render: (text: any, record: any) => {
					return this.getDealButton(record);
				},
			},
		];
	};

	isAdminTied = async () => {
		await Api.roleUserAdmin().then((res: { code: number; data: boolean }) => {
			if (res?.code === 1) {
				this.setState({
					isAdmin: res?.data,
				});
			}
		});
	};

	getDealButton(record: IStreamTaskProps | undefined, isPane?: boolean) {
		if (!record) return null;
		const notGoOn = [WEBSOCKET, SOCKET, POSTGRESQL].includes(record.originSourceType);
		let normal = '';
		let recover: React.ReactNode = '';
		let goOn = '';
		let stopping = false;
		let popTxt: React.ReactNode = '确定执行当前操作吗?';
		const openModal = () => {
			const Confirm = confirm({
				title: '是否停止任务',
				content: (
					<div>
						当任务状态过大时，保存SavePoint可能会导致任务停止失败。请确认是否保存SavePoint并停止任务
						<Button
							style={{
								height: 32,
								position: 'absolute',
								right: 160,
								top: '164px',
								padding: '0 15px',
							}}
							onClick={() => {
								Confirm.destroy();
								this.debounceUpdateTaskStatus(record, 'normal', 1);
							}}
						>
							不保存
						</Button>
					</div>
				),
				okText: '确认',
				cancelText: '取消',
				onOk: () => this.debounceUpdateTaskStatus(record, 'normal', 0),
			});
		};

		switch (record.status) {
			case TASK_STATUS.WAIT_SUBMIT:
			case TASK_STATUS.SUBMIT_FAILED:
				normal = '提交';
				break;
			case TASK_STATUS.FINISHED:
				goOn = notGoOn ? '' : '续跑';
				recover = <a>重跑</a>;
				popTxt = (
					<div style={{ width: 200 }}>
						重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停
					</div>
				);
				break;
			case TASK_STATUS.STOPED:
			case TASK_STATUS.KILLED:
				goOn = notGoOn ? '' : '续跑';
				popTxt = (
					<div style={{ width: 200 }}>
						重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停
					</div>
				);
				recover = <a>重跑</a>;
				break;
			case TASK_STATUS.RUN_FAILED:
				goOn = notGoOn ? '' : '续跑';
				normal = '重试';
				break;
			case TASK_STATUS.RUNNING:
			case TASK_STATUS.WAIT_RUN:
			case TASK_STATUS.WAIT_COMPUTE:
			case TASK_STATUS.SUBMITTING:
			case TASK_STATUS.RESTARTING:
				normal = '停止';
				break;
			case TASK_STATUS.STOPING:
				stopping = true;
				break;
			case TASK_STATUS.AUTO_CANCEL:
				goOn = notGoOn ? '' : '续跑';
				normal = '重试';
				break;
			default:
				break;
		}
		if (isPane) {
			return (
				<span className="c-operation__btn">
					<Button type="link" onClick={() => goToTaskDev({ id: record?.id })}>
						修改
					</Button>
					{goOn ? (
						<>
							<span className="ant-divider" />
							<Button
								type="link"
								onClick={() => {
									this.debounceUpdateTaskStatus(record);
								}}
							>
								{goOn}
							</Button>
						</>
					) : null}
					{normal ? (
						normal == '停止' ? (
							<>
								<span className="ant-divider" />
								<Button type="link" onClick={openModal}>
									{normal}
								</Button>
							</>
						) : (
							<>
								<span className="ant-divider" />
								<Button
									type="link"
									onClick={() => {
										this.debounceUpdateTaskStatus(record, 'normal');
									}}
								>
									{normal}
								</Button>
							</>
						)
					) : null}
					{recover ? (
						this.isFlinkSqlGuideMode(record) ? (
							<span>
								<span className="ant-divider" />
								<Button
									type="link"
									onClick={() => {
										this.setState({
											taskId: record.id,
											reRunModalVisible: true,
										});
									}}
								>
									{recover}
								</Button>
							</span>
						) : (
							<Popconfirm
								okText="确定"
								cancelText="取消"
								onConfirm={() => {
									this.debounceRecoverTask(record);
								}}
								title={popTxt}
							>
								<span className="ant-divider" />
								<Button type="link">{recover}</Button>
							</Popconfirm>
						)
					) : null}
					{stopping ? (
						<>
							<span className="ant-divider" />
							<Button type="link" disabled>
								正在停止
							</Button>
						</>
					) : null}
				</span>
			);
		} else {
			let arr: any = [];

			arr.push(
				<a
					key="change"
					onClick={() => {
						goToTaskDev({ id: record?.id });
					}}
				>
					修改
				</a>,
			);
			goOn &&
				arr.push(
					<a
						key="goon"
						onClick={() => {
							this.debounceUpdateTaskStatus(record);
						}}
					>
						{goOn}
					</a>,
				);
			if (normal) {
				if (normal == '停止') {
					arr.push(
						<a key="normal" onClick={openModal}>
							{normal}
						</a>,
					);
				} else {
					arr.push(
						<a
							key="normal"
							onClick={() => {
								this.debounceUpdateTaskStatus(record, 'normal');
							}}
						>
							{normal}
						</a>,
					);
				}
			}
			recover &&
				arr.push(
					this.isFlinkSqlGuideMode(record) ? (
						<span>
							<Button
								style={{ padding: 0 }}
								type="link"
								onClick={() => {
									this.setState({ taskId: record.id, reRunModalVisible: true });
								}}
							>
								{recover}
							</Button>
						</span>
					) : (
						<Popconfirm
							okText="确定"
							cancelText="取消"
							onConfirm={() => {
								this.debounceRecoverTask(record);
							}}
							title={popTxt}
						>
							{recover}
						</Popconfirm>
					),
				);
			stopping &&
				arr.push(
					<span>
						<a style={{ color: '#999' }}>正在停止</a>
					</span>,
				);

			/**
			 * 在每个按钮之间插入间隔符
			 */
			arr = arr.reduce((one: any, two: any) => {
				if (one.length) {
					return one.concat(
						<span key={one.length + 'divider'} className="ant-divider" />,
						two,
					);
				}
				return one.concat(two);
			}, []);

			return <div key={record.id}>{arr}</div>;
		}
	}

	hideGoOnTask = () => {
		this.setState({
			goOnTask: undefined,
		});
	};

	goOnTaskSuccess = () => {
		this.hideGoOnTask();
		this.loadCount();
		this.actionRef.current?.submit();
	};

	handTiedModal = async () => {
		const { selectedRowKeys } = this.actionRef.current!;
		if (selectedRowKeys.length) return this.setState({ handTiedModelVisible: true });
		Modal.warning({
			title: '手动重绑',
			content: '您还未选中任何任务，请选择后重试',
			icon: <QuestionCircleFilled />,
			okText: '关闭',
		});
	};

	handleReTaskRunning = () => {
		const { selectedRowKeys } = this.actionRef.current!;
		if (selectedRowKeys?.length === 0) {
			this.limitMsg();
			return;
		}
		this.setState({ batchReRunVisible: true });
	};

	renderFooter = () => {
		const { isAdmin } = this.state;
		const openModal = () => {
			const Confirm = confirm({
				title: '批量停止任务',
				content: (
					<div>
						此操作执行后不可逆，任务可能取消失败，是否保存SavePoint并停止任务？
						<Button
							style={{
								height: 32,
								position: 'absolute',
								right: 160,
								top: '164px',
								padding: '0 15px',
							}}
							onClick={() => {
								this.batchStop(IForceType.ISFORCE);
								Confirm.destroy();
							}}
						>
							不保存
						</Button>
					</div>
				),
				okText: '确认',
				cancelText: '取消',
				onOk: () => this.batchStop(IForceType.NOTFORCE),
			});
		};

		return [
			<Button key="submit" type="primary" onClick={this.handleReTaskRunning}>
				提交/重跑
			</Button>,
			<Button key="stop" onClick={openModal}>
				停止
			</Button>,
			<Tooltip
				placement="top"
				title="默认从SavePoint恢复，若无可用SavePoint，则选择最近的CheckPoint点位进行续跑"
			>
				<Button key="continue" onClick={this.batchGoOn}>
					续跑
				</Button>
			</Tooltip>,
			isAdmin ? <Button onClick={this.handTiedModal}>手动重绑</Button> : null,
		];
	};

	getStatusList = () => {
		const { overview } = this.state;
		const { ALL, FAILED, RUNNING, CANCELED, UNRUNNING }: Record<string, number> = overview;

		return [
			{
				className: 'status_overview_count_font',
				children: [{ title: '总数', dataSource: ALL }],
			},
			{
				className: 'status_overview_running_font',
				children: [{ title: '运行中', dataSource: RUNNING }],
			},
			{
				className: 'status_overview_yellow_font',
				children: [{ title: '未运行', dataSource: UNRUNNING }],
			},
			{
				className: 'status_overview_fail_font',
				children: [{ title: '失败', dataSource: FAILED }],
			},
			{
				className: 'status_overview_grey_font',
				children: [{ title: '取消', dataSource: CANCELED }],
			},
		];
	};

	renderStatus = () => {
		const statusList = this.getStatusList();
		return statusList.map(({ className, children }, index) => (
			<span key={index} className={className}>
				{children.map(({ title, dataSource }) => (
					<span key={title}>
						{title}: {dataSource || 0}
					</span>
				))}
			</span>
		));
	};

	handleRefresh = () => {
		this.actionRef.current?.submit();
	};

	handleReRunCancel = () => {
		this.setState({
			taskId: '',
			reRunModalVisible: false,
			__modalKey: Math.random(),
		});
	};

	handleFinishTied = () => {
		this.loadCount();
		this.actionRef.current?.submit();
		this.setState({
			handTiedModelVisible: false,
			isBatch: false,
		});
	};

	render() {
		const {
			tasks,
			selectTask,
			batchReRunVisible,
			reRunModalVisible,
			taskId,
			__modalKey,
			handTiedModelVisible,
			polling,
			goOnTask,
			visibleSlidePane,
		} = this.state;
		const { selectedRowKeys = [], submit } = this.actionRef?.current || {};
		const dataSource: IStreamTaskProps[] = tasks?.data || [];
		const detailPaneData =
			dataSource.find((item) => {
				return item.id == selectTask;
			}) || undefined;

		return (
			<div style={{ position: 'relative', height: '100%' }}>
				<Sketch<IStreamTaskProps, IFormFieldProps>
					actionRef={this.actionRef}
					polling={polling}
					header={[
						{
							name: 'input',
							props: {
								formItemProps: {
									initialValue: history.location.query?.tName,
								},
							},
						},
					]}
					extra={
						<Tooltip title="刷新数据">
							<Button className="dt-refresh">
								<SyncOutlined onClick={this.handleRefresh} />
							</Button>
						</Tooltip>
					}
					headerTitle={this.renderStatus()}
					headerTitleClassName="ope-statistics"
					request={this.loadTaskList}
					columns={this.initTaskColumns()}
					tableProps={{
						scroll: { x: 1709.6 },
						rowKey: 'id',
						rowClassName: (record) => {
							if (selectTask === record.id) {
								return 'row-select';
							}
							return '';
						},
					}}
					tableFooter={this.renderFooter()}
				/>
				<GoOnTask
					key={goOnTask}
					visible={!!goOnTask}
					taskId={goOnTask}
					onOk={this.goOnTaskSuccess}
					onCancel={this.hideGoOnTask}
				/>
				<ReRunModal
					key={__modalKey}
					visible={reRunModalVisible}
					taskId={taskId}
					refresh={() => {
						submit?.();
						this.loadCount();
					}}
					onCancel={this.handleReRunCancel}
				/>
				<Modal
					title="批量提交/重跑任务"
					visible={batchReRunVisible}
					onCancel={() => this.setState({ batchReRunVisible: false })}
					onOk={this.batchStart}
					className="o-modal"
				>
					<Alert
						message="重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停"
						type="warning"
					/>
					<Radio.Group defaultValue={0}>
						<Radio className="o-modal__radio" value={0}>
							使用上次任务参数重跑
						</Radio>
						<Radio className="o-modal__radio--padding" value={1} disabled>
							指定Offset Time位置重跑
						</Radio>
						<Tooltip title="仅支持Kafka 0.10版本以上的源表从指定Offset Time开始消费，确定后任务自动保存历史版本并进行重跑">
							<ExclamationCircleFilled />
						</Tooltip>
					</Radio.Group>
				</Modal>
				<DetailPane
					data={detailPaneData}
					visibleSlidePane={visibleSlidePane}
					closeSlidePane={this.closeSlidePane.bind(this)}
					extButton={this.getDealButton(detailPaneData, true)}
				/>
				{handTiedModelVisible && (
					<HandTiedModal
						visible={handTiedModelVisible}
						onCancel={() => this.setState({ handTiedModelVisible: false })}
						selectedRowKeys={selectedRowKeys}
						finishTied={this.handleFinishTied}
					/>
				)}
			</div>
		);
	}
}

export default StreamTask;
