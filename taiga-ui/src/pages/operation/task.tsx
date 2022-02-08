import { useState, useMemo, useEffect, useRef } from 'react';
import { Checkbox, Tabs, Divider, Button, message } from 'antd';
import type { FormInstance } from 'antd';
import type { ColumnsType } from 'antd/lib/table/interface';
import moment from 'moment';
import SlidePane from '@/components/slidePane';
import API from '@/api/operation';
import type { IActionRef } from '@/components/sketch';
import Sketch from '@/components/sketch';
import type { ITaskBasicProps, ITaskProps } from '@/interface';
import type { TASK_PERIOD_ENUM, TASK_TYPE_ENUM } from '@/constant';
import { offlineTaskPeriodFilter, SCHEDULE_STATUS } from '@/constant';
import { formatDateTime, getCookie } from '@/utils';
import { TaskTimeType, taskTypeText } from '@/utils/enums';
import PatchModal from './patch/patchModal';
import TaskFlowView from './taskFlowView';
import './task.scss';

const { TabPane } = Tabs;

interface IFormFieldProps {
	name?: string;
	owner?: number;
	checkList?: string[];
}

interface IRequestParams {
	currentPage: number;
	pageSize: number;
	name?: string;
	ownerId?: number;
	startModifiedTime?: number;
	endModifiedTime?: number;
	scheduleStatus?: SCHEDULE_STATUS;
	taskTypeList?: number[];
	periodTypeList?: TASK_PERIOD_ENUM[];
}

export default () => {
	const [selectedTask, setSelectedTasks] = useState<ITaskProps | null>(null);
	const [visibleSlidePane, setVisible] = useState(false);
	const [patchDataVisible, setPatchVisible] = useState(false);
	const [patchTargetTask, setPatchTask] = useState<ITaskBasicProps | null>(null);
	const [taskTypeFilter, setTaskFilter] = useState<{ id: number; value: number; text: string }[]>(
		[],
	);
	const actionRef = useRef<IActionRef>(null);

	const convertToParams = (formField: IFormFieldProps) => {
		const params: Partial<IRequestParams> = {
			name: formField.name,
			ownerId: formField.owner,
		};
		if (formField.checkList?.length) {
			formField.checkList.forEach((check) => {
				if (check === 'todayUpdate') {
					params.startModifiedTime =
						moment()
							.set({
								hour: 0,
								minute: 0,
								second: 0,
							})
							.unix() * 1000;
					params.endModifiedTime =
						moment()
							.set({
								hour: 23,
								minute: 59,
								second: 59,
							})
							.unix() * 1000;
				}

				if (check === 'stopped') {
					params.scheduleStatus = SCHEDULE_STATUS.FORZON;
				}
			});
		}

		return params;
	};

	const getTableResult = async (
		params: IFormFieldProps,
		{ current, pageSize }: { current: number; pageSize: number },
		filters: Record<string, any> = {},
	) => {
		const requestParams = convertToParams(params);
		const res = await API.queryOfflineTasks({
			currentPage: current,
			pageSize,
			taskTypeList: filters.taskType || [],
			periodTypeList: filters.taskPeriodId || [],
			...requestParams,
		});
		if (res.code === 1) {
			return {
				total: res.data.totalCount,
				data: res.data.data,
			};
		}
	};

	// 获取任务类型
	const getTaskTypesX = () => {
		API.getTaskTypesX({}).then((res) => {
			if (res.code === 1) {
				const taskTypes: {
					taskTypeCode: number;
					taskTypeName: string;
				}[] = res.data;
				const nextTaskFilter =
					taskTypes &&
					taskTypes.map((type) => {
						return {
							value: type.taskTypeCode,
							id: type.taskTypeCode,
							text: type.taskTypeName,
						};
					});
				setTaskFilter(nextTaskFilter);
			}
		});
	};

	const handleCloseSlidePane = () => {
		setVisible(false);
		setSelectedTasks(null);
	};

	const handleTaskFlowPatchClick = ({ taskId, name }: ITaskBasicProps) => {
		setPatchTask({ taskId, name });
		setPatchVisible(true);
	};

	const handlePatchModalCancel = () => {
		setPatchTask(null);
		setPatchVisible(false);
	};

	// 冻结或解冻
	const forzenTasks = (mode: SCHEDULE_STATUS) => {
		const { selectedRowKeys, setSelectedKeys, submit } = actionRef.current!;
		if (!selectedRowKeys.length) {
			message.error('您没有选择任何任务！');
			return false;
		}
		API.forzenTask({
			taskIdList: selectedRowKeys,
			scheduleStatus: mode,
		}).then((res) => {
			if (res.code === 1) {
				setSelectedKeys([]);
				submit();
			}
		});
	};

	// 冻结或解冻指定任务
	const handleForzonTask = (taskId: number, mode: SCHEDULE_STATUS) => {
		const { submit } = actionRef.current!;
		API.forzenTask({
			taskIdList: [taskId],
			scheduleStatus: mode,
		}).then((res) => {
			if (res.code === 1) {
				submit();
			}
		});
	};

	const showTask = (task: ITaskProps) => {
		setVisible(true);
		setSelectedTasks(task);
	};

	const handleFormValuesChange = (
		field: keyof IFormFieldProps,
		value: any,
		values: IFormFieldProps,
		form: FormInstance,
	) => {
		// 修改责任人需要连带勾选我的任务
		if (field === 'owner') {
			const nextCheckList = (values.checkList || []).concat();
			if (
				value?.toString() === getCookie('dt_user_id') &&
				!nextCheckList.includes('person')
			) {
				nextCheckList.push('person');
				form.setFieldsValue({
					checkList: nextCheckList,
				});
			}

			if (!value && nextCheckList.includes('person')) {
				const index = nextCheckList.indexOf('person');
				nextCheckList.splice(index, 1);
				form.setFieldsValue({
					checkList: nextCheckList,
				});
			}
		}

		// 勾选我的任务，需要连带修改责任人
		if (field === 'checkList') {
			const { owner } = values;
			if (value.includes('person') && owner?.toString() !== getCookie('dt_user_id')) {
				form.setFieldsValue({
					owner: Number(getCookie('dt_user_id')),
				});
			}

			if (!value.includes('person') && owner?.toString() === getCookie('dt_user_id')) {
				form.setFieldsValue({
					owner: undefined,
				});
			}
		}
	};

	useEffect(() => {
		getTaskTypesX();
	}, []);

	const columns = useMemo<ColumnsType<ITaskProps>>(() => {
		return [
			{
				title: '任务名称',
				dataIndex: 'name',
				key: 'name',
				render: (text, record) => {
					const content = (
						<a onClick={() => showTask(record)}>
							{record.name +
								(record.scheduleStatus === SCHEDULE_STATUS.FORZON
									? ' (已冻结)'
									: '')}
						</a>
					);
					return content;
				},
			},
			{
				title: '提交时间',
				dataIndex: 'gmtModified',
				key: 'gmtModified',
				render: (text: string) => {
					return <span>{formatDateTime(text)}</span>;
				},
			},
			{
				title: '任务类型',
				dataIndex: 'taskType',
				key: 'taskType',
				render: (text: TASK_TYPE_ENUM) => {
					return taskTypeText(text);
				},
				filters: taskTypeFilter,
			},
			{
				title: '调度周期',
				dataIndex: 'taskPeriodId',
				key: 'taskPeriodId',
				render: (text) => {
					return <TaskTimeType value={text} />;
				},
				filters: offlineTaskPeriodFilter,
			},
			{
				title: '责任人',
				dataIndex: 'ownerUserName',
				key: 'ownerUserName',
			},
			{
				title: '操作',
				key: 'operation',
				width: 120,
				render: (_, record) => {
					return (
						<span>
							<a onClick={() => handleTaskFlowPatchClick(record)}>补数据</a>
							<Divider type="vertical" />
							<a
								onClick={() => {
									// goToTaskDev(record);
								}}
							>
								修改
							</a>
						</span>
					);
				},
			},
		];
	}, [taskTypeFilter]);

	return (
		<div className="c-taskMana__wrap">
			<Sketch<ITaskProps, IFormFieldProps>
				actionRef={actionRef}
				header={[
					'input',
					'owner',
					{
						name: 'checkList',
						renderFormItem: (
							<Checkbox.Group>
								<Checkbox value="person" className="select-task">
									我的任务
								</Checkbox>
								<Checkbox value="todayUpdate" className="select-task">
									今日修改的任务
								</Checkbox>
								<Checkbox value="stopped" className="select-task">
									冻结的任务
								</Checkbox>
							</Checkbox.Group>
						),
					},
				]}
				request={getTableResult}
				onFormFieldChange={handleFormValuesChange}
				tableFooter={[
					<Button
						key="freeze"
						type="primary"
						onClick={() => forzenTasks(SCHEDULE_STATUS.FORZON)}
					>
						冻结
					</Button>,
					<Button
						key="unfreeze"
						style={{ marginLeft: 15 }}
						onClick={() => forzenTasks(SCHEDULE_STATUS.NORMAL)}
					>
						解冻
					</Button>,
				]}
				columns={columns}
				tableProps={{
					rowKey: 'taskId',
					rowClassName: (record) => {
						if (selectedTask && selectedTask.taskId === record.taskId) {
							return 'row-select';
						}
						return '';
					},
				}}
			/>
			<SlidePane
				onClose={handleCloseSlidePane}
				visible={visibleSlidePane}
				className="dt-slide-pane"
			>
				<Tabs
					className="c-taskMana__slidePane__tabs"
					animated={false}
					tabBarStyle={{ zIndex: 3 }}
				>
					<TabPane tab="依赖视图" key="taskFlow">
						<TaskFlowView
							onForzenTasks={handleForzonTask}
							onPatchData={({ taskId, taskName }) =>
								handleTaskFlowPatchClick({
									taskId,
									name: taskName,
								})
							}
							tabData={selectedTask}
						/>
					</TabPane>
				</Tabs>
			</SlidePane>
			<PatchModal
				visible={patchDataVisible}
				task={patchTargetTask}
				handCancel={handlePatchModalCancel}
			/>
		</div>
	);
};
