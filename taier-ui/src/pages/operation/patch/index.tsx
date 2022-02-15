import { useMemo } from 'react';
import moment from 'moment';
import { history } from 'umi';
import type { FormInstance } from 'antd';
import { message, Checkbox, Modal } from 'antd';
import Api from '@/api/operation';
import { getCookie } from '@/utils';
import type { ColumnsType } from 'antd/lib/table';
import Sketch from '@/components/sketch';
import { DRAWER_MENU_ENUM } from '@/constant';

const { confirm } = Modal;

const disabledDate = (current: moment.Moment) => {
	return current && current.valueOf() > Date.now();
};

// 补数据实例类型
interface ITasksProps {
	allJobSum: number | null;
	gmtCreate: string;
	doneJobSum: number | null;
	userId: number;
	userName: string;
	fillDataName: string;
	finishedJobSum: number | null;
	fromDay: string;
	id: number;
	toDay: string;
	runDay: string;
}

// 筛选表单类型
interface IFormFieldProps {
	owner?: number;
	name?: string;
	runDay?: moment.Moment | null | undefined;
	checkList?: string[];
}

// 请求类型
interface IRequestParams {
	currentPage: number;
	pageSize: number;
	jobName: string;
	/**
	 * YYYY-MM-dd 类型的日期
	 */
	runDay: string;
	ownerId: number;
}

export default () => {
	const handleKillAllJobs = (job: ITasksProps) => {
		confirm({
			title: '确认提示',
			content: '确定要杀死所有实例？',
			onOk() {
				Api.stopFillDataJobs({
					fillId: job.id,
				}).then((res) => {
					if (res.code === 1) {
						message.success('已成功杀死所有实例！');
					}
				});
			},
		});
	};

	const columns = useMemo<ColumnsType<ITasksProps>>(() => {
		return [
			{
				title: '补数据名称',
				dataIndex: 'fillDataName',
				key: 'fillDataName',
				width: 300,
				render: (text, record) => {
					return (
						<a
							onClick={() => {
								sessionStorage.setItem(
									'task-patch-data',
									JSON.stringify({
										name: record.fillDataName,
										id: record.id,
									}),
								);
								history.push({
									query: {
										drawer: DRAWER_MENU_ENUM.PATCH_DETAIL,
									},
								});
							}}
						>
							{text}
						</a>
					);
				},
			},
			{
				width: 120,
				title: '成功/已完成/总实例',
				dataIndex: 'doneJobSum',
				key: 'doneJobSum',
				render: (_, record) => {
					const isComplete =
						record.finishedJobSum === record.doneJobSum &&
						record.doneJobSum === record.allJobSum;
					const style = isComplete ? { color: '#333333' } : { color: '#EF5350' };
					return (
						<span style={style}>
							{record.finishedJobSum || 0}/{record.doneJobSum || 0}/
							{record.allJobSum || 0}
						</span>
					);
				},
			},
			{
				width: 140,
				title: '计划日期',
				dataIndex: 'fromDay',
				key: 'fromDay',
				render: (_, record) => {
					return (
						<>
							{record.fromDay} ~ {record.toDay}
						</>
					);
				},
			},
			{
				width: 120,
				title: '运行日期',
				dataIndex: 'runDay',
				key: 'runDay',
			},
			{
				width: 120,
				title: '实例生成时间',
				dataIndex: 'gmtCreate',
				key: 'gmtCreate',
			},
			{
				width: 120,
				title: '操作人',
				dataIndex: 'userName',
				key: 'userName',
			},
			{
				width: 120,
				title: '操作',
				dataIndex: 'id',
				fixed: 'right',
				key: 'id',
				render: (_, record) => {
					return <a onClick={() => handleKillAllJobs(record)}>杀死所有实例</a>;
				},
			},
		];
	}, []);

	const convertFormFieldToParams = (values: IFormFieldProps): Partial<IRequestParams> => {
		const { name, owner, runDay } = values;
		return {
			jobName: name,
			runDay: runDay ? moment(runDay).format('YYYY-MM-DD') : undefined,
			ownerId: owner,
		};
	};

	const handleRequestSearch = (
		values: IFormFieldProps,
		{ current, pageSize }: { current: number; pageSize: number },
	) => {
		const params: Partial<IRequestParams> = {
			currentPage: current,
			pageSize,
			...convertFormFieldToParams(values),
		};
		return Api.getFillData(params).then((res) => {
			if (res.code === 1) {
				return {
					total: res.data.totalCount,
					data: res.data.data || [],
				};
			}
		});
	};

	const handleFormFieldChange = (
		field: keyof IFormFieldProps,
		value: any,
		values: IFormFieldProps,
		form: FormInstance<IFormFieldProps>,
	) => {
		const currentUser = Number(getCookie('userId'));
		if (field === 'checkList') {
			const checkListValue = value as string[];
			// 勾选「我今天补的」则修改运行日期为今日，操作人为当前用户
			if (checkListValue.includes('todayUpdate')) {
				form.setFieldsValue({
					owner: currentUser,
					runDay: moment(),
				});
			} else if (checkListValue.includes('person')) {
				// 勾选「我的任务」则修改操作人为当前用户
				form.setFieldsValue({
					owner: currentUser,
				});
			} else {
				// 如果都不勾选
				// 运行日期为今日，则修改运行日期为空
				if (moment().isSame(values.runDay, 'day')) {
					form.setFieldsValue({
						runDay: null,
					});
				}

				// 操作人为当前用户，则修改操作人为空
				if (values.owner === currentUser) {
					form.setFieldsValue({
						owner: undefined,
					});
				}
			}
		}

		if (field === 'owner') {
			const ownerValue = value as number | undefined;

			// 如果操作人选择当前用户，则勾选我的任务
			if (ownerValue === currentUser) {
				form.setFieldsValue({
					checkList: ['person'],
				});
			} else if (values.checkList?.includes('person')) {
				// 如果操作人选择非当前用户，但是「我的任务」处于勾选状态，则取消勾选
				form.setFieldsValue({
					checkList: [],
				});
			}
		}

		if (field === 'runDay') {
			const runDayValue = value as moment.Moment | undefined;
			if (moment().isSame(runDayValue, 'day') && values.owner === currentUser) {
				const nextCheckList = values.checkList?.concat() || [];
				if (!nextCheckList.includes('todayUpdate')) {
					nextCheckList.push('todayUpdate');
				}
				form.setFieldsValue({
					checkList: nextCheckList,
				});
			}
		}
	};

	return (
		<>
			<Sketch<ITasksProps, IFormFieldProps>
				request={handleRequestSearch}
				header={[
					'input',
					{
						name: 'owner',
						props: {
							formItemProps: { label: '操作人' },
							slotProps: {
								placeholder: '请选择操作人',
							},
						},
					},
					{
						name: 'datePicker',
						props: {
							formItemProps: {
								name: 'runDay',
								label: '运行日期',
							},
							slotProps: {
								placeholder: '运行日期',
								disabledDate,
							},
						},
					},
					{
						name: 'checkList',
						renderFormItem: (
							<Checkbox.Group>
								<Checkbox value="person">我的任务</Checkbox>
								<Checkbox value="todayUpdate">我今天补的</Checkbox>
							</Checkbox.Group>
						),
					},
				]}
				onFormFieldChange={handleFormFieldChange}
				columns={columns}
				tableProps={{ rowSelection: undefined, scroll: { x: 920 } }}
			/>
		</>
	);
};
