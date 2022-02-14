import { useState, useEffect, useMemo } from 'react';
import { Tooltip, Dropdown, Menu, Modal, message, Button } from 'antd';
import moment from 'moment';
import { history } from 'umi';
import type { ColumnsType } from 'antd/lib/table';
import type { FilterValue } from 'antd/lib/table/interface';
import { SyncOutlined, DownOutlined } from '@ant-design/icons';
import SlidePane from '@/components/slidePane';
import Api from '@/api/operation';
import Sketch, { useSketchRef } from '@/components/sketch';
import type { TASK_PERIOD_ENUM, TASK_TYPE_ENUM } from '@/constant';
import { offlineTaskPeriodFilter } from '@/constant';
import {
	OFFLINE_TASK_STATUS_FILTERS,
	RESTART_STATUS_ENUM,
	STATISTICS_TYPE_ENUM,
	TASK_STATUS,
} from '@/constant';
import { goToTaskDev, removePopUpMenu } from '@/utils';
import { TaskStatus, TaskTimeType, taskTypeText } from '@/utils/enums';
import KillJobForm from './killJobForm';
import TaskJobFlowView from './taskJobFlowView';
import './schedule.scss';

const { confirm, warning } = Modal;

// Form 表单类型
interface IFormFieldProps {
	owner?: number;
	name?: string;
	cycDate?: [moment.Moment, moment.Moment];
}

// 接口请求类型
interface IRequestParams {
	ownerId: string;
	taskName: string;
	cycEndDay: number;
	cycStartDay: number;
	currentPage: number;
	pageSize: number;
	jobStatusList: number[];
	taskPeriodTypeList: number[];
	taskTypeList: number[];
	cycSort: 'desc' | 'asc';
	execEndSort: 'desc' | 'asc';
	execStartSort: 'desc' | 'asc';
	execTimeSort: 'desc' | 'asc';
	retryNumSort: 'desc' | 'asc';
}

// 周期实例类型
export interface IScheduleTaskProps {
	jobId: string;
	cycTime: string;
	endExecTime: string;
	execTime: string;
	ownerId: number;
	ownerName: string;
	periodType: TASK_PERIOD_ENUM;
	retryNum: number;
	startExecTime: string;
	taskId: number;
	taskName: string;
	taskType: TASK_TYPE_ENUM;
	status: TASK_STATUS;
}

export default () => {
	const [statistics, setStatistics] = useState<Record<string, number>>({});
	const [taskTypeFilter, setTaskType] = useState<{ id: number; value: number; text: string }[]>(
		[],
	);
	const [visibleSlidePane, setSlideVisible] = useState(false);
	const [killJobVisible, setKillJobVisible] = useState(false);
	const [selectedTask, setSelectedTask] = useState<IScheduleTaskProps | null>(null);
	const actionRef = useSketchRef();

	const loadJobStatics = (params: Partial<IRequestParams>) => {
		Api.queryJobStatics({
			...params,
			type: STATISTICS_TYPE_ENUM.SCHEDULE,
		}).then((res) => {
			if (res.code === 1) {
				const data: { count: TASK_STATUS; statusKey: string }[] = res.data || [];
				const nextStat = data.reduce((pre, cur) => {
					const next = pre;
					next[cur.statusKey] = cur.count;
					return next;
				}, {} as Record<string, TASK_STATUS>);
				setStatistics(nextStat);
			}
		});
	};

	const getTaskTypesX = () => {
		Api.getTaskTypesX({}).then((res) => {
			if (res.code === 1) {
				const taskTypes: {
					taskTypeCode: number;
					taskTypeName: string;
				}[] = res.data;
				const nextTaskType =
					taskTypes &&
					taskTypes.map((type) => {
						return {
							value: type.taskTypeCode,
							id: type.taskTypeCode,
							text: type.taskTypeName,
						};
					});
				setTaskType(nextTaskType);
			}
		});
	};

	const handleSlideClose = () => {
		setSlideVisible(false);
		setSelectedTask(null);
		removePopUpMenu();
	};

	// 批量按照业务日期杀死任务
	const showKillJobsByDate = (show: boolean) => {
		setKillJobVisible(show);
	};

	// 未运行、成功、失败/上游失败的任务可以reload
	const canReload = (ids: React.Key[]) => {
		if (ids && ids.length > 0) {
			const tasks: IScheduleTaskProps[] = actionRef.current?.getTableData() || [];
			for (let i = 0; i < ids.length; i += 1) {
				const id = ids[i].toString();
				const res = tasks.find((task) => task.taskId.toString() === id);
				if (
					res &&
					res.status !== TASK_STATUS.WAIT_SUBMIT &&
					res.status !== TASK_STATUS.FINISHED &&
					res.status !== TASK_STATUS.RUN_FAILED &&
					res.status !== TASK_STATUS.SUBMIT_FAILED &&
					res.status !== TASK_STATUS.STOPED &&
					res.status !== TASK_STATUS.KILLED &&
					res.status !== TASK_STATUS.PARENT_FAILD
				) {
					return false;
				}
			}
			return true;
		}
	};

	// 是否可以进行kill
	const canKill = (ids: React.Key[]) => {
		if (ids && ids.length > 0) {
			const tasks: IScheduleTaskProps[] = actionRef.current?.getTableData() || [];
			for (let i = 0; i < ids.length; i += 1) {
				const id = ids[i].toString();
				const res = tasks.find((task) => task.taskId.toString() === id);
				if (
					res &&
					res.status !== TASK_STATUS.WAIT_SUBMIT &&
					res.status !== TASK_STATUS.SUBMITTING &&
					res.status !== TASK_STATUS.WAIT_RUN &&
					res.status !== TASK_STATUS.RUNNING
				) {
					return false;
				}
			}
			return true;
		}
	};

	// 批量重跑
	const batchReloadJobs = () => {
		const selected = actionRef.current?.selectedRowKeys || [];
		if (!selected || selected.length <= 0) {
			warning({
				title: '提示',
				content: '您没有选择任何需要重跑的任务！',
			});
			return;
		}

		if (canReload(selected)) {
			confirm({
				title: '确认提示',
				content: '确认需要重跑选择的任务及其全部下游任务？',
				onOk() {
					Api.batchRestartAndResume({
						jobIds: selected,
						restartType: RESTART_STATUS_ENUM.DOWNSTREAM,
					}).then((res) => {
						if (res.code === 1) {
							message.success('已经成功重跑当前选中及其全部下游任务');
							actionRef.current?.setSelectedKeys([]);
							actionRef.current?.submit();
						}
					});
				},
			});
		} else {
			warning({
				title: '提示',
				content: `
                    只有“未运行、成功、失败、取消”状态下的任务可以进行重跑操作，
                    请您重新选择!
                `,
			});
		}
	};

	// 批量杀死
	const batchKillJobs = () => {
		const selected = actionRef.current?.selectedRowKeys || [];
		if (!selected || selected.length <= 0) {
			warning({
				title: '提示',
				content: '您没有选择任何需要杀死的任务！',
			});
			return;
		}

		if (canKill(selected)) {
			confirm({
				title: '确认提示',
				content: '确定要杀死选择的任务？',
				onOk() {
					Api.batchStopJob({
						jobIds: selected,
					}).then((res) => {
						if (res.code === 1) {
							message.success('已经成功杀死所选任务！');
							actionRef.current?.setSelectedKeys([]);
							actionRef.current?.submit();
						}
					});
				},
			});
		} else {
			warning({
				title: '提示',
				content: `
                   “失败、取消、成功、冻结”状态和“已删除”的任务，不能被杀死！
                `,
			});
		}
	};

	// 重跑当前任务
	const reloadCurrentJob = () => {
		const selected = actionRef.current?.selectedRowKeys || [];
		if (!selected || selected.length <= 0) {
			warning({
				title: '提示',
				content: '您没有选择任何需要重跑的任务！',
			});
			return;
		}

		if (canReload(selected)) {
			confirm({
				title: '确认提示',
				content: '确认需要重跑当前选中的任务？',
				onOk() {
					// 接口等待后端
					Api.batchRestartAndResume({
						jobIds: selected,
						restartType: RESTART_STATUS_ENUM.CURRENT,
					}).then((res) => {
						if (res.code === 1) {
							message.success('已经成功重跑当前选中的任务！');
							actionRef.current?.setSelectedKeys([]);
							actionRef.current?.submit();
						}
					});
				},
			});
		} else {
			warning({
				title: '提示',
				content: `
                        只有“未运行、成功、失败、取消”状态下的任务可以进行重跑操作，
                        请您重新选择!
                    `,
			});
		}
	};

	const showTask = (task: IScheduleTaskProps) => {
		setSlideVisible(true);
		setSelectedTask(task);
	};

	useEffect(() => {
		getTaskTypesX();
	}, []);

	const statusList = useMemo(() => {
		const {
			ALL,
			RUNNING,
			UNSUBMIT,
			SUBMITTING,
			WAITENGINE,
			FINISHED,
			CANCELED,
			FROZEN,
			SUBMITFAILD,
			FAILED,
			PARENTFAILED,
		} = statistics;

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
				children: [
					{ title: '等待提交', dataSource: UNSUBMIT },
					{ title: '提交中', dataSource: SUBMITTING },
					{ title: '等待运行', dataSource: WAITENGINE },
				],
			},
			{
				className: 'status_overview_finished_font',
				children: [{ title: '成功', dataSource: FINISHED }],
			},
			{
				className: 'status_overview_grey_font',
				children: [
					{ title: '取消', dataSource: CANCELED },
					{ title: '冻结', dataSource: FROZEN },
				],
			},
			{
				className: 'status_overview_fail_font',
				children: [
					{ title: '提交失败', dataSource: SUBMITFAILD },
					{ title: '运行失败', dataSource: FAILED },
					{ title: '上游失败', dataSource: PARENTFAILED },
				],
			},
		];
	}, [statistics]);

	const columns = useMemo<ColumnsType<IScheduleTaskProps>>(() => {
		return [
			{
				title: '任务名称',
				dataIndex: 'id',
				key: 'id',
				width: 300,
				render: (_, record) => {
					const name = record.taskName;
					const originText = name;
					let showName: React.ReactNode;
					if (
						record.retryNum &&
						[TASK_STATUS.WAIT_RUN, TASK_STATUS.RUNNING].indexOf(record.status) > -1
					) {
						showName = <a onClick={() => showTask(record)}>{name}(重试)</a>;
					} else {
						showName = <a onClick={() => showTask(record)}>{name}</a>;
					}
					return <span title={originText}>{showName}</span>;
				},
				fixed: true,
			},
			{
				title: '状态',
				dataIndex: 'status',
				key: 'status',
				width: 120,
				render: (text) => {
					return <TaskStatus value={text} />;
				},
				filters: OFFLINE_TASK_STATUS_FILTERS,
				filterMultiple: true,
			},
			{
				title: '任务类型',
				dataIndex: 'taskType',
				key: 'taskType',
				render: (text: TASK_TYPE_ENUM) => {
					return taskTypeText(text);
				},
				width: 100,
				filters: taskTypeFilter,
			},
			{
				title: '调度周期',
				dataIndex: 'periodType',
				key: 'periodType',
				render: (text) => {
					return <TaskTimeType value={text} />;
				},
				width: 100,
				filters: offlineTaskPeriodFilter,
			},
			{
				title: '计划时间',
				dataIndex: 'cycTime',
				key: 'cycTime',
				width: 160,
				sorter: true,
			},
			{
				title: '开始时间',
				dataIndex: 'startExecTime',
				key: 'startExecTime',
				width: 160,
				sorter: true,
			},
			{
				title: '结束时间',
				dataIndex: 'endExecTime',
				key: 'endExecTime',
				width: 160,
				sorter: true,
			},
			{
				title: '运行时长',
				dataIndex: 'execTime',
				key: 'execTime',
				width: 130,
				sorter: true,
			},
			{
				title: '重试次数',
				dataIndex: 'retryNum',
				key: 'retryNum',
				width: 110,
				sorter: true,
			},
			{
				title: '责任人',
				dataIndex: 'ownerName',
				key: 'ownerName',
				width: 200,
				fixed: 'right',
			},
		];
	}, [taskTypeFilter]);

	const renderStatus = (list: typeof statusList) => {
		return list.map((item, index) => {
			const { className, children } = item;
			return (
				<span key={index} className={className}>
					{children.map((childItem) => {
						return (
							<span key={childItem.title}>
								{childItem.title}: {childItem.dataSource || 0}
							</span>
						);
					})}
				</span>
			);
		});
	};

	const convertToParams = (values: Partial<IFormFieldProps>) => {
		const params: Partial<IRequestParams> = {
			ownerId: values.owner?.toString(),
			taskName: values.name,
		};

		if (values.cycDate?.[0] && values.cycDate?.[1]) {
			params.cycStartDay = values.cycDate[0].unix();
			params.cycEndDay = values.cycDate[1].unix();
		}

		return params;
	};

	const handleGetTableData = async (
		values: IFormFieldProps,
		{ current, pageSize }: { current: number; pageSize: number },
		filters: Record<string, FilterValue | null>,
		sorter: any,
	) => {
		const params = convertToParams(values);
		const { status = [], periodType = [], taskType = [] } = filters;
		const { field, order } = sorter || {};
		const sortMapping: Record<string, string> = {
			cycTime: 'cycSort',
			startExecTime: 'execEndSort',
			endExecTime: 'execStartSort',
			execTime: 'execTimeSort',
			retryNum: 'retryNumSort',
		};
		const orderMapping: Record<string, 'desc' | 'asc'> = {
			descend: 'desc',
			ascend: 'asc',
		};

		const sortKey = sortMapping[field as string];
		const sortValue = orderMapping[order || 'descend'];

		const queryParams: Partial<IRequestParams> = {
			...params,
			currentPage: current,
			pageSize,
			jobStatusList: (status || []) as number[],
			taskTypeList: (taskType || []) as number[],
			taskPeriodTypeList: (periodType || []) as number[],
			[sortKey]: sortValue,
		};
		return Api.queryJobs(queryParams).then((res) => {
			if (res.code === 1) {
				loadJobStatics(queryParams);
				return {
					total: res.data.totalCount,
					data: res.data.data,
				};
			}
		});
	};

	const handleRefresh = () => {
		actionRef.current?.submit();
	};

	return (
		<div className="c-taskOperation__wrap">
			<Sketch<IScheduleTaskProps, IFormFieldProps>
				actionRef={actionRef}
				header={[
					{
						name: 'input',
						props: {
							formItemProps: {
								initialValue: history.location.query?.tName,
							},
						},
					},
					'owner',
					{
						name: 'rangeDate',
						props: {
							formItemProps: {
								name: 'cycDate',
								label: '计划时间',
							},
							slotProps: {
								showTime: true,
								format: 'YYYY/MM/DD HH:mm:ss',
								ranges: {
									今天: [moment(), moment()],
									最近7天: [moment().subtract(7, 'days'), moment()],
									最近30天: [moment().subtract(30, 'days'), moment()],
								},
							},
						},
					},
				]}
				extra={
					<Tooltip title="刷新数据">
						<Button className="dt-refresh">
							<SyncOutlined onClick={() => handleRefresh()} />
						</Button>
					</Tooltip>
				}
				headerTitle={renderStatus(statusList)}
				headerTitleClassName="ope-statistics"
				request={handleGetTableData}
				columns={columns}
				tableProps={{
					scroll: { x: 1709.6 },
					rowKey: 'jobId',
					rowClassName: (record) => {
						if (selectedTask && selectedTask.jobId === record.jobId) {
							return 'row-select';
						}
						return '';
					},
				}}
				tableFooter={[
					<Dropdown.Button
						key="kill"
						type="primary"
						onClick={batchKillJobs}
						overlay={
							<Menu onClick={() => showKillJobsByDate(true)} style={{ width: 114 }}>
								<Menu.Item key="1">按业务日期杀</Menu.Item>
							</Menu>
						}
						trigger={['click']}
						style={{ marginRight: 10 }}
						icon={<DownOutlined />}
					>
						批量杀任务
					</Dropdown.Button>,
					<Dropdown.Button
						key="reload"
						type="primary"
						onClick={reloadCurrentJob}
						overlay={
							<Menu onClick={() => batchReloadJobs()}>
								<Menu.Item key="1">重跑当前及全部下游任务</Menu.Item>
							</Menu>
						}
						trigger={['click']}
						icon={<DownOutlined />}
					>
						重跑当前任务
					</Dropdown.Button>,
				]}
			/>
			<SlidePane
				className="m-tabs bd-top bd-right m-slide-pane"
				onClose={handleSlideClose}
				visible={visibleSlidePane}
				style={{
					right: '0px',
					width: '60%',
					height: '100%',
					minHeight: '600px',
					position: 'fixed',
					paddingTop: '64px',
					paddingBottom: '22px',
				}}
			>
				<TaskJobFlowView
					visibleSlidePane={visibleSlidePane}
					goToTaskDev={goToTaskDev}
					reload={actionRef.current?.submit}
					taskJob={selectedTask}
				/>
			</SlidePane>

			<KillJobForm
				visible={killJobVisible}
				autoFresh={() => actionRef.current?.submit()}
				onCancel={() => showKillJobsByDate(false)}
			/>
		</div>
	);
};
