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

import { useRef, useState, useMemo, useEffect } from 'react';
import type { ModalProps } from 'antd';
import { message, Modal, Button, Popconfirm, Tooltip, Alert, Radio, Space, Divider } from 'antd';
import { SyncOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { debounce } from 'lodash';
import { history } from 'umi';
import { DateTime } from '@dtinsight/dt-utils';
import type { IActionRef, ISketchProps } from '@/components/sketch';
import Sketch from '@/components/sketch';
import type { IStreamTaskProps } from '@/interface';
import type { ColumnsType, FilterValue } from 'antd/lib/table/interface';
import { TASK_TYPE_ENUM, FLINK_SQL_TYPE, IForceType } from '@/constant';
import {
	TASK_STATUS_FILTERS,
	TASK_STATUS,
	DATA_SOURCE_ENUM,
	FLINK_VERSION_TYPE_FILTER,
} from '@/constant';
import { TaskStatus, taskTypeText } from '@/utils/enums';
import { goToTaskDev } from '@/utils';

import DetailPane from './components/detailPane';
import GoOnTask from './components/goOnTask';
import ReRunModal from './components/reRunModal';
import stream from '@/api/stream';

// TODO
const Api = {
	stopTask: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	batchGoONTask: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
	batchStopTask: () => {
		return new Promise((resolve) => resolve({ code: 1, data: [] }));
	},
};

const { confirm } = Modal;

interface IFormFieldProps {
	name?: string;
}

// 是否为 flinksql 向导模式
function isFlinkSqlGuideMode(taskData: IStreamTaskProps) {
	const { taskType, createModel } = taskData;
	return taskType === TASK_TYPE_ENUM.SQL && createModel === FLINK_SQL_TYPE.GUIDE;
}

export default function StreamTask() {
	const [overview, setOverview] = useState<{
		ALL: number;
		FAILED: number;
		RUNNING: number;
		CANCELED: number;
		UNRUNNING: number;
	}>({ ALL: 0, FAILED: 0, RUNNING: 0, CANCELED: 0, UNRUNNING: 0 });
	const [polling, setPolling] = useState<ISketchProps<any, any>['polling']>(false);
	// 批量提交/重跑
	const [batchReRunVisible, setBatchReRunTaskVisible] = useState(false);
	const [goOnTask, setGoOnTask] = useState<IStreamTaskProps['id'] | undefined>(undefined);
	// 重跑 Modal 信息
	const [reRunInfo, setReRunInfo] = useState<{
		visible: boolean;
		taskId: IStreamTaskProps['id'] | undefined;
	}>({ visible: false, taskId: undefined });
	// 任务详情信息
	const [slidePane, setSlidePane] = useState<{
		visible: boolean;
		selectTask?: IStreamTaskProps;
	}>({ visible: false, selectTask: undefined });
	const actionRef = useRef<IActionRef>(null);

	const assertAtLeastOneTask = () => {
		const { selectedRowKeys } = actionRef.current!;
		if (selectedRowKeys?.length === 0) {
			message.warning('请至少选择1个任务！');
			return false;
		}
		return true;
	};

	const loadCount = (params: any) => {
		stream.getStatusCount(params).then((res) => {
			if (res.code === 1 && res?.data) {
				setOverview(res.data);
			}
		});
	};

	const getStatusList = () => {
		const { ALL, FAILED, RUNNING, CANCELED, UNRUNNING } = overview;

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

	const handleRefresh = () => {
		actionRef.current?.submit();
	};

	const handleReRunCancel = () => {
		setReRunInfo({
			taskId: undefined,
			visible: false,
		});
	};

	const chooseTask = (record: IStreamTaskProps) => {
		setSlidePane({ visible: true, selectTask: record });
	};

	const handleReTaskRunning = () => {
		if (assertAtLeastOneTask()) {
			setBatchReRunTaskVisible(true);
		}
	};

	const closeSlidePane = () => {
		setSlidePane({ visible: false, selectTask: undefined });
	};

	const handleContinueJobInBatch = async () => {
		if (assertAtLeastOneTask()) {
			const { selectedRowKeys } = actionRef.current!;
			const res = await Api.batchGoONTask({
				ids: selectedRowKeys,
			});
			if (res?.code === 1) return;
			message.success('任务操作成功！');
			actionRef.current?.submit();
		}
	};

	const batchStop = async (isForce: IForceType) => {
		if (assertAtLeastOneTask()) {
			const { selectedRowKeys } = actionRef.current!;
			const res = await Api.batchStopTask({
				ids: selectedRowKeys,
				isForce,
			});
			if (res?.code !== 1) return;
			message.success('任务操作成功！');
			actionRef.current?.submit();
		}
	};

	const handleStopTask = () => {
		if (assertAtLeastOneTask()) {
			const confirmInstance = confirm({
				title: '批量停止任务',
				content: (
					<div>
						此操作执行后不可逆，任务可能取消失败，是否保存SavePoint并停止任务？
						<Button
							style={{
								height: 32,
								position: 'absolute',
								right: 168,
								top: 122,
								padding: '0 15px',
							}}
							onClick={() => {
								batchStop(IForceType.ISFORCE);
								confirmInstance.destroy();
							}}
						>
							不保存
						</Button>
					</div>
				),
				okText: '确认',
				cancelText: '取消',
				onOk: () => batchStop(IForceType.NOTFORCE),
			});
		}
	};

	const handleStartJobInBatch = async () => {
		if (assertAtLeastOneTask()) {
			const { selectedRowKeys } = actionRef.current!;
			const res = await Api.batchStartTask({
				ids: selectedRowKeys,
			});
			if (res?.code !== 1) return;
			message.success('任务操作成功！');
			actionRef.current?.submit();
		}
	};

	const handleUpdateTaskStatus = (task: IStreamTaskProps, mode?: string, isForce?: number) => {
		const { status } = task;
		const isRestore = Number(
			status === TASK_STATUS.STOPED ||
				status === TASK_STATUS.FINISHED ||
				status === TASK_STATUS.RUN_FAILED ||
				status === TASK_STATUS.WAIT_SUBMIT ||
				status === TASK_STATUS.SUBMIT_FAILED ||
				status === TASK_STATUS.AUTO_CANCEL,
		);

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
					setGoOnTask(task.id);
				} else {
					const startRequest =
						task.taskType === TASK_TYPE_ENUM.SQL
							? stream.startTask
							: stream.startCollectionTask;
					startRequest({
							id: task.id,
							isRestoration: isRestore,
						})
						.then((res) => {
							if (res.code === 1) {
								if (res.data.status === TASK_STATUS.SUBMITTING) {
									message.success('任务操作成功！');
									actionRef.current?.submit();
								} else {
									message.error(res.data.msg);
								}
							}
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
				}).then(() => {
					message.success('任务正在停止！');
					actionRef.current?.submit();
				});
				break;
			}
			default:
				break;
		}
	};

	const debounceRecoverTask = debounce(
		(task: IStreamTaskProps) => {
			stream
				.startTask({
					taskId: task.id,
					isRestoration: 0,
				})
				.then((res: any) => {
					if (res.code === 1) {
						message.success('任务操作成功！');
						actionRef.current?.submit();
					}
				});
		},
		1000,
		{ maxWait: 5000 },
	);

	/**
	 * 这里判断是否需要自动刷新，
	 * 当有等待提交之类的状态，则自动刷新
	 */
	const shouldRequstPolling = (data?: IStreamTaskProps[]) => {
		if (!data) {
			return;
		}
		const SHOULD_POLLING_STATUS = [
			TASK_STATUS.RUNNING,
			TASK_STATUS.STOPING,
			TASK_STATUS.SUBMITTING,
			TASK_STATUS.RESTARTING,
			TASK_STATUS.WAIT_RUN,
			TASK_STATUS.WAIT_COMPUTE,
		];
		const doPolling = data.some(({ status }) => SHOULD_POLLING_STATUS.includes(status));
		if (doPolling) {
			setPolling(
				(pollingState) =>
					pollingState || {
						delay: 5000,
					},
			);
		} else {
			setPolling(false);
		}
	};

	const goOnTaskSuccess = () => {
		hideGoOnTask();
		actionRef.current?.submit();
	};

	const hideGoOnTask = () => {
		setGoOnTask(undefined);
	};

	const loadTaskList = async (
		values: IFormFieldProps,
		{ current, pageSize }: { current: number; pageSize: number },
		filters: Record<string, FilterValue | null>,
		sorter: any,
	) => {
		const { status, taskType, strategyName, componentVersion } = filters || {};
		const { field, order } = sorter || {};

		const orderMapping: Record<string, string> = {
			gmtModified: 'gmt_modified',
			execStartTime: 'exec_start_time',
		};

		const sortMapping: Record<string, 'desc' | 'asc'> = {
			descend: 'desc',
			ascend: 'asc',
		};

		const reqParams = {
			currentPage: current,
			pageSize,
			taskName: values?.name,
			isTimeSortDesc: true,
			statusList: status,
			type: taskType,
			strategyId: strategyName,
			orderBy: orderMapping[field],
			sort: sortMapping[order],
			componentVersion,
		};
		loadCount({
			taskName: values.name || '',
			statusList: status,
			type: taskType,
			strategyId: strategyName,
			componentVersion,
		});
		return stream.getTaskList(reqParams).then((res) => {
			if (res.code === 1) {
				shouldRequstPolling(res.data?.data);
				return {
					total: 1,
					data: res.data?.data,
				};
			}
		});
	};

	const renderStatus = () => {
		const statusList = getStatusList();
		return statusList.map(({ className, children }) => (
			<span key={className} className={className}>
				{children.map(({ title, dataSource }) => (
					<span key={title}>
						{title}: {dataSource || 0}
					</span>
				))}
			</span>
		));
	};

	const getDealButton = (record?: IStreamTaskProps) => {
		if (!record) return null;
		const notGoOn = [
			DATA_SOURCE_ENUM.WEBSOCKET,
			DATA_SOURCE_ENUM.SOCKET,
			DATA_SOURCE_ENUM.POSTGRESQL,
		].includes(record.originSourceType);
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
								handleUpdateTaskStatus(record, 'normal', 1);
							}}
						>
							不保存
						</Button>
					</div>
				),
				okText: '确认',
				cancelText: '取消',
				onOk: () => handleUpdateTaskStatus(record, 'normal', 0),
			});
		};

		switch (record.status) {
			case TASK_STATUS.WAIT_SUBMIT:
			case TASK_STATUS.SUBMIT_FAILED:
				return (
					<Space size={5} split={<Divider type="vertical" />}>
						<a
							href="#"
							onClick={() => {
								handleUpdateTaskStatus(record);
							}}
						>
							提交
						</a>
						<a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
							修改
						</a>
					</Space>
				);
			case TASK_STATUS.FINISHED:
			case TASK_STATUS.STOPED:
			case TASK_STATUS.KILLED:
				return (
					<Space size={5} split={<Divider type="vertical" />}>
						<a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
							修改
						</a>
						{!notGoOn && (
							<a
								href="#"
								onClick={() => {
									handleUpdateTaskStatus(record);
								}}
							>
								续跑
							</a>
						)}
						{isFlinkSqlGuideMode(record) ? (
							<a
								href="#"
								onClick={() => {
									setReRunInfo({ visible: true, taskId: record.id });
								}}
							>
								重跑
							</a>
						) : (
							<Popconfirm
								okText="确定"
								cancelText="取消"
								onConfirm={() => {
									debounceRecoverTask(record);
								}}
								title={
									<div style={{ width: 200 }}>
										重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停
									</div>
								}
							>
								重跑
							</Popconfirm>
						)}
					</Space>
				);
			case TASK_STATUS.RUN_FAILED:
				return (
					<Space size={5} split={<Divider type="vertical" />}>
						<a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
							修改
						</a>
						{!notGoOn && (
							<a
								href="#"
								onClick={() => {
									handleUpdateTaskStatus(record);
								}}
							>
								续跑
							</a>
						)}
						<a href="#" onClick={() => handleUpdateTaskStatus(record, 'normal')}>
							重试
						</a>
					</Space>
				);
			case TASK_STATUS.RUNNING:
			case TASK_STATUS.WAIT_RUN:
			case TASK_STATUS.WAIT_COMPUTE:
			case TASK_STATUS.SUBMITTING:
			case TASK_STATUS.RESTARTING:
				return (
					<Space size={5} split={<Divider type="vertical" />}>
						<a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
							修改
						</a>
						<a href="#" onClick={openModal}>
							停止
						</a>
					</Space>
				);
			case TASK_STATUS.STOPING:
				return (
					<Space size={5} split={<Divider type="vertical" />}>
						<a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
							修改
						</a>
						<a href="#">正在停止</a>
					</Space>
				);
			case TASK_STATUS.AUTO_CANCEL:
				return (
					<Space size={5} split={<Divider type="vertical" />}>
						<a href="#" onClick={() => goToTaskDev({ id: record?.id })}>
							修改
						</a>
						{!notGoOn && (
							<a
								href="#"
								onClick={() => {
									handleUpdateTaskStatus(record);
								}}
							>
								续跑
							</a>
						)}
						<a href="#" onClick={() => handleUpdateTaskStatus(record, 'normal')}>
							重试
						</a>
					</Space>
				);
			default:
				return null;
		}
	};

	useEffect(() => {
		if (polling) {
			actionRef.current?.submit();
		}
	}, [polling]);

	const tableColumns = useMemo<ColumnsType<IStreamTaskProps>>(
		() => [
			{
				title: '任务名称',
				dataIndex: 'name',
				key: 'name',
				width: 230,
				ellipsis: true,
				fixed: 'left',
				render: (text, record) => {
					return <a onClick={() => chooseTask(record)}>{text}</a>;
				},
			},
			{
				title: '状态',
				dataIndex: 'status',
				key: 'status',
				width: 120,
				fixed: 'left',
				render: (text) => {
					return <TaskStatus value={text} />;
				},
				filters: TASK_STATUS_FILTERS,
				filterMultiple: true,
			},
			{
				title: '版本',
				dataIndex: 'componentVersion',
				width: 80,
				key: 'componentVersion',
				filters: FLINK_VERSION_TYPE_FILTER,
				filterMultiple: true,
			},
			{
				title: '任务类型',
				dataIndex: 'taskType',
				key: 'taskType',
				width: 120,
				render: (text) => taskTypeText(text),
				filters: [TASK_TYPE_ENUM.SQL, TASK_TYPE_ENUM.DATA_ACQUISITION].map((t) => ({
					text: taskTypeText(t),
					value: t,
				})),
				filterMultiple: true,
			},
			{
				title: '责任人',
				dataIndex: 'createUserName',
				width: 150,
				key: 'createUserName',
			},
			{
				title: '运行开始时间',
				dataIndex: 'execStartTime',
				width: 200,
				key: 'execStartTime',
				render: (text) => (text ? DateTime.formatDateTime(text) : '-'),
			},
			{
				title: '最近操作时间',
				dataIndex: 'gmtModified',
				width: 180,
				key: 'gmtModified',
				render: (text) => (text ? DateTime.formatDateTime(text) : '-'),
				sorter: true,
			},
			{
				title: '最近操作人',
				dataIndex: 'modifyUserName',
				width: 150,
				key: 'modifyUserName',
			},
			{
				title: '操作',
				width: 160,
				key: 'operation',
				fixed: 'right',
				render: (_, record) => {
					return getDealButton(record);
				},
			},
		],
		[],
	);

	return (
		<div className="h-full">
			<Sketch<IStreamTaskProps, IFormFieldProps>
				actionRef={actionRef}
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
							<SyncOutlined onClick={handleRefresh} spin={!!polling} />
						</Button>
					</Tooltip>
				}
				headerTitle={renderStatus()}
				headerTitleClassName="ope-statistics"
				request={loadTaskList}
				columns={tableColumns}
				tableFooter={
					<Space>
						<Button key="submit" type="primary" onClick={handleReTaskRunning}>
							提交/重跑
						</Button>
						<Button key="stop" onClick={handleStopTask}>
							停止
						</Button>
						<Tooltip
							placement="top"
							title="默认从SavePoint恢复，若无可用SavePoint，则选择最近的CheckPoint点位进行续跑"
						>
							<Button key="continue" onClick={handleContinueJobInBatch}>
								续跑
							</Button>
						</Tooltip>
					</Space>
				}
			/>
			<BatchReRunModal
				visible={batchReRunVisible}
				onCancel={() => setBatchReRunTaskVisible(false)}
				onOk={handleStartJobInBatch}
			/>
			<DetailPane
				data={slidePane.selectTask}
				visibleSlidePane={slidePane.visible}
				closeSlidePane={closeSlidePane}
				extButton={getDealButton(slidePane.selectTask)}
			/>
			<GoOnTask
				visible={!!goOnTask}
				taskId={goOnTask}
				onOk={goOnTaskSuccess}
				onCancel={hideGoOnTask}
			/>
			<ReRunModal
				visible={reRunInfo.visible}
				taskId={reRunInfo.taskId}
				refresh={handleRefresh}
				onCancel={handleReRunCancel}
			/>
		</div>
	);
}

function BatchReRunModal({ visible, onCancel, onOk }: ModalProps) {
	return (
		<Modal title="批量提交/重跑任务" visible={visible} onCancel={onCancel} onOk={onOk}>
			<Alert
				message="重跑，则任务将丢弃停止前的状态，重新运行，若存在启停策略，将恢复自动启停"
				type="warning"
			/>
			<Radio.Group defaultValue={0}>
				<Space direction="vertical">
					<Radio value={0}>使用上次任务参数重跑</Radio>
					<Radio value={1} disabled>
						指定Offset Time位置重跑
						<Tooltip title="仅支持Kafka 0.10版本以上的源表从指定Offset Time开始消费，确定后任务自动保存历史版本并进行重跑">
							<ExclamationCircleOutlined />
						</Tooltip>
					</Radio>
				</Space>
			</Radio.Group>
		</Modal>
	);
}
